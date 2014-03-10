package models.balance.db
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._
  import scala.slick.model.ForeignKeyAction
  import scala.slick.jdbc.{GetResult => GR}
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  
  /** Entity class storing rows of table Balance
   *  @param balanceid Database column balanceId AutoInc, PrimaryKey
   *  @param userid Database column userId 
   *  @param amount Database column amount  */
  case class BalanceRow(balanceid: Int, userid: Int, amount: Double)
  /** GetResult implicit for fetching BalanceRow objects using plain SQL queries */
  implicit def GetResultBalanceRow(implicit e0: GR[Int], e1: GR[Double]): GR[BalanceRow] = GR{
    prs => import prs._
    BalanceRow.tupled((<<[Int], <<[Int], <<[Double]))
  }
  /** Table description of table balance. Objects of this class serve as prototypes for rows in queries. */
  class Balance(tag: Tag) extends Table[BalanceRow](tag, "balance") {
    def * = (balanceid, userid, amount) <> (BalanceRow.tupled, BalanceRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (balanceid.?, userid.?, amount.?).shaped.<>({r=>import r._; _1.map(_=> BalanceRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column balanceId AutoInc, PrimaryKey */
    val balanceid: Column[Int] = column[Int]("balanceId", O.AutoInc, O.PrimaryKey)
    /** Database column userId  */
    val userid: Column[Int] = column[Int]("userId")
    /** Database column amount  */
    val amount: Column[Double] = column[Double]("amount")
    
    /** Foreign key referencing User (database name balance_ibfk_1) */
    val userTable = TableQuery[models.db.UserTable]
    val userFk = foreignKey("balance_ibfk_1", userid, userTable)(r => r.userId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Balance */
  lazy val Balance = new TableQuery(tag => new Balance(tag))
  
 
  /** Entity class storing rows of table Transaction
   *  @param transactionid Database column transactionId AutoInc, PrimaryKey
   *  @param userid Database column userId 
   *  @param amount Database column amount 
   *  @param time Database column time 
   *  @param note Database column note  */
  case class TransactionRow(transactionid: Int, userid: Int, amount: Double, time: java.sql.Timestamp, note: Option[String])
  /** GetResult implicit for fetching TransactionRow objects using plain SQL queries */
  implicit def GetResultTransactionRow(e1: GR[Double], e2: GR[Int], e3: GR[java.sql.Timestamp], e4: GR[String]): GR[TransactionRow] = GR{
    prs => import prs._
    TransactionRow.tupled((<<[Int], <<[Int], <<[Double], <<[java.sql.Timestamp], <<?[String]))
  }
  /** Table description of table transaction. Objects of this class serve as prototypes for rows in queries. */
  class Transaction(tag: Tag) extends Table[TransactionRow](tag, "transaction") {
    def * = (transactionid, userid, amount, time, note) <> (TransactionRow.tupled, TransactionRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (transactionid.?, userid.?, amount.?, time.?, note).shaped.<>({r=>import r._; _1.map(_=> TransactionRow.tupled((_1.get, _2.get, _3.get, _4.get, _5)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))
    
    /** Database column transactionId AutoInc, PrimaryKey */
    val transactionid: Column[Int] = column[Int]("transactionId", O.AutoInc, O.PrimaryKey)
    /** Database column userId  */
    val userid: Column[Int] = column[Int]("userId")
    /** Database column amount  */
    val amount: Column[Double] = column[Double]("amount")
    /** Database column time  */
    val time: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("time")
    /** Database column note  */
    val note: Column[Option[String]] = column[Option[String]]("note")
    
    /** Foreign key referencing User (database name transaction_ibfk_1) */
    val userTable = TableQuery[models.db.UserTable]
    val userFk = foreignKey("transaction_ibfk_1", userid, userTable)(r => r.userId, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Transaction */
  lazy val Transaction = new TableQuery(tag => new Transaction(tag))
}
