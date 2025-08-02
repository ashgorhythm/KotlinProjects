package financetracker

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class FileManager {
    val json = Json {prettyPrint = true}

    fun saveFinanceData(data: FinanceData, fileName: String){
        val jsonData = json.encodeToString(data)
        File(fileName).writeText(jsonData)
    }
    fun loadFinanceData(fileName: String): FinanceData?{
        val file = File(fileName)
        if (!file.exists()) return null
        val jsonData = file.readText()
        return json.decodeFromString(jsonData)
    }
}
