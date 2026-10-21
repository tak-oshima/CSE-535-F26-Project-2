package edu.asu.group11.misere

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme(colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    GameScreen()
                }
            }
        }
    }
}

@Composable
private fun GameScreen() {
    var game by rememberSaveable(stateSaver = GameStateSaver) { mutableStateOf(GameState()) }
    val loser = game.loser
    val status = when {
        loser != null -> stringResource(R.string.loss_message, loser, if (loser == "X") "O" else "X")
        game.isDraw -> stringResource(R.string.draw_message)
        else -> stringResource(R.string.turn_message, game.currentPlayer)
    }

    Column(
        modifier = Modifier.fillMaxSize()
            .safeDrawingPadding()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(stringResource(R.string.group_label), style = MaterialTheme.typography.labelLarge)
        Text(
            stringResource(R.string.project_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        Text(stringResource(R.string.game_rule), textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(status, style = MaterialTheme.typography.titleMedium, textAlign = TextAlign.Center)
        GameBoard(game) { cell -> game = game.playMove(cell) }
        Spacer(Modifier.height(8.dp))
        Button(onClick = { game = GameState() }) {
            Text(stringResource(R.string.reset_game))
        }
        Text(stringResource(R.string.local_game_description), textAlign = TextAlign.Center)
    }
}

@Composable
private fun GameBoard(game: GameState, onCellClick: (Int) -> Unit) {
    Column(
        modifier = Modifier.widthIn(max = 320.dp)
            .fillMaxWidth()
            .aspectRatio(1f),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(3) { row ->
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                repeat(3) { column ->
                    val cell = row * 3 + column
                    val mark = game.board[cell]
                    val description = stringResource(
                        R.string.cell_description, row + 1, column + 1,
                        mark.ifEmpty { stringResource(R.string.empty_cell) }
                    )
                    Box(
                        Modifier.weight(1f).aspectRatio(1f)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            .clip(RoundedCornerShape(8.dp))
                            .clickable(
                                enabled = !game.isOver && mark.isEmpty(),
                                role = Role.Button,
                                onClick = { onCellClick(cell) }
                            )
                            .semantics { contentDescription = description },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(mark, style = MaterialTheme.typography.displayMedium)
                    }
                }
            }
        }
    }
}

// Keep the current game when the screen is rotated.
private val GameStateSaver = listSaver<GameState, String>(
    save = { it.board + listOf(it.currentPlayer, it.loser ?: "", it.isDraw.toString()) },
    restore = {
        GameState(
            board = it.take(9),
            currentPlayer = it[9],
            loser = it[10].ifEmpty { null },
            isDraw = it[11].toBoolean()
        )
    }
)
