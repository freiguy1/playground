package models.balance

import play.api.db.DB
import play.api.Play.current
import play.api.libs.json._
import play.api.libs.functional.syntax._

import scala.slick.driver.MySQLDriver.simple._
import Database.dynamicSession

case class Balance(balanceid: Int, userid: Int, amount: Double, transactions: Seq[Transaction])
case class Transaction(transactionid: Int, userid: Int, amount: Double, time: java.util.Date, note: Option[String])

object Balance{

  lazy private val database = Database.forDataSource(DB.getDataSource("default"))
  
  def get: Seq[Balance] = database.withDynSession {
    val query = for(
      balance <- db.Tables.Balance; 
      trans <- db.Tables.Transaction if trans.userid === balance.userid
    ) yield (balance, trans)
    val fullMap = query.list.groupBy(_._1.userid)
    fullMap.map(item => 
      Balance(item._2.head._1.balanceid, item._2.head._1.userid, item._2.head._1.amount, 
        item._2.map{case(b, t) => Transaction(t.transactionid, t.userid, t.amount, t.time, t.note)}
      )).toSeq 
  }

  def getByUserId(userId: Int): Balance = database.withDynSession {
    val query = for(balance <- db.Tables.Balance if(balance.userid === userId)) yield balance
    query.firstOption.map{ dbBalance =>
      val query2 = for(transaction <- db.Tables.Transaction if(transaction.userid === userId)) yield transaction
      val transList = query2.list.map(trans => 
        Transaction(trans.transactionid, trans.userid, trans.amount, trans.time, trans.note)).toSeq
      Balance(dbBalance.balanceid, userId, dbBalance.amount, transList)
    }.getOrElse{
      val balanceId = (db.Tables.Balance returning db.Tables.Balance.map(_.balanceid)) += db.Tables.BalanceRow(0, userId, 0)
      Balance(0, userId, 0.0, Seq())  
    }
  }

  def addTransaction(userId: Int, amount: Double, note: Option[String]): Unit = database.withDynSession {
    val roundedAmount = (math floor(amount * 100)) / 100
    val currentBalance = getByUserId(userId)
    val newTransaction = db.Tables.TransactionRow(0, userId, roundedAmount, new java.sql.Timestamp((new java.util.Date()).getTime()), note)
    db.Tables.Transaction += newTransaction
    val newBalance = (math floor((currentBalance.amount + roundedAmount) * 100)) / 100
    (for (balance <- db.Tables.Balance if balance.balanceid === currentBalance.balanceid) yield balance.amount).update(newBalance)
  }

  def clearTransactionsThrough(transactionId: Int, userId: Int): Unit = database.withDynSession {
    db.Tables.Transaction.filter(row => row.transactionid <= transactionId && row.userid === userId).delete
  }
}

