package models

import slick.jdbc.MySQLProfile.api._
import slick.lifted.ProvenShape
class CustomerTable(tag: Tag) extends Table[Customer](tag, Some("bank"), "customer"){
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def email: Rep[String] = column[String]("email")
    def phone: Rep[String] = column[String]("phone")
    def address: Rep[String] = column[String]("address")
    override def * : ProvenShape[Customer] = (id, name, email, phone, address)<>(Customer.tupled, Customer.unapply)
}
