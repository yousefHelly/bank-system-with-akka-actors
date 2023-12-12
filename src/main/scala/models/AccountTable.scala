package models

import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
import services.BankTables
import java.time.LocalDate


class AccountTable(tag: Tag) extends Table[Account](tag, Some("bank"), "account") {
  def id = column[Int] ("id", O.PrimaryKey, O.AutoInc)
  def balance = column[Double] ("balance")
  def openDate = column[LocalDate]("open_date")
  def customerId = column[Int]("customer_id")
  def bankService = column[String]("bank_service")
  // Foreign Key
  def customer = foreignKey("customer_id", customerId, BankTables.CustomerTable)(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  override def * : ProvenShape[Account] = (id, balance, openDate, customerId, bankService)<>(Account.tupled, Account.unapply)
}