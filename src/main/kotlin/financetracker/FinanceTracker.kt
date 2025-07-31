package financetracker

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID
import kotlin.unaryMinus

//transaction type
@Serializable
enum class TransactionType{INCOME, EXPENSE}

//category of income & expenses
@Serializable
enum class Category{FOOD,SALARY,TRAVEL,ENTERTAINMENT,OTHERS}

@Serializable
data class Transaction(
    val id: String = UUID.randomUUID().toString(), //random uid generation
    val amount: Double,
    val description: String,
    val date: String,
    val type: TransactionType,
    val category: Category
)
@Serializable
data class Account(
    val name: String,
    val initialBalance: Double,
    val transactions: MutableList<Transaction> = mutableListOf<Transaction>() // empty by default
) {
     val currentBalance: Double
//        get() {
//            var balance = initialBalance
//            for (transaction in transactions) {
//                when(transaction.type) {
//                    TransactionType.INCOME -> balance += transaction.amount
//                    TransactionType.EXPENSE -> balance -= transaction.amount
//                }
//            }
//             return balance
//        }
  get() = initialBalance + transactions.sumOf {
      when(it.type){
          TransactionType.EXPENSE -> - it.amount
          TransactionType.INCOME -> + it.amount
      }
}
}





//currency formatting
fun Double.toCurrency(): String = String.format("$%.2f",this)

fun main() {


}