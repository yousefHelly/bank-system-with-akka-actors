package actors

import akka.actor.{Actor, ActorRef, Props}
import models.{Customer => CustomerType}
import services.Customer.{insertCustomer, retrieveCustomer}
import services.Account.retrieveAccounts

class Customer extends Actor{
  import Customer._
  override def receive: Receive = initialReceive
  private def initialReceive: Receive = {
      case Initialize(customer) => handleGetCustomer(customer)
      case _ => println("I cannot take any actions until I have been initialized as a customer.")
    }
  private def initializedCustomer(customer: CustomerType, accountsCount: Map[String, Int]): Receive = {
    case CreateAccount(bankService, balance) => handleCreateAccount(customer, balance, bankService.path.name, accountsCount)
    case GetMyInfo() => handleGetInfo(customer, accountsCount)
    case Deposit(bankService, amount, accountNumber) => context.child(s"${bankService.path.name}-account-$accountNumber") match {
      case Some(account) => account ! CreateDepositRequest(bankService, amount)
      case None => println("[Error Account Not Found] Can't Make this operation. please initialize yourself as a customer or create a new account!")
    }
    case Withdraw(bankService, amount, accountNumber) => context.child(s"${bankService.path.name}-account-$accountNumber") match {
      case Some(account) => account ! CreateWithdrawRequest(bankService, amount)
      case None => println("[Error Account Not Found] Can't Make this operation. please initialize yourself as a customer or create a new account!")
    }
    case Transfer(bankService, toCustomer, amount, accountNumber, toAccountNumber, toBankService) => context.child(s"${bankService.path.name}-account-${accountNumber}") match {
      case Some(myAccount) =>
        context.system.actorSelection(s"/user/${toCustomer.path.name}/${toBankService.path.name}-account-${toAccountNumber}") ! RequestToReceiver(myAccount, amount)
      case None => println("[Error Account Not Found] Can't Make this operation. please initialize yourself as a customer or create a new account!")
    }
    case GetRecentTransactions(bankService) => bankService ! RecentTransactionsRequest(customer.id)
    case message: String => println(s"[${self.path.name}] I received a message: $message")
  }

  private def handleGetCustomer: CustomerType => Unit = (customer: CustomerType)=>{
    retrieveCustomer(customer.email) match {
      case Some(customerAvailable) =>
        println(s"[${self.path.name}] I've already been initialized as a customer!")
        val accounts = retrieveAccounts(customerAvailable.id)
        accounts match {
          case (accounts, accountsPerBankService) =>
            for {(bankService, account) <- accountsPerBankService} yield {
              val accountsInTheSameBank = accounts.filter((_._2 == bankService))
              (1 to account).zip(accountsInTheSameBank).foreach(i => context.actorOf(Props[Account],s"${bankService}-account-${i._1}") ! HasAccount(customerAvailable.id, i._2._1, bankService))
            }
            context.become(initializedCustomer(customerAvailable, accountsPerBankService))
        }
      case None => insertCustomer(customer) match {
        case num if(num!=0) => println(s"[${self.path.name}] customer initialized successfully!")
          retrieveCustomer(customer.email) match {
            case Some(customer) =>
              context.become(initializedCustomer(customer, Map()))
            case _ =>
          }
        case 0 => println(s"[${self.path.name}] Failed to initialize a customer!")
      }
    }
  }
  private def handleGetInfo = (customer: CustomerType, accountsCount: Map[String, Int]) => {
    println("************** Customer Info. ***************")
    println(s"[${self.path.name}] name: ${customer.name}")
    println(s"[${self.path.name}] email: ${customer.email}")
    println(s"[${self.path.name}] phone: ${customer.phone}")
    println(s"[${self.path.name}] address: ${customer.address}")
    println("************** Customer's Account/s Info. ***************")
    for {(bankService, account) <- accountsCount} yield {
      (1 to account).foreach(i => context.actorSelection(s"${bankService}-account-$i") ! WhatIsMyBalance)
    }
  }
  private def handleCreateAccount = (customer: CustomerType, balance: Double, bankService: String, accountsCount: Map[String, Int])=> {
    val myAccount = context.actorOf(Props[Account], s"${bankService}-account-${accountsCount.getOrElse(bankService,0)+1}")
    myAccount ! InitializeAccount(customer.id, bankService, balance)
    context.become(initializedCustomer(customer, accountsCount.updatedWith(bankService){
      case Some(accounts) => Some(accounts+1)
      case None => Some(1)
    }))
  }
}
object Customer {
  case class Initialize(customer: CustomerType)
  case class CreateAccount(bankService: ActorRef,balance: Double)
  case class InitializeAccount(id: Int, bankService: String, balance: Double)
  case object WhatIsMyBalance
  case class HasAccount(id: Int, accID: Int, BankServiceName: String)
  case class GetMyInfo()
  case class Deposit(bankService: ActorRef, amount: Double, accountNumber: Int=1)
  case class Withdraw(bankService: ActorRef, amount: Double, accountNumber: Int=1)
  case class RequestToReceiver(myAccount: ActorRef, amount: Double)
  case class Transfer(bankService: ActorRef, toCustomer: ActorRef, amount: Double, accountNumber: Int=1, toAccountNumber: Int = 1, toBankService: ActorRef)
  case class CreateDepositRequest(bankService: ActorRef, amount: Double)
  case class CreateWithdrawRequest(bankService: ActorRef, amount: Double)
  case class GetRecentTransactions(bankService: ActorRef)
  case class RecentTransactionsRequest(customerId: Int)
}
