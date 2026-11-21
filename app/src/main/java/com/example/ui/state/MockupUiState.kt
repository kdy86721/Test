package com.example.ui.state

import android.content.Context
import android.os.SystemClock
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class AppScreen {
  Main,
  Menu,
}

data class MockupUiState(
  val currentScreen: AppScreen = AppScreen.Main,
  val isBlocked: Boolean = false,
  val blockedAtMillis: Long? = null,
  val blockedTimestampText: String = "",
  val elapsedSeconds: Long = 0L,
  val voiceAlertEnabled: Boolean = true,
  val vibrationAlertEnabled: Boolean = true,
  val installDateTime: String = DEFAULT_INSTALL_DATETIME,
  val manufacturer: String = DEFAULT_MANUFACTURER,
  val modelName: String = DEFAULT_MODEL_NAME,
  val osVersion: String = DEFAULT_OS_VERSION,
  val isEditDialogVisible: Boolean = false,
) {
  val elapsedDays: Long
    get() = elapsedSeconds / SECONDS_PER_DAY

  val elapsedTimeFormatted: String
    get() {
      val rem = elapsedSeconds % SECONDS_PER_DAY
      val hours = rem / 3_600L
      val minutes = (rem % 3_600L) / 60L
      val seconds = rem % 60L
      return String.format(Locale.US, "%02d : %02d : %02d", hours, minutes, seconds)
    }

  companion object {
    private const val SECONDS_PER_DAY = 86_400L
    const val DEFAULT_INSTALL_DATETIME = "26.06.16 09:01"
    const val DEFAULT_MANUFACTURER = "samsung"
    const val DEFAULT_MODEL_NAME = "SM-G991N"
    const val DEFAULT_OS_VERSION = "15"
  }
}

/**
 * Top-level UI-only ViewModel managing screen routing, persisted blocked/unblocked state,
 * wall-clock-backed real-time elapsed timer surviving background process death, settings toggles,
 * and hidden triple-tap display-info editor state.
 * Intentionally contains no real device camera blocking, GPS/location access, or audio control.
 */
