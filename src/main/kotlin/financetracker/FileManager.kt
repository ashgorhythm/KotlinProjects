package financetracker

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class FileManager {
    val json = Json {prettyPrint = true}

    fun saveAccount(account: Account,filename: String){
        val jsonData = json.encodeToString(account)
        File(filename).writeText(jsonData)
    }
    fun loadAccount(filename: String): Account? {
        val file = File(filename)
        if (!file.exists()) return null
        val jsonData = file.readText()
        return json.decodeFromString(jsonData)
    }
    fun saveTransaction(transaction: List<Transaction>,filename: String){
        val jsonData = json.encodeToString(transaction)
        File(filename).writeText(jsonData)
    }
    fun loadTransaction(filename: String): List<Transaction>?{
        val file = File(filename)
        if (!file.exists()) return null
        val jsonData = file.readText()
        return json.decodeFromString(jsonData)
    }
}
