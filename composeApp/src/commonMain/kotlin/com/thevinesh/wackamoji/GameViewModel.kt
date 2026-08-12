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
const val ENDLESS_START_LIVES = 3

internal val LEVEL_THRESHOLDS = listOf(0, 5, 15, 30, 50, 75)

enum class GameMode {
    Classic,
    Endless,
}

enum class PowerUpType {
    ExtraLife,    // ❤️
    Slowdown,     // 💊
    Freeze,       // ❄️
}

enum class CellContentType {
    Emoji,
    PowerUp,
}

data class CellState(
    val isUp: Boolean = false,
    val contentType: CellContentType = CellContentType.Emoji,
    val content: String = "",
    val powerUpType: PowerUpType? = null,
)

internal fun gameOverTitle(mode: GameMode): String = when (mode) {
    GameMode.Classic -> "TIME'S UP!"
    GameMode.Endless -> "OUT OF LIVES!"
}

internal fun gameOverEmoji(mode: GameMode): String = when (mode) {
    GameMode.Classic -> "⏰"
    GameMode.Endless -> "💔"
}

internal fun freshGameUiState(mode: GameMode): GameUiState = GameUiState(
    mode = mode,
    lives = ENDLESS_START_LIVES,
)

internal fun comboBonusPoints(streak: Int): Int = when {
    streak >= 10 -> 3
    streak >= 5 -> 2
    streak >= 3 -> 1
    else -> 0
}

internal fun pointsForHit(currentCombo: Int): Int {
    val nextCombo = currentCombo + 1
    return 1 + comboBonusPoints(nextCombo)
}

internal fun applyMoleTimeouts(
    state: GameUiState,
    missedCount: Int,
    cells: List<CellState>,
): GameUiState {
    val comboAfterMiss = if (missedCount > 0) 0 else state.combo
    if (state.mode != GameMode.Endless || missedCount <= 0) {
        return state.copy(cells = cells, combo = comboAfterMiss)
    }

    val newLives = (state.lives - missedCount).coerceAtLeast(0)
    return if (newLives == 0) {
        state.copy(
            cells = List(9) { CellState() },
            lives = 0,
            combo = 0,
            gameOver = true,
            running = false,
        )
    } else {
        state.copy(cells = cells, lives = newLives, combo = comboAfterMiss)
    }
}

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

internal fun powerUpEmoji(type: PowerUpType): String = when (type) {
    PowerUpType.ExtraLife -> "❤️"
    PowerUpType.Slowdown -> "💊"
    PowerUpType.Freeze -> "❄️"
}

internal fun randomPowerUpType(): PowerUpType {
    val types = PowerUpType.entries
    return types[Random.nextInt(types.size)]
}

internal enum class ScreenshotScenario(val launchValue: String) {
    Gameplay("gameplay"),
    GameOver("game-over"),
    Settings("settings"),
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
            cells = listOf(
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "😎"),
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "🤪"),
                CellState(isUp = true, contentType = CellContentType.Emoji, content = "😂"),
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "😤"),
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "🥴"),
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "😅"),
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "🙄"),
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "😡"),
                CellState(isUp = false, contentType = CellContentType.Emoji, content = "😎"),
            ),
        )

        ScreenshotScenario.GameOver -> GameUiState(
            score = 32,
            running = false,
            timeLeft = 0,
            gameOver = true,
            cells = List(9) { CellState() },
        )

        ScreenshotScenario.Settings -> GameUiState(
            running = false,
            cells = List(9) { CellState() },
        )
    }

private fun initialGameUiState(
    screenshotScenario: ScreenshotScenario?,
    mode: GameMode,
): GameUiState =
    screenshotScenario?.let(::screenshotStateForScenario) ?: freshGameUiState(mode)

private fun shouldAutoplayBackgroundMusic(
    screenshotScenario: ScreenshotScenario?,
    backgroundMusicAutoplayEnabled: Boolean,
): Boolean = screenshotScenario == null && backgroundMusicAutoplayEnabled

