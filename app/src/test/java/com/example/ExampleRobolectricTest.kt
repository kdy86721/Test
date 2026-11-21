package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.state.MockupViewModel
import com.example.ui.state.SharedPreferencesMockupStateStore
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("국방모바일보안(병사)", appName)
  }

  @Test
  fun `blocked state and elapsed time survive process death`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = SharedPreferencesMockupStateStore(context)
    var fakeNowMs = 1_763_749_140_000L // Fixed wall-clock timestamp

    val firstVm = MockupViewModel(store = store, wallClockMs = { fakeNowMs })
    firstVm.setBlocked(true)
    val savedTimestampText = firstVm.uiState.value.blockedTimestampText

    assertTrue(firstVm.uiState.value.isBlocked)
    assertEquals(fakeNowMs, firstVm.uiState.value.blockedAtMillis)
    assertEquals(0L, firstVm.uiState.value.elapsedSeconds)

    // Simulate process killed in background and reopened 1 day, 2 hours, 3 minutes, 4 seconds later
    fakeNowMs += ((1L * 86_400L) + (2L * 3_600L) + (3L * 60L) + 4L) * 1_000L
    val restoredVm = MockupViewModel(store = store, wallClockMs = { fakeNowMs })

    assertTrue(restoredVm.uiState.value.isBlocked)
    assertEquals(savedTimestampText, restoredVm.uiState.value.blockedTimestampText)
    assertEquals(1L, restoredVm.uiState.value.elapsedDays)
    assertEquals("02 : 03 : 04", restoredVm.uiState.value.elapsedTimeFormatted)

    // Unblocking resets and persists across subsequent cold start
    restoredVm.setBlocked(false)
    val afterUnblockVm = MockupViewModel(store = store, wallClockMs = { fakeNowMs + 60_000L })

    assertFalse(afterUnblockVm.uiState.value.isBlocked)
    assertNull(afterUnblockVm.uiState.value.blockedAtMillis)
    assertEquals("", afterUnblockVm.uiState.value.blockedTimestampText)
    assertEquals(0L, afterUnblockVm.uiState.value.elapsedSeconds)
  }

  @Test
  fun `edited display info and alert toggles persist across restart`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val store = SharedPreferencesMockupStateStore(context)

    val firstVm = MockupViewModel(store = store)
    firstVm.saveEditableDisplayInfo(
      installDateTime = "26.07.01 10:30",
      manufacturer = "Samsung",
      modelName = "SM-S928N",
      osVersion = "16",
    )
    firstVm.toggleVoiceAlert()
    firstVm.toggleVibrationAlert()

    val restoredVm = MockupViewModel(store = store)
    assertEquals("26.07.01 10:30", restoredVm.uiState.value.installDateTime)
    assertEquals("Samsung", restoredVm.uiState.value.manufacturer)
    assertEquals("SM-S928N", restoredVm.uiState.value.modelName)
    assertEquals("16", restoredVm.uiState.value.osVersion)
    assertFalse(restoredVm.uiState.value.voiceAlertEnabled)
    assertFalse(restoredVm.uiState.value.vibrationAlertEnabled)
  }
}
