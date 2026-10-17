package edu.asu.group11.misere

data class GameState(
    val board: List<String> = List(9) { "" },
    val currentPlayer: String = "X"
) {
    fun playMove(cell: Int): GameState {
        if (cell !in board.indices || board[cell].isNotEmpty()) {
            return this
        }

        val nextBoard = board.toMutableList()
        nextBoard[cell] = currentPlayer
        return copy(
            board = nextBoard,
            currentPlayer = if (currentPlayer == "X") "O" else "X"
        )
    }
}
