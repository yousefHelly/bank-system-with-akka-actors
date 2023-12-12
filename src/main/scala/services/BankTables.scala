package services

import models.{AccountTable, CustomerTable, TransactionTable}
import slick.lifted.TableQuery
object BankTables{
  //    API entry point
  lazy val CustomerTable = TableQuery[CustomerTable]
  lazy val AccountTable = TableQuery[AccountTable]
  lazy val transactionTable = TableQuery[TransactionTable]
 }