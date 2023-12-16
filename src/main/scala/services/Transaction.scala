package services
import models.{Account, Transaction, TypeOfTransaction}

import scala.concurrent.Await
import slick.jdbc.MySQLProfile.api._

import scala.concurrent.duration.DurationInt
import Connection.db
import models.TypeOfTransaction.transaction

import java.time.LocalDate

object Transaction {
  def insertTransaction(transaction: Transaction): Int = {
    val insertQuery = BankTables.transactionTable += transaction
    val result = try {
      Await.result(db.run(insertQuery), 1.second)
    } catch {
      case err: Throwable => println(err)
        0
    }
    result
  }

  def RetrieveRecentTransactions(bankService: String, customerId: Int) = {
    implicit val bankMappingTable = MappedColumnType.base[TypeOfTransaction.transaction, String](
      transaction => transaction.toString,
      string => TypeOfTransaction.withName(string)
    )
    val transactionsQuery = for {
      ((ct, at), tt) <- BankTables.CustomerTable.join(BankTables.AccountTable).on(_.id===_.customerId).join(BankTables.transactionTable).on(_._2.id===_.accountId).filter(_._1._1.id===customerId).filter(_._1._2.bankService===bankService)
    } yield {
      (ct.name, at.bankService, at.id, tt.transactionType, tt.amount, tt.date, tt.accountTransferId)
    }
    val futureAccount = db.run(transactionsQuery.result)
    val result = try {
      Await.result(futureAccount, 1.second)
    } catch {
      case _: Throwable => None
    }
    Some(result)
  }
}
