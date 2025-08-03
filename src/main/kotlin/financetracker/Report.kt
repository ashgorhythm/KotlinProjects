package financetracker

import kotlinx.serialization.Serializable
import java.time.LocalDate
@Serializable
sealed class Report {
    @Serializable
    data class CategoryReport(
        val category: Category,
        val totalIncome: Double,
        val totalExpense: Double,
        val net: Double
    ) : Report()
    @Serializable
    data class PeriodReport(
        val period: BudgetPeriod,
        val from: String,
        val to: String,
        val totalIncome: Double,
        val totalExpense: Double,
        val net: Double
    ): Report()
    @Serializable
    data class GeneralReport(
        val totalIncome: Double,
        val totalExpense: Double,
        val net: Double,
        val transactionCount: Int
    ) : Report()




}