package tictactoe

fun main() {

        val board = Array(3) { Array(3) { " " } }
        val game = TicTacToe()
        var currentPlayer = "X"

        game.printBoard(board)

        while (true) {
            game.updateBoard(board, currentPlayer)

            val winner = game.checkWin(board)
            if (winner != null) {
                println("Player $winner wins!")
                break
            } else if (game.isDraw(board)) {
                println("It's a draw!")
                break
            }

            currentPlayer = game.switchPlayer(currentPlayer)
        }
    }





class TicTacToe{
    //printing board
    fun printBoard(board: Array<Array<String>>){
        for (i in board.indices){
            println(board[i].joinToString(" | "))
            if (i < board.size-1){
                println("---------")
            }
        }
    }
    //making move
    fun makeMove(board: Array<Array<String>>, player: String): Pair<Int, Int> {
        while (true){
            println("Player $player,enter row (1-3)")
            val row = (readln().toInt()) - 1
            println("Player $player,enter column (1-3)")
            val col = (readln().toInt()) - 1

            if (row in 0..2 && col in 0..2){
                if(board[row][col] == " "){
                    return row to col
                }
                else println("Cell is taken.Select Another")
            }
            else {
                println("Invalid move.Try gain")
            }

        }

    }
    // updating board
    fun updateBoard(board: Array<Array<String>>,player: String){
        val (row,col) = makeMove(board,player)
        board[row][col] = player
        printBoard(board)
    }
    fun switchPlayer(player: String): String{
        return if (player == "X") "O" else "X"
    }
    fun checkWin(board: Array<Array<String>>): String?{
        //row check
        for (row in 0..2){
            if (board[row][0] == board[row][1] && board[row][1] == board[row][2] && board[row][0] != " "){
                return board[row][0]
            }
        }
        //column check
        for (col in 0..2){
            if (board[0][col] == board[1][col] && board[1][col] == board[2][col] && board[0][col] != " " ){
                return board[0][col]
            }
        }
        //diagonal
        if (board[0][0] == board[1][1] && board[1][1] == board[2][2] && board[0][0] != " "){
            return board[0][0]
        }
        if (board[0][2] == board[1][1] && board[1][1] == board[2][0] && board[0][2] != " "){
            return board[0][2]
        }
        return null
    }
    fun isDraw(board: Array<Array<String>>): Boolean {
        for (row in board) {
            for (cell in row) {
                if (cell == " ") return false
            }
        }
        return true
    }


}
