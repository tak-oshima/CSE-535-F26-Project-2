package edu.asu.group11.misere

data class GameState(
    val board: List<String> = List(9) { "" },
    val currentPlayer: String = "X",
    val loser: String? = null,
    val isDraw: Boolean = false
) {
    val isOver: Boolean
        get() = loser != null || isDraw

    fun playMove(cell: Int): GameState {
        if (isOver || cell !in board.indices || board[cell].isNotEmpty()) {
            return this
        }

        val nextBoard = board.toMutableList()
        nextBoard[cell] = currentPlayer

        // A line is a loss, even when it fills the last empty cell.
        if (hasLine(nextBoard, currentPlayer)) {
            return copy(board = nextBoard, loser = currentPlayer)
        }
        if (nextBoard.all { it.isNotEmpty() }) {
            return copy(board = nextBoard, isDraw = true)
        }

        return copy(
            board = nextBoard,
            currentPlayer = if (currentPlayer == "X") "O" else "X"
        )
    }

    private fun hasLine(board: List<String>, player: String): Boolean {
        val lines = listOf(
            listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
            listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
            listOf(0, 4, 8), listOf(2, 4, 6)
        )
        return lines.any { line -> line.all { board[it] == player } }
    }
}
