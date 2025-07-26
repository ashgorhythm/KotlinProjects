package todoapp

import java.io.File

data class Task(val title: String, var status: Boolean=false)

class TaskManager(private val filePath: String){
    private val tasks = mutableListOf<Task>()

    //loading all task when class is called
    init {
        tasks.addAll(loadTask(filePath))
    }
    //for loading the file
    fun loadTask(filePath: String): MutableList<Task>{
        val tasks = mutableListOf<Task>()
        val file = File(filePath)

        //to check if the file exist or not
        if (!file.exists()) return tasks

        //checking file
        file.forEachLine { line ->
            val parts = line.trim().split("::") //To avoid hidden bugs if a line has leading/trailing spaces
            if (parts.size==2) {
                val done = parts[0].toBooleanStrictOrNull() ?: false
                val title = parts[1]
                tasks.add(Task(title=title,status=done))
            }

        }
        return tasks

    }

    //saving task in the file
    fun saveTask(){
        val file = File(filePath)
        val content = tasks.joinToString(separator = "\n") { "${it.status}::${it.title}" }
        file.writeText(content)
    }

    //adding task
    fun addTask(title: String){
        val newTask = Task(title) //status is false
        tasks.add(newTask)
        saveTask() //save after adding

    }

    //to listing all task
    fun listTask(){
        if (tasks.isEmpty()){
            println("No task")
            return
        }
        for ((index,task) in tasks.withIndex()){
            val statusSymbol = if (task.status) "[✔]" else "[.]"
            println("$index . $statusSymbol ${task.title}")
        }
        println("Total: ${tasks.size}, Completed: ${tasks.count { it.status }}")

    }

    //for marking a task done
    fun markDone(index: Int){
        if (index<0 || index>= tasks.size){
            println("Invalid task number")
            return
        }
        val task = tasks[index]
        if (task.status){
            println("Task ${task.title} is already done.")
            return
        }
        task.status = true
        saveTask() //save after marking done
        println("${task.title} is marked as done.")
    }
}

/*
//Creating file
fun createFile(){
    val file = File("C:\\Users\\Dell\\IdeaProjects\\Projects\\src\\main\\kotlin\\ToDoApp\\task.txt")
    file.writeText("")
}
*/

fun main() {
    val filePath = "C:\\Users\\Dell\\IdeaProjects\\Projects\\src\\main\\kotlin\\ToDoApp\\task.txt"
    val manager = TaskManager(filePath)

    while (true){
        println("\n--- To-Do App ---")
        println("1. Add Task")
        println("2. List Tasks")
        println("3. Mark Task as Done")
        println("4. Exit")
        print("Enter your choice: ")

        when(readLine()?.trim()){
            "1" -> {
                println("Add task:")
                val title = readLine() ?: ""
                manager.addTask(title)

            }
            "2" -> {
                manager.listTask()
            }
            "3" -> {
                manager.listTask()
                print("Enter task number to mark as done: ")
                val input = readLine()
                val index = input?.toIntOrNull()
                if (index != null){
                    manager.markDone(index)
                }
                else println("Invalid input")
            }
            "4" -> {
                println("GoodBye")
                break
            }
            else -> println("Invalid option. Try again.")
        }
    }
}