private fun initialBackgroundMusicState(
    screenshotScenario: ScreenshotScenario?,
    backgroundMusicAutoplayEnabled: Boolean,
): BackgroundMusicState {
    return if (shouldAutoplayBackgroundMusic(screenshotScenario, backgroundMusicAutoplayEnabled)) {
        BackgroundMusicState(playback = BackgroundMusicPlayback.Playing)
    } else {
        BackgroundMusicState()
    }
}

// ─── UI State ────────────────────────────────────────────────────────────────

data class GameUiState(
    val score: Int = 0,
    val running: Boolean = true,
    val timeLeft: Int = GAME_DURATION_SECONDS,
    val gameOver: Boolean = false,
    val mode: GameMode = GameMode.Classic,
    val lives: Int = ENDLESS_START_LIVES,
    val combo: Int = 0,
    val cells: List<CellState> = List(9) { CellState() },
    val powerUpSlowdownTicksRemaining: Int = 0,
    val powerUpFreezeTicksRemaining: Int = 0,
    val lastPickedUpPowerUp: Pair<PowerUpType, Int>? = null, // (type, cellIndex)
    val lastHitCellIndex: Int? = null, // cell of the most recent scored mole hit
    val lastScoreDelta: Int? = null, // points gained on the most recent scored hit
    val scoredHitCount: Int = 0, // total scored mole hits (unique trigger per hit)
) {
    val level: Int get() = calculateLevel(score)
    val timerFraction: Float get() = timeLeft.toFloat() / GAME_DURATION_SECONDS.toFloat()
    
    val activePowerUps: List<ActivePowerUp>
        get() = buildList {
            if (powerUpSlowdownTicksRemaining > 0) {
                add(ActivePowerUp(PowerUpType.Slowdown, powerUpSlowdownTicksRemaining))
            }
            if (powerUpFreezeTicksRemaining > 0) {
                add(ActivePowerUp(PowerUpType.Freeze, powerUpFreezeTicksRemaining))
            }
        }
}

internal const val TICKS_PER_SECOND = 1000 / DELAY_BETWEEN_MOLES_MS.toInt()
internal const val POWER_UP_DURATION_TICKS = 10 * TICKS_PER_SECOND

// ─── ViewModel ───────────────────────────────────────────────────────────────

