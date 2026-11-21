package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NoPhotography
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CameraGuardTheme

/**
 * Top curved wave header supporting both visual states:
 * - Default (isBlocked = false): vivid crimson header, "메뉴"/"삭제" buttons, install datetime + support info
 * - Blocked active (isBlocked = true): warm amber/gold header, "메뉴" + outlined line-icon button,
 *   install datetime + "차단일시 : 26.11.21 18:19" + support info
 */
@Composable
fun MainHeaderWave(
  isBlocked: Boolean,
  installDateTimeText: String,
  blockedTimestampText: String,
  onMenuClick: () -> Unit,
  onRightActionClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val colorAnim = tween<Color>(durationMillis = 240)

  val headerTopColor by
    animateColorAsState(
      targetValue = if (isBlocked) colors.headerAmberTop else colors.headerCrimsonTop,
      animationSpec = colorAnim,
      label = "headerTopColor",
    )
  val headerBottomColor by
    animateColorAsState(
      targetValue = if (isBlocked) colors.headerAmberBottom else colors.headerCrimson,
      animationSpec = colorAnim,
      label = "headerBottomColor",
    )
  val backWaveColor by
    animateColorAsState(
      targetValue = if (isBlocked) colors.blockedWaveShadow else colors.headerWaveShadow,
      animationSpec = colorAnim,
      label = "backWaveColor",
    )
  val midWaveColor by
    animateColorAsState(
      targetValue = if (isBlocked) colors.blockedWaveSecondary else colors.headerWaveSecondary,
      animationSpec = colorAnim,
      label = "midWaveColor",
    )

  Box(modifier = modifier.fillMaxWidth()) {
    Canvas(modifier = Modifier.matchParentSize()) {
      val width = size.width
      val height = size.height

      // Upper translucent smoky wave peeking gracefully above the transition
      val backWavePath =
        Path().apply {
          moveTo(0f, height * 0.89f)
          cubicTo(
            width * 0.24f,
            height * 0.96f,
            width * 0.44f,
            height * 0.78f,
            width * 0.66f,
            height * 0.81f,
          )
          cubicTo(
            width * 0.82f,
            height * 0.83f,
            width * 0.92f,
            height * 0.91f,
            width,
            height * 0.94f,
          )
          lineTo(width, height)
          cubicTo(
            width * 0.72f,
            height * 0.92f,
            width * 0.34f,
            height * 0.97f,
            0f,
            height * 0.95f,
          )
          close()
        }
      drawPath(path = backWavePath, color = backWaveColor)

      // Secondary deeper translucent wave ribbon right below the upper wave
      val midWavePath =
        Path().apply {
          moveTo(0f, height * 0.91f)
          cubicTo(
            width * 0.26f,
            height * 0.96f,
            width * 0.56f,
            height * 0.84f,
            width,
            height * 0.97f,
          )
          lineTo(width, height)
          cubicTo(
            width * 0.66f,
            height * 0.91f,
            width * 0.30f,
            height * 0.98f,
            0f,
            height * 0.96f,
          )
          close()
        }
      drawPath(path = midWavePath, color = midWaveColor)

      // Main curved header body (crimson when unblocked, warm amber/gold when blocked)
      val headerWavePath =
        Path().apply {
          moveTo(0f, 0f)
          lineTo(width, 0f)
          lineTo(width, height * 0.95f)
          cubicTo(
            width * 0.82f,
            height * 0.87f,
            width * 0.68f,
            height * 0.80f,
            width * 0.52f,
            height * 0.82f,
          )
          cubicTo(
            width * 0.32f,
            height * 0.84f,
            width * 0.16f,
            height * 0.95f,
            0f,
            height * 0.90f,
          )
          close()
        }

      drawPath(
        path = headerWavePath,
        brush =
          Brush.verticalGradient(
            colors = listOf(headerTopColor, headerBottomColor),
          ),
      )
    }

    Column(
      modifier =
        Modifier.fillMaxWidth()
          .padding(
            top = 10.dp,
            start = 14.dp,
            end = 14.dp,
            bottom = if (isBlocked) 44.dp else 52.dp,
          ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top bar: 메뉴 (left), Winged-Anchor-Star Emblem (center), 삭제 or Blocked Icon (right)
      Box(modifier = Modifier.fillMaxWidth()) {
        HeaderOutlineButton(
          label = stringResource(R.string.action_menu),
          onClick = onMenuClick,
          testTag = "menu_button",
          modifier = Modifier.align(Alignment.TopStart),
        )

        DefenseWingAnchorEmblem(
          modifier =
            Modifier.align(Alignment.TopCenter)
              .padding(top = 2.dp)
              .size(width = 84.dp, height = 60.dp),
        )

        if (isBlocked) {
          HeaderOutlineIconButton(
            onClick = onRightActionClick,
            testTag = "blocked_header_icon_button",
            modifier = Modifier.align(Alignment.TopEnd),
          )
        } else {
          HeaderOutlineButton(
            label = stringResource(R.string.action_delete),
            onClick = onRightActionClick,
            testTag = "delete_button",
            modifier = Modifier.align(Alignment.TopEnd),
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Install date-time (large bold white text, synced across both unblocked and blocked states)
      Text(
        text = stringResource(R.string.install_datetime_format, installDateTimeText),
        style =
          MaterialTheme.typography.headlineSmall.copy(
            fontSize = 20.5.sp,
            lineHeight = 26.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.3).sp,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("install_datetime_text"),
      )

      // Recorded blocked timestamp shown when blocked active state is enabled
      AnimatedVisibility(
        visible = isBlocked,
        enter = fadeIn(tween(200)) + expandVertically(tween(200)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(180)),
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = stringResource(R.string.blocked_datetime_format, blockedTimestampText),
            style =
              MaterialTheme.typography.headlineSmall.copy(
                fontSize = 20.5.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.3).sp,
              ),
            color = colors.menuTextPrimary,
            textAlign = TextAlign.Center,
            modifier = Modifier.testTag("blocked_datetime_text"),
          )
        }
      }

      Spacer(modifier = Modifier.height(if (isBlocked) 10.dp else 14.dp))

      // Customer support center heading
      Text(
        text = stringResource(R.string.support_center_title),
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = 15.5.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("main_support_center_title"),
      )

      Spacer(modifier = Modifier.height(3.dp))

      // Bold highlighted phone number (static display only, no dial intent)
      Text(
        text = stringResource(R.string.support_center_phone),
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = 17.sp,
            lineHeight = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.sp,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("main_support_center_phone"),
      )

      Spacer(modifier = Modifier.height(2.dp))

      // Support center operating hours
      Text(
        text = stringResource(R.string.support_center_hours),
        style =
          MaterialTheme.typography.bodyMedium.copy(
            fontSize = 13.5.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Medium,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("main_support_center_hours"),
      )
    }
  }
}

@Composable
private fun HeaderOutlineButton(
  label: String,
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }

  // 48dp minimum touch area wrapping the compact outlined rectangle button
  Box(
    modifier =
      modifier
        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        .clip(RoundedCornerShape(6.dp))
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colors.menuTextPrimary),
          role = Role.Button,
          onClick = onClick,
        )
        .testTag(testTag)
        .padding(horizontal = 2.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center,
  ) {
    Surface(
      shape = RoundedCornerShape(4.dp),
      color = Color.Transparent,
      border = BorderStroke(width = 1.1.dp, color = colors.headerOutlineWhite),
      modifier = Modifier.width(56.dp).height(33.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = label,
          style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
          color = colors.menuTextPrimary,
          maxLines = 1,
        )
      }
    }
  }
}

/**
 * Top-right outlined rounded-rectangle icon button shown in the blocked active header state.
 */
@Composable
private fun HeaderOutlineIconButton(
  onClick: () -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier =
      modifier
        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        .clip(RoundedCornerShape(6.dp))
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colors.menuTextPrimary),
          role = Role.Button,
          onClick = onClick,
        )
        .testTag(testTag)
        .padding(horizontal = 2.dp, vertical = 6.dp),
    contentAlignment = Alignment.Center,
  ) {
    Surface(
      shape = RoundedCornerShape(4.dp),
      color = Color.Transparent,
      border = BorderStroke(width = 1.1.dp, color = colors.headerOutlineWhite),
      modifier = Modifier.width(46.dp).height(33.dp),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Icon(
          imageVector = Icons.Outlined.NoPhotography,
          contentDescription = stringResource(R.string.cd_header_lock_action),
          tint = colors.menuTextPrimary,
          modifier = Modifier.size(19.dp),
        )
      }
    }
  }
}
