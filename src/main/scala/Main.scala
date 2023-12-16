import actors.{BankService, Customer}
import actors.Customer.{CreateAccount, Deposit, GetMyInfo, GetRecentTransactions, Initialize, Transfer, Withdraw}
import akka.actor.{Actor, ActorSystem, Props}
import models.{Customer => CustomerType}
object Main extends App {
    val system = ActorSystem("supervisorSystem")
    val bankMisrService = system.actorOf(BankService.CreateBankService((0F, 2/100F, 5/100F)), "bank-misr")
    val bankAlAhlyService = system.actorOf(BankService.CreateBankService((1/100F, 5/100F, 10/100F)), "bank-alAhly")
    val naguib = system.actorOf(Props[Customer], "customer-naguib")
    val yousef = system.actorOf(Props[Customer], "customer-yousef")
    val ahmed = system.actorOf(Props[Customer], "customer-ahmed")

  /**
   * initialize a new customer or gets his details from the database if he exists
   * */

    Thread.sleep(1500)
    yousef ! Initialize(CustomerType(name = "yousef helly", email = "yousef.helly@gmail.com", phone = "01020273407", address = "Egypt, Giza,Eshreen st."))
//    ahmed ! GetMyInfo()
    ahmed ! Initialize(CustomerType(name = "ahmed mohamed", email = "ahmed@gmail.com", phone = "01020273407", address = "Egypt, Giza,Eshreen st."))

  /**
   * Create a new account in a bank service and initialize it with an initial balance
   * */

    //ahmed ! CreateAccount(bankAlAhlyService, 1000)
    //ahmed ! CreateAccount(bankMisrService, 500)
  //Thread.sleep(1500)

  /**
   * Get information about customer and his accounts
   * */

    //ahmed ! GetMyInfo()

  Thread.sleep(1500)

  /**
   * make a deposit or withdraw to the bank account
   * */

    //ahmed ! Withdraw(bankAlAhlyService, 500, 2)
  //Thread.sleep(1500)
   // ahmed ! Deposit(bankAlAhlyService, 5045)
  //Thread.sleep(1500)

  /**
   * transfer money to another account
   * */

  //ahmed ! Transfer(bankMisrService, yousef, 250, toBankService = bankMisrService)
  //Thread.sleep(1500)

  /**
   * Get the Customer recent transactions related to bank service
   * */

  //ahmed ! GetRecentTransactions(bankAlAhlyService)
  Thread.sleep(2000)
  ahmed ! GetRecentTransactions(bankMisrService)
  //Thread.sleep(1500)

}