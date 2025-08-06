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
    var accounts: MutableList<Account>,
    var transactions: MutableList<Transaction>,
    var budgets: List<Budget>
)
class FinanceTracker {
    var data: FinanceData = FinanceData(
        accounts = mutableListOf(),
        transactions = mutableListOf(),
        budgets = listOf()
    )


    fun addAccount(account: Account) {
        data.accounts.add(account)
    }
    fun printAllAccount(){
        if (data.accounts.isEmpty()) println("No Accounts are created.")
        else{
            println("Available Accounts:")
            data.accounts.forEach { account ->
                println(account.name)
            }


        }
    }

    fun addTransaction(transaction: Transaction, account: Account) {
        data.transactions += transaction
        val existingAccount = data.accounts.firstOrNull { it.name == account.name }
        if (existingAccount != null) {
            existingAccount.transactions.add(transaction)
        } else println("Account '${account.name}' doesn't exist...")
    }
    fun setBudget(category: Category,limit: Double,period: BudgetPeriod){
        val updateBudget = Budget(category,limit,period)
        data.budgets = data.budgets.filter { it.category != category } + updateBudget
    }
    fun printAllBudget(){
        if (data.budgets.isEmpty()) println("No Budgets")
        else {
            data.budgets.forEach { budget ->
                println("Category:${budget.category} | Limit:${budget.limit.toCurrency()} | Period:${budget.period}")
            }
        }
    }
    fun generateCategoryReport(category: Category): Report.CategoryReport{
        val filtered = data.transactions.filter { it.category == category }
        val totalIncome = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        return Report.CategoryReport(category,totalIncome,totalExpense,totalIncome-totalExpense)
    }
    fun generatePeriodReport(period: BudgetPeriod): Report.PeriodReport{
        val end = LocalDate.now()
        val start = when (period) {
            BudgetPeriod.WEEKLY -> end.minusDays(6)
            BudgetPeriod.MONTHLY -> end.withDayOfMonth(1)
            BudgetPeriod.YEARLY -> end.withDayOfYear(1)
        }
        val filtered = data.transactions.filter {
            val date = LocalDate.parse(it.date)
            date in start..end
        }
        val totalIncome = filtered.filter { it.type == TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = filtered.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
        return Report.PeriodReport(period,start.toString(),end.toString(),totalIncome,totalExpense,totalIncome-totalExpense)
    }
    fun generateReport(): Report.GeneralReport{
        val totalIncome = data.transactions.filter { it.type== TransactionType.INCOME }.sumOf { it.amount }
        val totalExpense = data.transactions.filter { it.type== TransactionType.EXPENSE }.sumOf { it.amount }
        val count = data.transactions.size
        return Report.GeneralReport(totalIncome,totalExpense,totalIncome-totalExpense,count)
    }
    fun printAccountSummary(accountName: String){
        val account = data.accounts.firstOrNull{it.name==accountName}
        if (account==null) {
            println("Account \"$accountName\" not found.")
            return
        }

        println("📘 Account Summary for: ${account.name}")
        println("Current Balance: ${account.currentBalance.toCurrency()}")

        //val recent = account.transactions.takeLast(5).reversed()
        val recent = mutableListOf<Transaction>()
        val size = account.transactions.size
        val start = maxOf(0,size-5)
        for (i in size-1 downTo start){
            recent.add(account.transactions[i])
        }
        println("\n📄 Last 5 Transactions:")
        if (recent.isEmpty()){
            println("No transactions you POOR :) ")
        }
        else {
            for ((i,tx) in recent.withIndex()) {
                val sign = if (tx.type== TransactionType.INCOME) "+" else "-"
                println("${i+1}. ID:${tx.id.takeLast(5).uppercase()} ${tx.date} | ${tx.category} | $sign${tx.amount.toCurrency()} | ${tx.type}")
            }
        }
        val incomeTotal = account.transactions.filter { it.type== TransactionType.INCOME }.sumOf { it.amount }
        val expenseTotal = account.transactions.filter { it.type== TransactionType.EXPENSE }.sumOf { it.amount }
        println("\n📊 Totals:")
        println("➕ Total Income:  ${incomeTotal.toCurrency()}")
        println("➖ Total Expense: ${expenseTotal.toCurrency()}")
    }

}


//currency formatting

fun Double.toCurrency(): String = String.format("৳%.2f",this)

fun main() {
 val manager = FileManager()
    val tracker = FinanceTracker()
    println("Load saved account: y/n ")
    val input = readlnOrNull()
    if (input != null){
        when(input){
            "y" -> manager.loadFinanceData("finance-tracker.json")
            "n" -> println("Create new account: ")

        }
    }
}

/*
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
 */
