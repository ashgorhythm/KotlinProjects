package financetracker

data class income(
    val income: Double,
    val source: String
)
data class expense(
    val amount: Double,
    val catgory: String
)