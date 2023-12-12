package actors

import actors.BankService.{CountUserTransactions, CreateTransaction}
import akka.actor.Actor
import services.Transaction._
class Transaction extends Actor{
  override def receive: Receive = {
    case CreateTransaction(transaction) =>
      insertTransaction(transaction) match {
        case 1 =>
          println(s"[${self.path.parent.name} -> ${self.path.name}] Transaction created Successfully!")
        case 0 =>
          println(s"[${self.path.parent.name} -> ${self.path.name}] Failed to create a Transaction!")
      }
    case CountUserTransactions(customerId) =>
      val allTransactions = RetrieveRecentTransactions(sender().path.name, customerId)
      println(s"*********************** Recent Transactions ***********************")
      for {
        (name, bank, id, transaction, amount, date, toID) <- allTransactions
      } yield {
        toID match {
          case Some(id) => println(s"[${sender().path.name} ${self.path.name}] customer name: ${name} | bank service: ${bank} | accountID: ${id} -> transaction type: ${transaction} | amount: ${amount} | date: ${date} | to: ${toID}")
          case None => println(s"[${sender().path.name} ${self.path.name}] customer name: ${name} | bank service: ${bank} | accountID: ${id} -> transaction type: ${transaction} | amount: ${amount} | date: ${date}")
        }
      }
  }
}
