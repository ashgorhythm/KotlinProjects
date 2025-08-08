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
            for((i,account) in data.accounts.withIndex()){
                println("${i+1}. ${account.name}")
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
    fun showAllTransaction(){
        data.transactions.forEach {
            println("${it.id} | ${it.amount.toCurrency()} | ${it.date} \n ${it.type} | ${it.category} ${it.description}")
        }
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
    val fileName = "finance-tracker.json"
    val tracker = FinanceTracker()
    var account: Account?
    println("${"-".repeat(10)} Welcome to Finance Tracker ${"_".repeat(10)}")
    val loadedData = manager.loadFinanceData(fileName)
    if (loadedData?.accounts.isNullOrEmpty()) {
        println("Yo Gorib \n You don't have any account ")
        println("Create an account(y/n)")
        val choice = readlnOrNull()
        if (choice != null) {
            when (choice.lowercase()) {
                "y" -> {
                    println("Enter account name: ")
                    val accName = readlnOrNull()
                    println("Enter initial amount:")
                    val initialBalance = readln().toDoubleOrNull() ?: 0.0
                    if (accName != null) {
                        account = Account(accName, initialBalance)
                        tracker.addAccount(account)
                    } else println("Invalid input.Try again.")
                }

                "n" -> return

            }
        } else println("Invalid input.")
    } else {
        tracker.data = loadedData
    }
    println("Select a default account:")
    tracker.printAllAccount()
    val input = readlnOrNull()?.toIntOrNull()
    if (input == null || input !in 1..tracker.data.accounts.size) {
        println("Invalid account selection.")
        return
    }
    var defaultAccount = tracker.data.accounts[input - 1]
    println("✅ '${defaultAccount.name}' set as default account.")
    while (true) {

        println(
            """
                ==== MENU ====
            1. Account Summary
            2. Add Transaction
            3. Set Budget
            4. Generate Report
            5. Print All Budget
            6. Print All Accounts 
            7. Save Data
            8. Load Data
            9. All Transactions
            10.Change Default Account
            11. Add Another Account
            0. Exit
            
        """.trimIndent()
        )
        println("Choose an Option")
        when (readln()) {
            "1" -> {
                tracker.printAccountSummary(defaultAccount.name)
            }

            "2" -> {
                println("➕ Add Transaction")
                println("Amount:")
                val amount = readln().toDouble()
                println("Description:")
                val desc = readlnOrNull() ?: ""
                println("Transaction type: 1.INCOME 2.EXPENSE ")
                val type = when (readln()) {
                    "1" -> TransactionType.INCOME
                    else -> TransactionType.EXPENSE
                }
                println("Select Category: 1) FOOD 2) SALARY 3) TRAVEL 4) ENTERTAINMENT 5) OTHERS")
                val category = when (readln()) {
                    "1" -> Category.FOOD
                    "2" -> Category.SALARY
                    "3" -> Category.TRAVEL
                    "4" -> Category.ENTERTAINMENT
                    else -> Category.OTHERS
                }
                val transaction = Transaction(amount = amount, description = desc, type = type, category = category)
                println("Choose an account: ")
                tracker.printAllAccount()
                tracker.addTransaction(transaction, defaultAccount)
                println("✅ Transaction added.")
            }

            "3" -> {
                println("Select Category: 1) FOOD 2) SALARY 3) TRAVEL 4) ENTERTAINMENT 5) OTHERS")
                val category = when (readln()) {
                    "1" -> Category.FOOD
                    "2" -> Category.SALARY
                    "3" -> Category.TRAVEL
                    "4" -> Category.ENTERTAINMENT
                    else -> Category.OTHERS
                }
                println("Set a limit:")
                val limit = readln().toDoubleOrNull() ?: 0.0
                println("Select Period:1.WEEKLY 2.MONTHLY 3.YEARLY")
                val period = when (readln()) {
                    "1" -> BudgetPeriod.WEEKLY
                    "2" -> BudgetPeriod.MONTHLY
                    else -> BudgetPeriod.YEARLY
                }
                tracker.setBudget(category, limit, period)
                println("✅ Budget set.")
            }

            "4" -> {
                println("Choose a report: 1.General Report 2.Category Report 3.Period Report")
                val reportChoice = readln()
                when (reportChoice) {
                    "1" -> {
                        println("📆 General Report")
                        val gReport = tracker.generateReport()
                        println("Total Income:${gReport.totalIncome.toCurrency()} ")
                        println("Total Expense:${gReport.totalExpense.toCurrency()} ")
                        println("Net Balance:${gReport.net.toCurrency()} ")
                        println("Total Transactions:${gReport.transactionCount}")
                    }

                    "2" -> {
                        println("Select Category: 1) FOOD 2) SALARY 3) TRAVEL 4) ENTERTAINMENT 5) OTHERS")
                        val reportCategory = when (readln()) {
                            "1" -> Category.FOOD
                            "2" -> Category.SALARY
                            "3" -> Category.TRAVEL
                            "4" -> Category.ENTERTAINMENT
                            else -> Category.OTHERS
                        }
                        val cReport = tracker.generateCategoryReport(reportCategory)
                        println("${"-".repeat(5)}Category Report(${cReport.category})${"-".repeat(5)}")
                        println("Total Income:${cReport.totalIncome.toCurrency()} ")
                        println("Total Expense:${cReport.totalExpense.toCurrency()} ")
                        println("Net Balance:${cReport.net.toCurrency()} ")
                    }

                    "3" -> {
                        println("Select Period:1.WEEKLY 2.MONTHLY 3.YEARLY")
                        val period = when (readln()) {
                            "1" -> BudgetPeriod.WEEKLY
                            "2" -> BudgetPeriod.MONTHLY
                            else -> BudgetPeriod.YEARLY
                        }
                        val pReport = tracker.generatePeriodReport(period)
                        println("${"-".repeat(5)} ${pReport.period} Report ${"-".repeat(5)}")
                        println("From ${pReport.from} to ${pReport.to}")
                        println("Total Income:${pReport.totalIncome.toCurrency()} ")
                        println("Total Expense:${pReport.totalExpense.toCurrency()} ")
                        println("Net Balance:${pReport.net.toCurrency()} ")

                    }
                }

            }

            "5" -> {
                println("All Budgets:")
                tracker.printAllBudget()
            }

            "6" -> {
                tracker.printAllAccount()
            }

            "7" -> {
                manager.saveFinanceData(tracker.data, fileName)
                println("✅ Account data saved to file.")
            }

            "8" -> {
                val loaded = manager.loadFinanceData(fileName)
                if (loaded != null) {
                    tracker.data = loaded
                    println("✅ Data loaded.")
                } else println("❌ No saved data found.")

            }
            "9" -> {
                tracker.showAllTransaction()
            }
            "10" -> {
                println("Change default account:")
                tracker.printAllAccount()
                val newAccount = readln().toIntOrNull()
                if (newAccount!=null && newAccount in 1..tracker.data.accounts.size){
                    defaultAccount = tracker.data.accounts[newAccount-1]
                    println("✅ Default account changed to ${defaultAccount.name}")
                } else {
                    println("❌ Invalid selection.")
                }
            }
            "11" -> {
                println("Enter new account name:")
                val newName = readln()
                println("Enter account balance:")
                val newBalance = readln().toDouble()
                val newAccount = Account(newName,newBalance)
                tracker.addAccount(newAccount)
                println("$newName added with balance ${newBalance.toCurrency()}")
            }

            "0" -> {
                println("👋 Exiting. Goodbye!")
                break

            }
        }


    }
}
