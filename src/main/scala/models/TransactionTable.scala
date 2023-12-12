package models

import slick.lifted.Tag
import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
import services.BankTables

import java.time.LocalDate
import scala.language.postfixOps


class TransactionTable(tag: Tag) extends Table[Transaction](tag, Some("bank"), "transaction"){
  implicit val bankMappingTable = MappedColumnType.base[TypeOfTransaction.transaction, String](
    transaction => transaction.toString,
    string => TypeOfTransaction.withName(string)
  )
  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def transactionType =column[TypeOfTransaction.transaction]("transaction_type")
  def date = column[LocalDate]("date")
  def amount = column[Double]("amount")
  def accountId = column[Int]("account_id")
  def accountTransferId = column[Option[Int]]("account_transfer_id")
  foreignKey("account_id", accountId, BankTables.AccountTable)(_.id, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  foreignKey("account_transfer_id", accountTransferId, BankTables.AccountTable)(_.id?, onUpdate = ForeignKeyAction.Cascade, onDelete = ForeignKeyAction.Cascade)
  override def * : ProvenShape[Transaction] = (id, transactionType, date, amount, accountId, accountTransferId) <> (Transaction.tupled, Transaction.unapply)
}
