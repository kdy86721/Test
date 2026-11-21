package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.BlockedCameraRingIcon
import com.example.ui.components.CameraRingIcon
import com.example.ui.components.DefenseHelpCallBanner
import com.example.ui.components.MainHeaderWave
import com.example.ui.theme.CameraGuardAppTheme
import com.example.ui.theme.CameraGuardTheme

/**
 * Screen 1: Main Screen (pure UI mockup supporting isBlocked = false / true visual states).
 * - Default (isBlocked = false): crimson curved header, simple translucent camera ring, brown "카메라 차단" button
 * - Blocked (isBlocked = true): amber/gold curved header with "차단일시", radial tick-mark prohibition camera
 *   emblem with live elapsed time, and slate-grey "기능해제(비콘)" button returning to default state
 */
@Composable
fun MainScreen(
  isBlocked: Boolean,
  installDateTimeText: String,
  blockedTimestampText: String,
  elapsedDays: Long,
  elapsedTimeText: String,
  onOpenMenu: () -> Unit,
  onDeleteButtonClick: () -> Unit,
  onSetBlocked: (Boolean) -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val colorAnim = tween<Color>(durationMillis = 240)

  val bgPrimary by
    animateColorAsState(
      targetValue = if (isBlocked) colors.blockedMainBackground else colors.mainBackground,
      animationSpec = colorAnim,
      label = "mainBgPrimary",
    )
  val bgSecondary by
    animateColorAsState(
      targetValue = if (isBlocked) colors.blockedMainBackgroundLower else colors.mainBackgroundLower,
      animationSpec = colorAnim,
      label = "mainBgSecondary",
    )

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(
          brush =
            Brush.verticalGradient(
              colors = listOf(bgPrimary, bgSecondary, bgPrimary),
            ),
        )
        .statusBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier.fillMaxSize().widthIn(max = 560.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top curved wave header (crimson when default, warm amber/gold with 차단일시 when blocked)
      MainHeaderWave(
        isBlocked = isBlocked,
        installDateTimeText = installDateTimeText,
        blockedTimestampText = blockedTimestampText,
        onMenuClick = onOpenMenu,
        onRightActionClick = {
          if (!isBlocked) {
            onDeleteButtonClick()
          }
        },
      )

      // Middle content area
      Column(
        modifier = Modifier.weight(1f).fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        // Centered "국방헬프콜 1303" emblem & logo positioned 10dp below the wave boundary
        DefenseHelpCallBanner(modifier = Modifier.padding(top = 10.dp))

        Spacer(modifier = Modifier.weight(0.5f))

        // Central camera visual switching smoothly between default ring and blocked radial tick ring
        AnimatedContent(
          targetState = isBlocked,
          transitionSpec = {
            (fadeIn(tween(220)) + scaleIn(initialScale = 0.94f, animationSpec = tween(220)))
              .togetherWith(
                fadeOut(tween(160)) + scaleOut(targetScale = 0.96f, animationSpec = tween(160)),
              )
          },
          label = "cameraCenterGraphicTransition",
        ) { blocked ->
          if (blocked) {
            BlockedCameraRingIcon(
              elapsedDays = elapsedDays,
              elapsedTimeText = elapsedTimeText,
            )
          } else {
            CameraRingIcon()
          }
        }

        Spacer(modifier = Modifier.weight(0.95f))
      }

      // Bottom primary pill action button ("카메라 차단" <-> "기능해제(비콘)")
      MainActionMockButton(
        isBlocked = isBlocked,
        onClick = { onSetBlocked(!isBlocked) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Dark bottom footer displaying static app version
      Box(
        modifier =
          Modifier.fillMaxWidth()
            .background(colors.mainFooterBackground)
            .navigationBarsPadding()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center,
      ) {
        Text(
          text = stringResource(R.string.app_version),
          style = MaterialTheme.typography.labelSmall,
          color = colors.versionTextMuted,
          textAlign = TextAlign.Center,
          modifier = Modifier.testTag("version_text"),
        )
      }
    }
  }
}

@Composable
private fun MainActionMockButton(
  isBlocked: Boolean,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }
  val buttonColor by
    animateColorAsState(
      targetValue = if (isBlocked) colors.unblockButtonSlate else colors.cameraBlockButtonFill,
      animationSpec = tween(durationMillis = 220),
      label = "mainActionButtonColor",
    )

  Surface(
    shape = RoundedCornerShape(percent = 50),
    color = buttonColor,
    modifier =
      modifier
        .height(48.dp)
        .clip(RoundedCornerShape(percent = 50))
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colors.menuTextPrimary),
          role = Role.Button,
          onClick = onClick,
        )
        .testTag(if (isBlocked) "camera_unblock_button" else "camera_block_button"),
  ) {
    Box(contentAlignment = Alignment.Center) {
      Text(
        text =
          stringResource(
            if (isBlocked) R.string.action_unblock_camera else R.string.action_block_camera,
          ),
        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
      )
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenDefaultPreview() {
  CameraGuardAppTheme {
    MainScreen(
      isBlocked = false,
      installDateTimeText = "26.06.16 09:01",
      blockedTimestampText = "",
      elapsedDays = 0L,
      elapsedTimeText = "00 : 00 : 00",
      onOpenMenu = {},
      onDeleteButtonClick = {},
      onSetBlocked = {},
    )
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MainScreenBlockedPreview() {
  CameraGuardAppTheme {
    MainScreen(
      isBlocked = true,
      installDateTimeText = "26.06.16 09:01",
      blockedTimestampText = "26.11.21 18:19",
      elapsedDays = 0L,
      elapsedTimeText = "00 : 00 : 15",
      onOpenMenu = {},
      onDeleteButtonClick = {},
      onSetBlocked = {},
    )
  }
}
