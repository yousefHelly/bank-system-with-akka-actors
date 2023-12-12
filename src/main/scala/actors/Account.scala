package actors

import actors.Account._
import actors.BankService.{DepositDoneSuccessfully, ReceiveTransferDoneSuccessfully, SendTransferDoneSuccessfully, WithdrawalDoneSuccessfully}
import actors.Customer._
import akka.actor.{Actor, ActorRef}
import services.Account.{insertAccount, retrieveAccount}
import models.{Account => AccountType}

import java.time.LocalDate

class Account extends Actor{
  override def receive: Receive = initialAccount
  private def initialAccount: Receive = {
    case HasAccount(id, accountId, bankService) =>
      HandleRetrieveAccount(id, accountId, false)
    case DepositRequest(_,_,_) | WithdrawRequest(_,_,_) => println("Can't Make this operation. please initialize yourself as a customer or create a new account")
    case InitializeAccount(id, bankService, balance) =>
      insertAccount(AccountType(balance = balance, openDate = LocalDate.now(), customerId = id, bankService = bankService)) match {
        case number => HandleRetrieveAccount(id, number, true)

        case 0 =>
          println(s"[${self.path.parent.name}'s ${self.path.name}] Failed to create an Account!")
      }

  }
  private def initializedAccount(account: AccountType): Receive = {
    case WhatIsMyBalance => println(s"[${self.path.parent.name}'s ${self.path.name}] current balance is ${account.balance}$$")
    case CreateDepositRequest(bankService, amount) => bankService ! DepositRequest(account.id, account.balance, amount)
    case CreateWithdrawRequest(bankService, amount) => bankService ! WithdrawRequest(account.id, account.balance, amount)
    case ConfirmReceiverRequest(receiver, receiverId, receiverBalance, amount) =>
      println(s"[${self.path.parent.name}'s ${self.path.name}] Receiver Confirmed the transfer!")
      context.system.actorSelection("/user/"+account.bankService) ! TransferRequest(AccountId = account.id, ReceiverId = receiverId, currentBalance = account.balance, amount = amount, receiverBalance = receiverBalance, receiver = receiver)
    case DepositDoneSuccessfully | WithdrawalDoneSuccessfully | SendTransferDoneSuccessfully | ReceiveTransferDoneSuccessfully =>
      println(s"[${self.path.parent.name}'s ${self.path.name}] Operation Done successfully on this account.")
      context.become(initialAccount)
      self ! HasAccount(account.customerId, account.id, account.bankService)
    case RequestToReceiver(requester, amount) =>
      println(s"[${self.path.parent.name}'s ${self.path.name}] ${requester.path.parent.name} wants to transfer me ${amount}$$.")
      requester ! ConfirmReceiverRequest(self, account.id, account.balance, amount)
  }

  private def HandleRetrieveAccount = (id: Int, number: Int, isNew: Boolean) => {
    if(isNew)
      println(s"[${self.path.parent.name}'s ${self.path.name}] Account created successfully!")
    retrieveAccount(id, Some(number)) match {
      case Some(account) =>
        context.become(initializedAccount(account))
      case None =>
        println(s"[${self.path.parent.name}'s ${self.path.name}] Failed to Retrieve account data!")
    }
  }
}
object Account{
  case class DepositRequest(AccountId: Int, currentBalance: Double, amount: Double)
  case class WithdrawRequest(AccountId: Int, currentBalance: Double, amount: Double)
  case class TransferRequest(AccountId: Int, receiver: ActorRef, ReceiverId: Int, currentBalance: Double, receiverBalance: Double, amount: Double)
  case class ConfirmReceiverRequest(receiver: ActorRef, receiverId: Int, receiverBalance: Double, amount: Double)
}