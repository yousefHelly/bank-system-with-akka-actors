package models

import java.time.LocalDate

final case class Account(id: Int=1, balance: Double, openDate: LocalDate, customerId: Int, bankService: String)
