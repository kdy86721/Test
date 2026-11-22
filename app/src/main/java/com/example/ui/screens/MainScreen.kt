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
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.unit.lerp
import com.example.R
import com.example.ui.components.BlockedCameraRingIcon
import com.example.ui.components.CameraRingIcon
import com.example.ui.components.DefenseHelpCallBanner
import com.example.ui.components.MainHeaderWave
import com.example.ui.theme.CameraGuardAppTheme
import com.example.ui.theme.CameraGuardTheme

/**
 * Screen 1: Main Screen (pure UI mockup supporting isBlocked = false / true visual states).
 * Adaptively scales header proportions, "국방헬프콜 1303" logo, and central camera emblem
 * to the available window height via BoxWithConstraints so compact/split-screen displays never
 * distort the blocked camera icon or clip elapsed-time text.
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
    BoxWithConstraints(
      modifier = Modifier.fillMaxSize().widthIn(max = 560.dp),
    ) {
      // Smoothly adapt header & logo proportions across short (<= 480dp) to tall (>= 760dp) screens
      val heightFraction = ((maxHeight - 460.dp) / (760.dp - 460.dp)).coerceIn(0f, 1f)
      val helpCallScale = 0.72f + 0.28f * heightFraction
      val helpCallTopPadding = lerp(4.dp, 10.dp, heightFraction)
      val buttonBottomSpacer = lerp(8.dp, 14.dp, heightFraction)
      val footerVerticalPadding = lerp(6.dp, 10.dp, heightFraction)

      Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        // Top curved wave header (adaptively scaled for compact screens)
        MainHeaderWave(
          isBlocked = isBlocked,
          installDateTimeText = installDateTimeText,
          blockedTimestampText = blockedTimestampText,
          heightFraction = heightFraction,
          onMenuClick = onOpenMenu,
          onRightActionClick = {
            if (!isBlocked) {
              onDeleteButtonClick()
            }
          },
        )

        // Middle content area: "국방헬프콜 1303" banner + container-fitted central camera graphic
        Column(
          modifier = Modifier.weight(1f).fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally,
        ) {
          DefenseHelpCallBanner(
            scale = helpCallScale,
            modifier = Modifier.padding(top = helpCallTopPadding),
          )

          // Dynamically size the central camera ring to the remaining middle height so it never squishes
          BoxWithConstraints(
            modifier = Modifier.weight(1f).fillMaxWidth().padding(vertical = 4.dp),
            contentAlignment = Alignment.Center,
          ) {
            val targetMaxRing = if (isBlocked) 242.dp else 216.dp
            val fittedRingSize =
              minOf(targetMaxRing, maxHeight - 4.dp, maxWidth - 28.dp).coerceAtLeast(112.dp)

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
                  ringSize = fittedRingSize,
                )
              } else {
                CameraRingIcon(ringSize = fittedRingSize)
              }
            }
          }
        }

        // Bottom primary pill action button ("카메라 차단" <-> "기능해제(비콘)")
        MainActionMockButton(
          isBlocked = isBlocked,
          onClick = { onSetBlocked(!isBlocked) },
          modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp),
        )

        Spacer(modifier = Modifier.height(buttonBottomSpacer))

        // Dark bottom footer displaying static app version
        Box(
          modifier =
            Modifier.fillMaxWidth()
              .background(colors.mainFooterBackground)
              .navigationBarsPadding()
              .padding(vertical = footerVerticalPadding),
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

@Preview(showBackground = true, widthDp = 390, heightDp = 560)
@Composable
private fun MainScreenCompactBlockedPreview() {
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