class GameViewModel internal constructor(
    private val screenshotScenario: ScreenshotScenario? = null,
    private val mode: GameMode = GameMode.Classic,
    private val backgroundMusicAutoplayEnabled: Boolean = screenshotScenario == null,
    private val soundEffectPlayer: SoundEffectPlayer = NoOpSoundEffectPlayer,
    private val hapticFeedback: HapticFeedback = NoOpHapticFeedback,
    private val hapticsEnabled: () -> Boolean = { true },
) : ViewModel() {

    private val _uiState = MutableStateFlow(initialGameUiState(screenshotScenario, mode))
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val _backgroundMusicState = MutableStateFlow(
        initialBackgroundMusicState(
            screenshotScenario = screenshotScenario,
            backgroundMusicAutoplayEnabled = backgroundMusicAutoplayEnabled,
        )
    )
    val backgroundMusicState: StateFlow<BackgroundMusicState> = _backgroundMusicState.asStateFlow()

    private var timerJob: Job? = null
    private var spawnJob: Job? = null

    init {
        if (screenshotScenario == null) {
            startGameLoops()
        }
    }

    // ── Actions ──────────────────────────────────────────────────────────────

    fun onMoleHit(index: Int) {
        var scoredHit = false
        _uiState.update { state ->
            val cell = state.cells[index]
            if (!cell.isUp || state.gameOver || !state.running) return@update state

            when (cell.contentType) {
                CellContentType.Emoji -> {
                    scoredHit = true
                    val nextCombo = state.combo + 1
                    val gained = pointsForHit(state.combo)
                    state.copy(
                        score = state.score + gained,
                        combo = nextCombo,
                        lastHitCellIndex = index,
                        lastScoreDelta = gained,
                        scoredHitCount = state.scoredHitCount + 1,
                        cells = state.cells.toMutableList().also { it[index] = CellState() },
                    )
                }
                CellContentType.PowerUp -> {
                    val updatedState = applyPowerUp(state, cell.powerUpType!!)
                    updatedState.copy(
                        cells = updatedState.cells.toMutableList().also { it[index] = CellState() },
                        lastPickedUpPowerUp = cell.powerUpType to index
                    )
                }
            }
        }
        if (scoredHit) {
            soundEffectPlayer.play(SoundEffect.Wack)
            if (hapticsEnabled()) {
                hapticFeedback.performLightImpact()
            }
        }
    }

    private fun applyPowerUp(state: GameUiState, type: PowerUpType): GameUiState = when (type) {
        PowerUpType.ExtraLife -> {
            if (state.mode == GameMode.Endless) {
                state.copy(lives = state.lives + 1)
            } else {
                state
            }
        }
        PowerUpType.Slowdown -> {
            state.copy(powerUpSlowdownTicksRemaining = POWER_UP_DURATION_TICKS)
        }
        PowerUpType.Freeze -> {
            state.copy(powerUpFreezeTicksRemaining = POWER_UP_DURATION_TICKS)
        }
    }

    fun onRestart() {
        if (screenshotScenario != null) {
            _uiState.value = screenshotStateForScenario(screenshotScenario)
            stopGameLoops()
            return
        }

        val currentMode = _uiState.value.mode
        _uiState.value = freshGameUiState(currentMode)
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
    
    fun clearLastPickupTrigger() {
        _uiState.update { it.copy(lastPickedUpPowerUp = null) }
    }

    fun onAppForegrounded() {
        updateBackgroundMusicPlayback(BackgroundMusicPlayback.Playing)
    }

    fun onAppBackgrounded() {
        updateBackgroundMusicPlayback(BackgroundMusicPlayback.Paused)
    }

    override fun onCleared() {
        stopGameLoops()
        _backgroundMusicState.value = _backgroundMusicState.value.copy(
            playback = BackgroundMusicPlayback.Stopped,
        )
        super.onCleared()
    }

    // ── Internal loops ───────────────────────────────────────────────────────

    private fun updateBackgroundMusicPlayback(playback: BackgroundMusicPlayback) {
        if (!shouldAutoplayBackgroundMusic(screenshotScenario, backgroundMusicAutoplayEnabled)) {
            return
        }

        _backgroundMusicState.update { state ->
            if (state.playback == playback) {
                state
            } else {
                state.copy(playback = playback)
            }
        }
    }

    private fun startGameLoops() {
        stopGameLoops()
        if (_uiState.value.mode == GameMode.Classic) {
            timerJob = viewModelScope.launch { runTimer() }
        }
        spawnJob = viewModelScope.launch { runMoleSpawner() }
    }

    private fun stopGameLoops() {
        timerJob?.cancel()
        spawnJob?.cancel()
    }

    private suspend fun runTimer() {
        while (true) {
            val state = _uiState.value
            if (!state.running || state.gameOver || state.mode != GameMode.Classic) return
            delay(1000L)
            _uiState.update { s ->
                if (!s.running || s.gameOver || s.mode != GameMode.Classic) return@update s
                val newTime = s.timeLeft - 1
                if (newTime <= 0) {
                    s.copy(
                        timeLeft = 0,
                        gameOver = true,
                        running = false,
                        cells = List(9) { CellState() },
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
            
            // Check if power-ups are active and tick them down
            val isSlowdownActive = state.powerUpSlowdownTicksRemaining > 0
            val isFreezeActive = state.powerUpFreezeTicksRemaining > 0
            
            val (minTime, maxTime) = moleUpTimeRange(currentLevel)
            val adjustedMinTime = if (isSlowdownActive) minTime * 2 else minTime
            val adjustedMaxTime = if (isSlowdownActive) maxTime * 2 else maxTime

            var newCells: MutableList<CellState>? = null
            var missedCount = 0

            // Tick down mole visibility timers (skip if freeze is active for emoji cells)
            for (i in 0 until 9) {
                val cell = state.cells[i]
                if (cell.isUp) {
                    // Power-ups always tick down, emojis freeze when freeze is active
                    val shouldTick = cell.contentType == CellContentType.PowerUp || !isFreezeActive
                    
                    if (shouldTick) {
                        remaining[i] = (remaining[i] - DELAY_BETWEEN_MOLES_MS).coerceAtLeast(0L)
                        if (remaining[i] == 0L) {
                            if (newCells == null) newCells = state.cells.toMutableList()
                            newCells[i] = CellState()
                            // Only count as missed if it's an emoji (not a power-up)
                            if (cell.contentType == CellContentType.Emoji) {
                                missedCount++
                            }
                        }
                    }
                }
            }

            // Maybe spawn a new mole or power-up
            val cellsToCount = newCells ?: state.cells
            val upCount = cellsToCount.count { it.isUp }
            if (upCount < maxMoles) {
                val idx = Random.nextInt(0, 9)
                if (!cellsToCount[idx].isUp) {
                    if (newCells == null) newCells = state.cells.toMutableList()
                    
                    // In endless mode, 5% chance to spawn a power-up instead of emoji
                    val shouldSpawnPowerUp = state.mode == GameMode.Endless && Random.nextFloat() < 0.05f
                    
                    if (shouldSpawnPowerUp) {
                        val powerUpType = randomPowerUpType()
                        newCells[idx] = CellState(
                            isUp = true,
                            contentType = CellContentType.PowerUp,
                            content = powerUpEmoji(powerUpType),
                            powerUpType = powerUpType,
                        )
                    } else {
                        val emoji = if (isFreezeActive) "🥶" else randomMoleEmoji()
                        newCells[idx] = CellState(
                            isUp = true,
                            contentType = CellContentType.Emoji,
                            content = emoji,
                        )
                    }
                    remaining[idx] = Random.nextLong(adjustedMinTime, adjustedMaxTime)
                }
            }
            
            // Apply freeze effect to already-visible emojis
            if (isFreezeActive && newCells == null) {
                for (i in 0 until 9) {
                    val cell = state.cells[i]
                    if (cell.isUp && cell.contentType == CellContentType.Emoji && cell.content != "🥶") {
                        if (newCells == null) newCells = state.cells.toMutableList()
                        newCells[i] = cell.copy(content = "🥶")
                    }
                }
            }

            // Update state and tick down power-up timers
            if (newCells != null || missedCount > 0 || isSlowdownActive || isFreezeActive) {
                val finalCells = newCells ?: state.cells
                _uiState.update { s ->
                    if (!s.running || s.gameOver) return@update s
                    val updated = applyMoleTimeouts(
                        state = s,
                        missedCount = missedCount,
                        cells = finalCells,
                    )
                    // Decrement power-up ticks
                    updated.copy(
                        powerUpSlowdownTicksRemaining = (updated.powerUpSlowdownTicksRemaining - 1).coerceAtLeast(0),
                        powerUpFreezeTicksRemaining = (updated.powerUpFreezeTicksRemaining - 1).coerceAtLeast(0),
                    )
                }
            }

            delay(DELAY_BETWEEN_MOLES_MS)
        }
    }

    companion object {
        internal fun createForTest(
            initialState: GameUiState,
            soundEffectPlayer: SoundEffectPlayer = NoOpSoundEffectPlayer,
            hapticFeedback: HapticFeedback = NoOpHapticFeedback,
            hapticsEnabled: () -> Boolean = { true },
        ): GameViewModel {
            val viewModel = GameViewModel(
                mode = initialState.mode,
                backgroundMusicAutoplayEnabled = false,
                soundEffectPlayer = soundEffectPlayer,
                hapticFeedback = hapticFeedback,
                hapticsEnabled = hapticsEnabled,
            )
            viewModel.stopGameLoops()
            viewModel._uiState.value = initialState
            return viewModel
        }
    }
}
