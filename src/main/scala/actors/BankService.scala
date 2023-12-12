package actors

import actors.Account.{DepositRequest, TransferRequest, WithdrawRequest}
import actors.BankService.{CountUserTransactions, CreateTransaction, DepositDoneSuccessfully, ReceiveTransferDoneSuccessfully, SendTransferDoneSuccessfully, WithdrawalDoneSuccessfully}
import actors.Customer.RecentTransactionsRequest
import akka.actor.{Actor, ActorRef, Props}
import models.{TypeOfTransaction, Transaction => TransactionType}
import services.Account.UpdateAccountBalance
import services.Transaction.RetrieveRecentTransactions

import java.time.format.DateTimeFormatter
import java.time.{LocalDate, LocalDateTime}

class BankService(taxs: Tuple3[Float, Float, Float]) extends Actor{
  override def receive: Receive = {
    case DepositRequest(accountId,currentBalance, amount) => handleDeposit(accountId, currentBalance, amount)

    case WithdrawRequest(accountId, currentBalance, amount) => handleWithdraw(accountId, currentBalance, amount)

    case TransferRequest(accountId, receiver, receiverId, currentBalance, receiverBalance, amount) => handleTransfer(accountId, receiver, receiverId, currentBalance, receiverBalance, amount)

    case RecentTransactionsRequest(customerId) => handleGetTransactions(customerId)
  }
  private def handleDeposit = (accountId: Int,currentBalance: Double, amount: Double)=>{
    val amountWithTaxes = (amount - (amount * taxs._1)).ceil
    val newBalance = currentBalance + amountWithTaxes
    println(s"[${self.path.name}] account with ID $accountId requested to deposit $amount${if ( (amount * taxs._1).ceil > 0) s" + ${ (amount * taxs._1).ceil}$$ taxes" else "$"}")
    UpdateAccountBalance(accountId, newBalance) match {
      case done: Int =>
        println(s"[${self.path.name}] depositing done successfully to accountId: ${accountId}")
        sender() ! DepositDoneSuccessfully
        MakeATransaction(accountId, amountWithTaxes, TypeOfTransaction.Deposit, None)
      case 0 => println(s"[${self.path.name}] depositing Failed to accountId: ${accountId}")
    }
  }
  private def handleWithdraw = (accountId: Int, currentBalance: Double, amount: Double) => {
    val amountWithTaxes = (amount + (amount * taxs._2)).ceil
    val newBalance = currentBalance - amountWithTaxes
    println(s"[${self.path.name}] account with ID $accountId requested to withdraw $amount${if ((amount * taxs._2).ceil > 0) s"$$ + ${(amount * taxs._2).ceil}$$ taxes" else "$"}")

    if(newBalance>=0){
      UpdateAccountBalance(accountId, newBalance) match {
        case done: Int =>
          println(s"[${self.path.name}] Withdrawal done successfully from accountId: ${accountId}.")
          sender() ! WithdrawalDoneSuccessfully
          MakeATransaction(accountId, amountWithTaxes, TypeOfTransaction.Withdraw, None)

        case 0 => println(s"[${self.path.name}] Withdrawal Failed from accountId: ${accountId}.")
      }
    }
    else{
      println(s"[${self.path.name}] Withdrawal Failed from accountId: ${accountId}. current balance(${currentBalance}$$) is less than the withdrawal amount(${(amount + ( amount * taxs._2 )).ceil}$$).")
    }
  }
  private def handleTransfer = (accountId: Int, receiver: ActorRef, receiverId: Int, currentBalance: Double, receiverBalance: Double, amount: Double) => {
    val amountWithTaxes = (amount + (amount * taxs._3)).ceil
    val newBalance = currentBalance - amountWithTaxes
    val newReceiveBalance = receiverBalance + amount
    println(s"[${self.path.name}] account with ID $accountId requested to transfer $amount${if ((amount * taxs._3).ceil > 0) s"$$ + ${(amount * taxs._3).ceil}$$ taxes" else "$"} to account with ID ${receiverId}")
    if (newBalance >= 0) {
      UpdateAccountBalance(accountId, newBalance) match {
        case done: Int =>
          println(s"[${self.path.name}] Transfer done successfully from accountId: ${accountId}.")
          UpdateAccountBalance(receiverId, newReceiveBalance) match {
            case done: Int =>
              println(s"[${self.path.name}] Transfer done successfully to accountId: ${receiverId}.")
              sender() ! SendTransferDoneSuccessfully
              receiver ! ReceiveTransferDoneSuccessfully
              MakeATransaction(accountId, amountWithTaxes, TypeOfTransaction.Send, Some(receiverId))
              MakeATransaction(receiverId, amount, TypeOfTransaction.Receive, None)
            case 0 => println(s"[${self.path.name}] Transfer Failed to accountId: ${receiverId}.")
          }

        case 0 => println(s"[${self.path.name}] Transfer Failed from accountId: ${accountId}.")
      }
    }
    else {
      println(s"[${self.path.name}] Withdrawal Failed from accountId: ${accountId}. current balance(${currentBalance}$$) is less than the withdrawal amount(${(amount + (amount * taxs._2)).ceil}$$).")
    }
  }
  private def MakeATransaction = (accountId: Int, amountWithTaxes:Double, operationType: TypeOfTransaction.transaction, receiveID: Option[Int])=>{
    val currentDateTime = LocalDateTime.now()
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH:mm:ss")
    val formattedDateTime = currentDateTime.format(formatter)
    val createdTransaction = context.actorOf(Props[Transaction], s"transaction-${accountId}-${operationType}-${formattedDateTime}")
    createdTransaction ! CreateTransaction(TransactionType(transactionType = operationType, date = LocalDate.now(), amount = amountWithTaxes, accountId = accountId, AccountTransferId = receiveID))
  }
  private def handleGetTransactions = (customerId: Int)=>{
    context.system.actorOf(Props[Transaction], "TransactionCounter") ! CountUserTransactions(customerId)
  }
}

object BankService {
  def CreateBankService = (taxs: Tuple3[Float, Float, Float])=> Props(new BankService(taxs))
  case object DepositDoneSuccessfully
  case object WithdrawalDoneSuccessfully
  case object SendTransferDoneSuccessfully
  case object ReceiveTransferDoneSuccessfully
  case class CountUserTransactions(customerId: Int)
  case class CreateTransaction(transaction: TransactionType)
}
