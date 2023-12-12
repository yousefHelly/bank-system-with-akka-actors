package models

import java.time.LocalDate

case class Transaction(id: Int=1, transactionType: TypeOfTransaction.transaction, date: LocalDate, amount: Double, accountId: Int, AccountTransferId: Option[Int] = None)
