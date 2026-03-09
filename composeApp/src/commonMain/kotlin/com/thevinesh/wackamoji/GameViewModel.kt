package com.thevinesh.wackamoji

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

// ─── Constants ───────────────────────────────────────────────────────────────

const val MOLE_UP_TIME_MIN_MS = 600L
const val MOLE_UP_TIME_MAX_MS = 1200L
const val DELAY_BETWEEN_MOLES_MS = 80L
const val GAME_DURATION_SECONDS = 30

internal val LEVEL_THRESHOLDS = listOf(0, 5, 15, 30, 50, 75)

// ─── Pure helpers (easily testable) ──────────────────────────────────────────

internal fun calculateLevel(score: Int): Int {
    return LEVEL_THRESHOLDS.indexOfLast { score >= it }.coerceAtLeast(0) + 1
}

internal fun maxMolesForLevel(level: Int): Int {
    return when {
        level >= 5 -> 3
        level >= 3 -> 2
        else -> 1
    }
}

internal fun moleUpTimeRange(level: Int): Pair<Long, Long> {
    return when {
        level >= 5 -> 300L to 600L
        level >= 4 -> 400L to 800L
        level >= 3 -> 500L to 900L
        level >= 2 -> 500L to 1000L
        else -> MOLE_UP_TIME_MIN_MS to MOLE_UP_TIME_MAX_MS
    }
}

internal fun randomMoleEmoji(): String {
    val pool = listOf("😡", "😂", "🙄", "😅", "🤪", "😤", "🥴", "😎")
    return pool[Random.nextInt(pool.size)]
}

internal enum class ScreenshotScenario(val launchValue: String) {
    Gameplay("gameplay"),
    GameOver("game-over"),
}

private val screenshotEmojis = listOf("😎", "🤪", "😂", "😤", "🥴", "😅", "🙄", "😡", "😎")

internal fun screenshotScenarioFromLaunchValue(value: String?): ScreenshotScenario? =
    ScreenshotScenario.entries.firstOrNull { it.launchValue == value }

internal fun screenshotStateForScenario(scenario: ScreenshotScenario): GameUiState =
    when (scenario) {
        ScreenshotScenario.Gameplay -> GameUiState(
            score = 12,
            running = true,
            timeLeft = 9,
            gameOver = false,
            cells = listOf(false, false, true, false, false, false, false, false, false),
            emojis = screenshotEmojis,
        )

        ScreenshotScenario.GameOver -> GameUiState(
            score = 32,
            running = false,
            timeLeft = 0,
            gameOver = true,
            cells = List(9) { false },
            emojis = screenshotEmojis,
        )
    }

private fun initialGameUiState(screenshotScenario: ScreenshotScenario?): GameUiState =
    screenshotScenario?.let(::screenshotStateForScenario) ?: GameUiState()

// ─── UI State ────────────────────────────────────────────────────────────────

data class GameUiState(
    val score: Int = 0,
    val running: Boolean = true,
    val timeLeft: Int = GAME_DURATION_SECONDS,
    val gameOver: Boolean = false,
    val cells: List<Boolean> = List(9) { false },
    val emojis: List<String> = List(9) { randomMoleEmoji() },
) {
    val level: Int get() = calculateLevel(score)
    val timerFraction: Float get() = timeLeft.toFloat() / GAME_DURATION_SECONDS.toFloat()
}

// ─── ViewModel ───────────────────────────────────────────────────────────────

class GameViewModel internal constructor(
    private val screenshotScenario: ScreenshotScenario? = null,
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialGameUiState(screenshotScenario))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private var spawnJob: Job? = null

    init {
        if (screenshotScenario == null) {
            startGameLoops()
        }
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    fun onMoleHit(index: Int) {
        _uiState.update { state ->
            if (!state.cells[index] || state.gameOver) return@update state
            state.copy(
                score = state.score + 1,
                cells = state.cells.toMutableList().also { it[index] = false },
            )
        }
    }

    fun onRestart() {
        if (screenshotScenario != null) {
            _uiState.value = screenshotStateForScenario(screenshotScenario)
            stopGameLoops()
            return
        }

        _uiState.value = GameUiState()
        startGameLoops()
    }

    fun onPauseResume() {
        if (screenshotScenario != null) return

        _uiState.update { state ->
            if (state.gameOver) return@update state
            state.copy(running = !state.running)
        }
        val state = _uiState.value
        if (state.running && !state.gameOver) {
            startGameLoops()
        } else {
            stopGameLoops()
        }
    }

    // ── Internal loops ───────────────────────────────────────────────────────

    private fun startGameLoops() {
        stopGameLoops()
        timerJob = viewModelScope.launch { runTimer() }
        spawnJob = viewModelScope.launch { runMoleSpawner() }
    }

    private fun stopGameLoops() {
        timerJob?.cancel()
        spawnJob?.cancel()
    }

    private suspend fun runTimer() {
        while (true) {
            val state = _uiState.value
            if (!state.running || state.gameOver) return
            delay(1000L)
            _uiState.update { s ->
                if (!s.running || s.gameOver) return@update s
                val newTime = s.timeLeft - 1
                if (newTime <= 0) {
                    s.copy(
                        timeLeft = 0,
                        gameOver = true,
                        running = false,
                        cells = List(9) { false },
                    )
                } else {
                    s.copy(timeLeft = newTime)
                }
            }
            if (_uiState.value.gameOver) return
        }
    }

    private suspend fun runMoleSpawner() {
        val remaining = LongArray(9)
        while (true) {
            val state = _uiState.value
            if (!state.running || state.gameOver) return

            val currentLevel = state.level
            val maxMoles = maxMolesForLevel(currentLevel)
            val (minTime, maxTime) = moleUpTimeRange(currentLevel)

            val newCells = state.cells.toMutableList()

            // Tick down mole visibility timers
            for (i in 0 until 9) {
                if (newCells[i]) {
                    remaining[i] = (remaining[i] - DELAY_BETWEEN_MOLES_MS).coerceAtLeast(0L)
                    if (remaining[i] == 0L) newCells[i] = false
                }
            }

            // Maybe spawn a new mole
            val upCount = newCells.count { it }
            val newEmojis = state.emojis.toMutableList()
            if (upCount < maxMoles) {
                val idx = Random.nextInt(0, 9)
                if (!newCells[idx]) {
                    newCells[idx] = true
                    newEmojis[idx] = randomMoleEmoji()
                    remaining[idx] = Random.nextLong(minTime, maxTime)
                }
            }

            _uiState.update { s ->
                if (!s.running || s.gameOver) return@update s
                s.copy(cells = newCells, emojis = newEmojis)
            }

            delay(DELAY_BETWEEN_MOLES_MS)
        }
    }
}
