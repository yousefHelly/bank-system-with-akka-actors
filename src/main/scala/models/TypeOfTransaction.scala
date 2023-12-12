package models

object TypeOfTransaction extends Enumeration{
  type transaction = Value
  val Withdraw = Value("withdraw")
  val Deposit = Value("deposit")
  val Send = Value("send")
  val Receive = Value("receive")
}