class MockupViewModel(
  private val store: MockupStateStore,
  private val wallClockMs: () -> Long = System::currentTimeMillis,
  private val monoClockMs: () -> Long = SystemClock::elapsedRealtime,
) : ViewModel() {
  private val _uiState: MutableStateFlow<MockupUiState>
  val uiState: StateFlow<MockupUiState>

  private var timerJob: Job? = null

  // Tracks monotonic timestamps for triple-tap detection on the top-right "삭제" button
  private val deleteTapTimestamps = ArrayDeque<Long>()

  init {
    val saved = store.load()
    val restoredBlockedAt = if (saved.isBlocked) saved.blockedAtMillis else null
    val initialElapsedSeconds =
      if (restoredBlockedAt != null) {
        computeElapsedSeconds(restoredBlockedAt)
      } else {
        0L
      }
    val restoredTimestampText =
      if (restoredBlockedAt != null) {
        saved.blockedTimestampText.ifEmpty { formatBlockTimestamp(restoredBlockedAt) }
      } else {
        ""
      }

    _uiState =
      MutableStateFlow(
        MockupUiState(
          isBlocked = restoredBlockedAt != null,
          blockedAtMillis = restoredBlockedAt,
          blockedTimestampText = restoredTimestampText,
          elapsedSeconds = initialElapsedSeconds,
          voiceAlertEnabled = saved.voiceAlertEnabled,
          vibrationAlertEnabled = saved.vibrationAlertEnabled,
          installDateTime = saved.installDateTime,
          manufacturer = saved.manufacturer,
          modelName = saved.modelName,
          osVersion = saved.osVersion,
        ),
      )
    uiState = _uiState.asStateFlow()

    if (restoredBlockedAt != null) {
      startElapsedTicker(restoredBlockedAt)
    }
  }

  fun openMenu() {
    deleteTapTimestamps.clear()
    _uiState.update { it.copy(currentScreen = AppScreen.Menu) }
  }

  fun closeMenu() {
    _uiState.update { it.copy(currentScreen = AppScreen.Main) }
  }

  fun setBlocked(blocked: Boolean) {
    deleteTapTimestamps.clear()
    if (blocked) {
      if (_uiState.value.isBlocked) return
      val nowWallMs = wallClockMs()
      val formattedTimestamp = formatBlockTimestamp(nowWallMs)

      _uiState.update {
        it.copy(
          isBlocked = true,
          blockedAtMillis = nowWallMs,
          blockedTimestampText = formattedTimestamp,
          elapsedSeconds = 0L,
        )
      }
      persistCurrentState()
      startElapsedTicker(nowWallMs)
    } else {
      stopAndResetTimer()
      _uiState.update {
        it.copy(
          isBlocked = false,
          blockedAtMillis = null,
          blockedTimestampText = "",
          elapsedSeconds = 0L,
        )
      }
      persistCurrentState()
    }
  }

  /**
   * Immediately recalculates elapsed seconds from the persisted blocked wall-clock timestamp
   * when the app returns to the foreground after background sleep or Doze.
   */
  fun syncElapsedTime() {
    val current = _uiState.value
    val blockedAt = current.blockedAtMillis
    if (current.isBlocked && blockedAt != null) {
      val elapsedSec = computeElapsedSeconds(blockedAt)
      if (current.elapsedSeconds != elapsedSec) {
        _uiState.update { state ->
          if (state.isBlocked && state.blockedAtMillis == blockedAt) {
            state.copy(elapsedSeconds = elapsedSec)
          } else {
            state
          }
        }
      }
      if (timerJob?.isActive != true) {
        startElapsedTicker(blockedAt)
      }
    }
  }

  /**
   * Detects 3 consecutive taps within 1.5 seconds on the default Main Screen's "삭제" button.
   * Older taps outside the 1.5s window expire automatically.
   */
  fun onDeleteButtonTapped() {
    if (_uiState.value.isBlocked) return
    val nowMs = monoClockMs()
    while (deleteTapTimestamps.isNotEmpty() && nowMs - deleteTapTimestamps.first() > TRIPLE_TAP_WINDOW_MS) {
      deleteTapTimestamps.removeFirst()
    }
    deleteTapTimestamps.addLast(nowMs)
    if (deleteTapTimestamps.size >= TRIPLE_TAP_COUNT) {
      deleteTapTimestamps.clear()
      _uiState.update { it.copy(isEditDialogVisible = true) }
    }
  }

  fun dismissEditDialog() {
    _uiState.update { it.copy(isEditDialogVisible = false) }
  }

  fun saveEditableDisplayInfo(
    installDateTime: String,
    manufacturer: String,
    modelName: String,
    osVersion: String,
  ) {
    _uiState.update { current ->
      current.copy(
        installDateTime = installDateTime.trim().ifEmpty { current.installDateTime },
        manufacturer = manufacturer.trim().ifEmpty { current.manufacturer },
        modelName = modelName.trim().ifEmpty { current.modelName },
        osVersion = osVersion.trim().ifEmpty { current.osVersion },
        isEditDialogVisible = false,
      )
    }
    persistCurrentState()
  }

  fun toggleVoiceAlert() {
    _uiState.update { it.copy(voiceAlertEnabled = !it.voiceAlertEnabled) }
    persistCurrentState()
  }

  fun toggleVibrationAlert() {
    _uiState.update { it.copy(vibrationAlertEnabled = !it.vibrationAlertEnabled) }
    persistCurrentState()
  }

  private fun persistCurrentState() {
    val current = _uiState.value
    store.save(
      PersistedMockupState(
        isBlocked = current.isBlocked,
        blockedAtMillis = current.blockedAtMillis,
        blockedTimestampText = current.blockedTimestampText,
        voiceAlertEnabled = current.voiceAlertEnabled,
        vibrationAlertEnabled = current.vibrationAlertEnabled,
        installDateTime = current.installDateTime,
        manufacturer = current.manufacturer,
        modelName = current.modelName,
        osVersion = current.osVersion,
      ),
    )
  }

  private fun computeElapsedSeconds(blockedAtWallMs: Long): Long {
    val elapsedMs = (wallClockMs() - blockedAtWallMs).coerceAtLeast(0L)
    return elapsedMs / 1_000L
  }

  private fun startElapsedTicker(blockedAtWallMs: Long) {
    timerJob?.cancel()
    timerJob =
      viewModelScope.launch {
        while (isActive) {
          val elapsedMs = (wallClockMs() - blockedAtWallMs).coerceAtLeast(0L)
          val elapsedSec = elapsedMs / 1_000L
          _uiState.update { state ->
            if (state.isBlocked && state.elapsedSeconds != elapsedSec) {
              state.copy(elapsedSeconds = elapsedSec)
            } else {
              state
            }
          }
          val delayToNextSecond = 1_000L - (elapsedMs % 1_000L)
          delay(delayToNextSecond.coerceIn(50L, 1_000L))
        }
      }
  }

  private fun stopAndResetTimer() {
    timerJob?.cancel()
    timerJob = null
  }

  private fun formatBlockTimestamp(timeMillis: Long): String {
    val formatter = SimpleDateFormat("yy.MM.dd HH:mm", Locale.KOREA)
    return formatter.format(Date(timeMillis))
  }

  override fun onCleared() {
    stopAndResetTimer()
    super.onCleared()
  }

  companion object {
    private const val TRIPLE_TAP_WINDOW_MS = 1_500L
    private const val TRIPLE_TAP_COUNT = 3

    fun factory(context: Context): ViewModelProvider.Factory {
      val appContext = context.applicationContext
      return object : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
          require(modelClass.isAssignableFrom(MockupViewModel::class.java)) {
            "Unknown ViewModel class: ${modelClass.name}"
          }
          return MockupViewModel(store = SharedPreferencesMockupStateStore(appContext)) as T
        }
      }
    }
  }
}
