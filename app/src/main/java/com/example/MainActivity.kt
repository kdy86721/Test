package com.example

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.HiddenInfoEditDialog
import com.example.ui.screens.MainScreen
import com.example.ui.screens.MenuSettingsScreen
import com.example.ui.state.AppScreen
import com.example.ui.state.MockupViewModel
import com.example.ui.theme.CameraGuardAppTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge(
      statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
      navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
    )
    setContent {
      CameraGuardAppTheme {
        CameraGuardMockupApp()
      }
    }
  }
}

@Composable
fun CameraGuardMockupApp(
  viewModel: MockupViewModel = run {
    val context = LocalContext.current.applicationContext
    viewModel(factory = remember(context) { MockupViewModel.factory(context) })
  },
) {
  val uiState by viewModel.uiState.collectAsStateWithLifecycle()

  LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
    viewModel.syncElapsedTime()
  }

  AnimatedContent(
    targetState = uiState.currentScreen,
    modifier = Modifier.fillMaxSize(),
    transitionSpec = {
      if (targetState == AppScreen.Menu) {
        (slideInHorizontally(animationSpec = tween(240)) { -it / 4 } + fadeIn(tween(220)))
          .togetherWith(fadeOut(tween(180)))
      } else {
        fadeIn(tween(200))
          .togetherWith(
            slideOutHorizontally(animationSpec = tween(220)) { -it / 4 } + fadeOut(tween(180)),
          )
      }
    },
    label = "screenTransition",
  ) { screen ->
    when (screen) {
      AppScreen.Main -> {
        MainScreen(
          isBlocked = uiState.isBlocked,
          installDateTimeText = uiState.installDateTime,
          blockedTimestampText = uiState.blockedTimestampText,
          elapsedDays = uiState.elapsedDays,
          elapsedTimeText = uiState.elapsedTimeFormatted,
          onOpenMenu = viewModel::openMenu,
          onDeleteButtonClick = viewModel::onDeleteButtonTapped,
          onSetBlocked = viewModel::setBlocked,
        )
      }
      AppScreen.Menu -> {
        MenuSettingsScreen(
          isBlocked = uiState.isBlocked,
          manufacturer = uiState.manufacturer,
          modelName = uiState.modelName,
          osVersion = uiState.osVersion,
          voiceAlertEnabled = uiState.voiceAlertEnabled,
          vibrationAlertEnabled = uiState.vibrationAlertEnabled,
          onToggleVoiceAlert = viewModel::toggleVoiceAlert,
          onToggleVibrationAlert = viewModel::toggleVibrationAlert,
          onClose = viewModel::closeMenu,
        )
      }
    }
  }

  if (uiState.isEditDialogVisible) {
    HiddenInfoEditDialog(
      initialInstallDateTime = uiState.installDateTime,
      initialManufacturer = uiState.manufacturer,
      initialModelName = uiState.modelName,
      initialOsVersion = uiState.osVersion,
      onDismiss = viewModel::dismissEditDialog,
      onSave = viewModel::saveEditableDisplayInfo,
    )
  }
}
