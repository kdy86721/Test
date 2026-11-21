package com.example.ui.state

import android.content.Context

data class PersistedMockupState(
  val isBlocked: Boolean = false,
  val blockedAtMillis: Long? = null,
  val blockedTimestampText: String = "",
  val voiceAlertEnabled: Boolean = true,
  val vibrationAlertEnabled: Boolean = true,
  val installDateTime: String = MockupUiState.DEFAULT_INSTALL_DATETIME,
  val manufacturer: String = MockupUiState.DEFAULT_MANUFACTURER,
  val modelName: String = MockupUiState.DEFAULT_MODEL_NAME,
  val osVersion: String = MockupUiState.DEFAULT_OS_VERSION,
)

interface MockupStateStore {
  fun load(): PersistedMockupState

  fun save(state: PersistedMockupState)
}

class SharedPreferencesMockupStateStore(context: Context) : MockupStateStore {
  private val prefs =
    context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

  override fun load(): PersistedMockupState {
    val isBlocked = prefs.getBoolean(KEY_IS_BLOCKED, false)
    val storedBlockedAt =
      if (prefs.contains(KEY_BLOCKED_AT_MILLIS)) {
        prefs.getLong(KEY_BLOCKED_AT_MILLIS, NO_TIMESTAMP).takeIf { it > 0L }
      } else {
        null
      }
    val validBlocked = isBlocked && storedBlockedAt != null
    val blockedTimestampText =
      if (validBlocked) {
        prefs.getString(KEY_BLOCKED_TIMESTAMP_TEXT, "").orEmpty()
      } else {
        ""
      }

    return PersistedMockupState(
      isBlocked = validBlocked,
      blockedAtMillis = if (validBlocked) storedBlockedAt else null,
      blockedTimestampText = blockedTimestampText,
      voiceAlertEnabled = prefs.getBoolean(KEY_VOICE_ALERT_ENABLED, true),
      vibrationAlertEnabled = prefs.getBoolean(KEY_VIBRATION_ALERT_ENABLED, true),
      installDateTime =
        prefs.getString(KEY_INSTALL_DATETIME, MockupUiState.DEFAULT_INSTALL_DATETIME)
          .orEmpty()
          .ifEmpty { MockupUiState.DEFAULT_INSTALL_DATETIME },
      manufacturer =
        prefs.getString(KEY_MANUFACTURER, MockupUiState.DEFAULT_MANUFACTURER)
          .orEmpty()
          .ifEmpty { MockupUiState.DEFAULT_MANUFACTURER },
      modelName =
        prefs.getString(KEY_MODEL_NAME, MockupUiState.DEFAULT_MODEL_NAME)
          .orEmpty()
          .ifEmpty { MockupUiState.DEFAULT_MODEL_NAME },
      osVersion =
        prefs.getString(KEY_OS_VERSION, MockupUiState.DEFAULT_OS_VERSION)
          .orEmpty()
          .ifEmpty { MockupUiState.DEFAULT_OS_VERSION },
    )
  }

  override fun save(state: PersistedMockupState) {
    prefs.edit().apply {
      putBoolean(KEY_IS_BLOCKED, state.isBlocked)
      if (state.isBlocked && state.blockedAtMillis != null) {
        putLong(KEY_BLOCKED_AT_MILLIS, state.blockedAtMillis)
        putString(KEY_BLOCKED_TIMESTAMP_TEXT, state.blockedTimestampText)
      } else {
        remove(KEY_BLOCKED_AT_MILLIS)
        remove(KEY_BLOCKED_TIMESTAMP_TEXT)
      }
      putBoolean(KEY_VOICE_ALERT_ENABLED, state.voiceAlertEnabled)
      putBoolean(KEY_VIBRATION_ALERT_ENABLED, state.vibrationAlertEnabled)
      putString(KEY_INSTALL_DATETIME, state.installDateTime)
      putString(KEY_MANUFACTURER, state.manufacturer)
      putString(KEY_MODEL_NAME, state.modelName)
      putString(KEY_OS_VERSION, state.osVersion)
      // Commit immediately so state survives abrupt background process kill or force swipe-away.
      commit()
    }
  }

  companion object {
    private const val PREFS_NAME = "camera_guard_mockup_prefs"
    private const val KEY_IS_BLOCKED = "is_blocked"
    private const val KEY_BLOCKED_AT_MILLIS = "blocked_at_millis"
    private const val KEY_BLOCKED_TIMESTAMP_TEXT = "blocked_timestamp_text"
    private const val KEY_VOICE_ALERT_ENABLED = "voice_alert_enabled"
    private const val KEY_VIBRATION_ALERT_ENABLED = "vibration_alert_enabled"
    private const val KEY_INSTALL_DATETIME = "install_date_time"
    private const val KEY_MANUFACTURER = "manufacturer"
    private const val KEY_MODEL_NAME = "model_name"
    private const val KEY_OS_VERSION = "os_version"
    private const val NO_TIMESTAMP = -1L
  }
}
