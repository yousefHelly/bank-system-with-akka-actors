import actors.{BankService, Customer}
import actors.Customer.{CreateAccount, Deposit, GetMyInfo, GetRecentTransactions, Initialize, Transfer, Withdraw}
import akka.actor.{Actor, ActorSystem, Props}
import models.{Customer => CustomerType}
object Main extends App {
    val system = ActorSystem("supervisorSystem")
    val bankMisrService = system.actorOf(BankService.CreateBankService((0F, 2/100F, 5/100F)), "bank-misr")
    val bankAlAhlyService = system.actorOf(BankService.CreateBankService((1/100F, 5/100F, 10/100F)), "bank-alAhly")
    val naguib = system.actorOf(Props[Customer], "customer-naguib")
    val yousef = system.actorOf(Props[Customer], "customer-yousef")

  /**
   * initialize a new customer or gets his details from the database if he exists
   * */

//  yousef ! Initialize(CustomerType(name = "yousef helly", email = "yousef.helly@gmail.com", phone = "01020273407", address = "Egypt, Giza,Eshreen st."))
//  Thread.sleep(1500)

  /**
   * Create a new account in a bank service and initialize it with an initial balance
   * */

  //naguib ! CreateAccount(bankAlAhlyService, 25000)
  //Thread.sleep(1500)

  /**
   * Get information about customer and his accounts
   * */

  //naguib ! GetMyInfo()
  //Thread.sleep(1500)

  /**
   * make a deposit or withdraw to the bank account
   * */

  //naguib ! Withdraw(bankAlAhlyService, 10000, 2)
  //Thread.sleep(1500)
  //naguib ! Deposit(bankAlAhlyService, 5045)
  //Thread.sleep(1500)

  /**
   * transfer money to another account
   * */

  //naguib ! Transfer(bankMisrService, yousef, 3500, accountNumber = 2, toBankService = bankMisrService)
  //Thread.sleep(1500)

  /**
   * Get the Customer recent transactions related to bank service
   * */

  //naguib ! GetRecentTransactions(bankAlAhlyService)
  //Thread.sleep(1500)

}