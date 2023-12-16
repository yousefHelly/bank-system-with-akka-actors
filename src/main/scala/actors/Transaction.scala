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
      allTransactions match {
        case Some(transactions) => {
          println(s"*********************** Recent Transactions ***********************")
          transactions.iterator.foreach {
            case (name, bank, id, transaction, amount, date, toID) =>
              toID match {
                case Some(_) =>
                  println(s"[${sender().path.name} ${self.path.name}] customer name: ${name} | bank service: ${bank} | accountID: ${id} -> transaction type: ${transaction} | amount: ${amount} | date: ${date} | to: accountID-${toID.get}")
                case None =>
                  println(s"[${sender().path.name} ${self.path.name}] customer name: ${name} | bank service: ${bank} | accountID: ${id} -> transaction type: ${transaction} | amount: ${amount} | date: ${date}")
              }
          }
        }
        case _ => println(s"[${self.path.parent.name} -> ${self.path.name}] Failed to get all Transactions for customer id $customerId !")
      }
  }
}
