package financetracker

import kotlinx.serialization.Serializable
import java.time.LocalDate
import java.util.UUID


//transaction type
@Serializable
enum class TransactionType{INCOME, EXPENSE}

//category of income & expenses
@Serializable
enum class Category{FOOD,SALARY,TRAVEL,ENTERTAINMENT,OTHERS}

@Serializable
enum class BudgetPeriod{WEEKLY,MONTHLY,YEARLY}

@Serializable
data class Transaction(
    val id: String = UUID.randomUUID().toString(), //random uid generation
    val amount: Double,
    val description: String,
    val date: String = LocalDate.now().toString(),
    val type: TransactionType,
    val category: Category
)
@Serializable
data class Account(
    val name: String,
    val initialBalance: Double,
    val transactions: MutableList<Transaction> = mutableListOf()
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
@Serializable
data class Budget(
    val category: Category,
    val limit: Double,
    val period: BudgetPeriod
)
@Serializable
data class FinanceData(
    var accounts: List<Account>,
    var transactions: List<Transaction>,
    var budgets: List<Budget>
)
class FinanceTracker {
    var data: FinanceData = FinanceData(
        accounts = listOf(),
        transactions = listOf(),
        budgets = listOf()
    )


    fun addAccount(account: Account) {
        data.accounts += account
    }

    fun addTransaction(transaction: Transaction, account: Account) {
        data.transactions += transaction
        val existingAccount = data.accounts.firstOrNull { it.name == account.name }
        if (existingAccount != null) {
            existingAccount.transactions.add(transaction)
        } else println("Account '${account.name}' doesn't exist...")
    }


    fun setBudget(category: Category, limit: Double, period: BudgetPeriod) {
        val updateBudget = Budget(category, limit, period)
       data.budgets = data.budgets.filterNot { it.category == category } + updateBudget
    }
    fun generateCategoryReport(category: Category): Report.CategoryReport {
        val filtered = data.transactions.filter { it.category == category }
        val income = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        return Report.CategoryReport(category, income, expense, income - expense)
    }
    fun generatePeriodReport(period: BudgetPeriod): Report.PeriodReport{
        val (start, end) = getPeriodRange(period)
        val filtered = data.transactions.filter {
            val date = LocalDate.parse(it.date)
            date in start..end
        }
        val income = filtered.filter { it.type== TransactionType.INCOME }.sumOf { it.amount }
        val expense = filtered.filter { it.type== TransactionType.EXPENSE }.sumOf { it.amount }
        return Report.PeriodReport(period,start.toString(),end.toString(),income,expense,income-expense)
    }
    fun getPeriodRange(period: BudgetPeriod): Pair<LocalDate, LocalDate> {
        val now = LocalDate.now()
        return when (period) {
            BudgetPeriod.WEEKLY -> now.minusDays(6) to now
            BudgetPeriod.MONTHLY -> now.withDayOfMonth(1) to now
            BudgetPeriod.YEARLY -> now.withDayOfYear(1) to now
        }
    }
    fun generateGeneralReport(): Report.GeneralReport {
        val income = data.transactions.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val expense = data.transactions.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        val count = data.transactions.size
        return Report.GeneralReport(income, expense, income - expense, count)
    }
    fun printAccountSummary(accountName: String) {
        val account = data.accounts.firstOrNull { it.name == accountName }

        if (account == null) {
            println("Account \"$accountName\" not found.")
            return
        }

        println("📘 Account Summary for: ${account.name}")
        println("💰 Current Balance: ${account.currentBalance.toCurrency()}")

        val recent = account.transactions.takeLast(5).reversed()
        println("\n📄 Last 5 Transactions:")
        if (recent.isEmpty()) {
            println("No transactions yet.")
        } else {
            for ((i, tx) in recent.withIndex()) {
                val sign = if (tx.type == TransactionType.INCOME) "+" else "-"
                println("${i + 1}. ${tx.date} | ${tx.category} | $sign${tx.amount.toCurrency()} | ${tx.description}")
            }
        }

        val incomeTotal = account.transactions
            .filter { it.type == TransactionType.INCOME }
            .sumOf { it.amount }

        val expenseTotal = account.transactions
            .filter { it.type == TransactionType.EXPENSE }
            .sumOf { it.amount }

        println("\n📊 Totals:")
        println("➕ Total Income:  ${incomeTotal.toCurrency()}")
        println("➖ Total Expense: ${expenseTotal.toCurrency()}")
    }


}




//currency formatting


fun Double.toCurrency(): String = String.format("৳%.2f",this)

fun main() {

    val tracker = FinanceTracker()
    val manager = FileManager()
    println("Enter Account Name:")
    val accName = readln()

    println("Enter Initial Balance:")
    val initialBalance = readln().toDoubleOrNull() ?: 0.0

    val account = Account(name = accName, initialBalance = initialBalance)
    tracker.addAccount(account)

    println("${"-".repeat(10)} Welcome to Finance Tracker ${"-".repeat(10)}")
    while (true) {
        println(
            """
            
            ==== MENU ====
            1. Add Transaction
            2. View Account Summary
            3. Save Account
            4. Load Account
            5. Set Budget
            6. Generate Monthly Report
            0. Exit
            """.trimIndent()
        )
        print("Choose an option: ")
        when (readln()) {
            "1" -> {
                println("➕ Add Transaction")
                print("Amount: ")
                val amount = readln().toDouble()
                print("Description: ")
                val desc = readln()
                println("Type: 1) INCOME  2) EXPENSE")
                val type = when (readln()) {
                    "1" -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                println("Category: 1) FOOD 2) SALARY 3) TRAVEL 4) ENTERTAINMENT 5) OTHERS")
                val category = when (readln()) {
                    "1" -> Category.FOOD
                    "2" -> Category.SALARY
                    "3" -> Category.TRAVEL
                    "4" -> Category.ENTERTAINMENT
                    else -> Category.OTHERS
                }
                val transaction = Transaction(amount = amount, description = desc, type = type, category = category)
                tracker.addTransaction(transaction, account)
                println("✅ Transaction added.")
            }

            "2" -> tracker.printAccountSummary(account.name)
            "3" -> {
                manager.saveFinanceData(tracker.data, "finance-tracker.json")
                println("✅ Account data saved to file.")
            }

            "4" -> {
                val loaded = manager.loadFinanceData("finance-tracker.json")
                if (loaded != null) {
                    tracker.data = loaded
                    println("✅ Data loaded.")
                } else println("❌ No saved data found.")
            }

            "5" -> {
                println(" Set Budget ")

                println("Choose category (1-5): 1) FOOD 2) SALARY 3) TRAVEL 4) ENTERTAINMENT 5) OTHERS")
                val cat = when (readln()) {
                    "1" -> Category.FOOD
                    "2" -> Category.SALARY
                    "3" -> Category.TRAVEL
                    "4" -> Category.ENTERTAINMENT
                    else -> Category.OTHERS
                }
                print("Enter budget limit: ")
                val limit = readln().toDouble()
                println("Period: 1) WEEKLY 2) MONTHLY 3) YEARLY")
                val period = when (readln()) {
                    "1" -> BudgetPeriod.WEEKLY
                    "2" -> BudgetPeriod.MONTHLY
                    else -> BudgetPeriod.YEARLY
                }
                tracker.setBudget(cat, limit, period)
                println("✅ Budget set.")


            }

            "6" -> {
                val report = tracker.generatePeriodReport(BudgetPeriod.MONTHLY)
                println("📆 Monthly Report")
                println("From ${report.from} to ${report.to}")
                println("Income: ${report.totalIncome.toCurrency()}")
                println("Expense: ${report.totalExpense.toCurrency()}")
                println("Net: ${(report.totalIncome - report.totalExpense).toCurrency()}")

            }

            "0" -> {
                println("👋 Exiting. Goodbye!")
                break
            }

            else -> println("❗ Invalid option.")


        }
    }
}
