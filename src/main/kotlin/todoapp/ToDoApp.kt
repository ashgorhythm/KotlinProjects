package todoapp

import java.io.File

data class Task(var title: String, var status: Boolean=false)

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
            val statusSymbol = if (task.status) "[✔]" else "[❌]"
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
    //removing task
    fun removeTask(index: Int){
        if (index < 0 || index >= tasks.size) {
            println("Invalid task number.")
            return
        }
        val removedTask = tasks[index]
        tasks.removeAt(index)
        saveTask()
        println("${removedTask.title} is removed")
    }
    //mark as undone
    fun markUndone(index: Int){
        if (index < 0 || index >= tasks.size) {
            println("Invalid task number.")
            return
        }
        val task = tasks[index]
        if (!task.status){
            println("${task.title} is already undone.")
            return
        }
        task.status = false
        saveTask()
        println("${task.title} is marked as undone")

    }
    //completed task list
    fun completedList(){
        val completed = tasks.filter { it.status }
        if (completed.isEmpty()){
            println("No completed task")
            return
        }
        println("Completed task: ")
        for ((index,task) in completed.withIndex()){
            println("$index. [✔] ${task.title}")
        }

    }
    //incompleted task list
    fun incompletedList(){
        val incompleted = tasks.filter { !it.status }
        if (incompleted.isEmpty()){
            println("No incompleted task")
            return
        }
        println("Incompleted task: ")
        for ((index,task) in incompleted.withIndex()){
            println("$index. [X] ${task.title}")
        }

    }
    //for searching a task
    fun searchTask(keyword: String){
        val result = tasks.filter { it.title.contains(keyword, ignoreCase = true) }
        if (result.isEmpty()){
            println("No task found with keyword $keyword")
        }
        else result.forEachIndexed { index, task -> println("$index. ${task.title}") }
    }
    //editing task
    fun editTask(index: Int, newTitle: String){
        if (index in tasks.indices){
            tasks[index].title = newTitle
            saveTask()
            println("Title updated...")
        }
        else println("Invalid index")
    }
    //deleting all task
    fun deleteAll(){
        tasks.removeAll(tasks)
        saveTask()
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
        println("4. Remove Task")
        println("5. Mark as Undone")
        println("6. List Completed Tasks")
        println("7. List Uncompleted Tasks")
        println("8. Search a Task")
        println("9. Edit Task")
        println("10. Delete all Task")
        println("11. Exit")
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
                manager.listTask()
                print("Enter the task number you want to remove: ")
                val input = readLine()
                val index = input?.toIntOrNull()
                if (index!=null){
                    manager.removeTask(index)
                }
                else println("Invalid input.")
            }
            "5" -> {
                manager.listTask()
                print("Enter task number to mark as undone: ")
                val input = readLine()
                val index = input?.toIntOrNull()
                if (index != null){
                    manager.markUndone(index)
                }
                else println("Invalid input")

            }
            "6" -> {
               manager.completedList()
            }
            "7" -> {
                manager.incompletedList()
            }
            "8" -> {

                print("Enter keyword: ")
                val input = readLine()
                if (input== null) println("Enter a valid keywork")
                else manager.searchTask(input)
            }
            "9" -> {
                manager.listTask()
                print("Enter the number you want to edit:")
                val index = readLine()?.toIntOrNull()
                if (index!=null){
                    print("Enter your task: ")
                    val newTitle = readLine()?: ""
                    manager.editTask(index,newTitle)
                    println("Your edited task is $index. $newTitle")
                }

            }
            "10" -> {
                manager.deleteAll()
            }
            "11" -> {
                println("GoodBye")
                break
            }
            else -> println("Invalid option. Try again.")
        }
    }
}