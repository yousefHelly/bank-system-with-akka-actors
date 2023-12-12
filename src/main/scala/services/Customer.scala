package services
import scala.concurrent.Await
import slick.jdbc.MySQLProfile.api._
import models.Customer
import scala.concurrent.duration.DurationInt
import Connection.db

object Customer {
  def insertCustomer(customer: Customer): Int = {
    val insertQuery = (BankTables.CustomerTable returning BankTables.CustomerTable.map(_.id)) += customer
    val result = try {
      Await.result(db.run(insertQuery), 1.second)
    } catch {
      case _: Throwable => 0
    }
    result
  }

  def retrieveCustomers(): Seq[Customer] = {
    val futureCustomers = db.run(BankTables.CustomerTable.result)
    val result = try {
      Await.result(futureCustomers, 1.second)
    } catch {
      case _: Throwable => Seq.empty[Customer]
    }
    result
  }

  def retrieveCustomer(email: String): Option[Customer] = {
    val customerQuery = BankTables.CustomerTable.filter(_.email.like(s"%$email%")).result
    val futureCustomer = db.run(customerQuery)
    val result = try {
      Await.result(futureCustomer, 1.second)
    } catch {
      case _: Throwable => Seq.empty[Customer]
    }
    result.headOption
  }

  def updateCustomer(id: Int, customer: Customer): Int = {
    val customerQuery = BankTables.CustomerTable.filter(_.id === id).update(customer)
    val result = try {
      Await.result(db.run(customerQuery), 1.second)
    } catch {
      case _: Throwable => 0
    }
    result
  }

  def deleteCustomer(id: Int): Int = {
    val customerQuery = BankTables.CustomerTable.filter(_.id === id).delete
    val result = try {
      Await.result(db.run(customerQuery), 1.second)
    } catch {
      case _: Throwable => 0
    }
    result
  }

}
