package services
import models.Account
import scala.concurrent.Await
import slick.jdbc.MySQLProfile.api._
import scala.concurrent.duration.DurationInt
import Connection.db

object Account {
  def insertAccount(account: Account): Int = {
    val insertQuery = (BankTables.AccountTable returning BankTables.AccountTable.map(_.id)) += account
    val result = try {
      Await.result(db.run(insertQuery), 1.second)
    } catch {
      case _: Throwable => 0
    }
    result
  }

  def retrieveAccount(customerId: Int, accountId: Option[Int]): Option[Account] = {
    val accountQuery = accountId match {
      case Some(acId) => BankTables.AccountTable.filter(_.customerId===customerId).filter(_.id===acId).result
      case None => BankTables.AccountTable.filter(_.customerId===customerId).result
    }
    val futureAccount = db.run(accountQuery)
    val result = try {
      Await.result(futureAccount, 1.second)
    } catch {
      case _: Throwable => Seq.empty[Account]
    }
    result.headOption
  }

  def retrieveAccounts(customerId: Int): Tuple2[ Map[Int, String] ,Map[String, Int]] = {
    val accountQuery = BankTables.AccountTable.filter(_.customerId === customerId).result

    val futureAccount = db.run(accountQuery)
    val result = try {
      Await.result(futureAccount, 1.second)
    } catch {
      case _: Throwable => Seq.empty[Account]
    }
    val resultMap =  result.map( acc => acc.id -> acc.bankService).toMap
    var accountsCountInEachBank: Map[String, Int] = Map()
     val a = for {
      (id, name) <- resultMap
    } yield {
       accountsCountInEachBank = accountsCountInEachBank.updatedWith(name) {
        case Some(count) => Some(count+1)
        case None => Some(1)
      }
    }
    (
      resultMap,
      accountsCountInEachBank
    )
  }


  def UpdateAccountBalance(id: Int, newBalance: Double): Int = {
    val oldBalance = for { ac <- BankTables.AccountTable if ac.id===id } yield ac.balance
    val accountQuery = oldBalance.update(newBalance)
    val result = try {
      Await.result(db.run(accountQuery), 1.second)
    } catch {
      case _: Throwable => 0
    }
    result
  }


  def updateAccount(id: Int, account: Account): Int = {
    val accountQuery = BankTables.AccountTable.filter(_.id === id).update(account)
    val result = try {
      Await.result(db.run(accountQuery), 1.second)
    } catch {
      case _: Throwable => 0
    }
    result
  }

  def deleteAccount(id: Int): Int = {
    val accountQuery = BankTables.AccountTable.filter(_.id === id).delete
    val result = try {
      Await.result(db.run(accountQuery), 1.second)
    } catch {
      case _: Throwable => 0
    }
    result
  }
}
