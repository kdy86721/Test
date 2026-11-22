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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.lerp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CameraGuardTheme

/**
 * Top curved dual-wave header supporting both visual states and adaptive screen-height scaling:
 * - Default (isBlocked = false): vivid crimson header, "메뉴"/"삭제" outline buttons, install datetime + support info
 * - Blocked active (isBlocked = true): warm amber/gold header, "메뉴" outline button + white ringing bell icon,
 *   install datetime + "차단일시 : YY.MM.DD HH:mm" + support info
 *
 * [heightFraction] ranges from 0f (very compact/short screens) to 1f (standard/tall phones) so
 * emblem size, typography, vertical gaps, and wave band stay crisp and never crush lower content.
 */
@Composable
fun MainHeaderWave(
  isBlocked: Boolean,
  installDateTimeText: String,
  blockedTimestampText: String,
  onMenuClick: () -> Unit,
  onRightActionClick: () -> Unit,
  modifier: Modifier = Modifier,
  heightFraction: Float = 1f,
) {
  val colors = CameraGuardTheme.colors
  val colorAnim = tween<Color>(durationMillis = 240)
  val f = heightFraction.coerceIn(0f, 1f)

  // Responsive dimensions smoothly interpolated between compact and full-height layouts
  val topPad = lerp(6.dp, 12.dp, f)
  val horizontalPad = lerp(14.dp, 18.dp, f)
  val emblemWidth = lerp(68.dp, 96.dp, f)
  val emblemHeight = lerp(48.dp, 68.dp, f)
  val gapAfterEmblem = lerp(10.dp, 24.dp, f)
  val gapBetweenTimestamps = lerp(5.dp, 12.dp, f)
  val gapBeforeSupport = lerp(10.dp, 24.dp, f)
  val gapSupportTitlePhone = lerp(2.dp, 5.dp, f)
  val gapSupportPhoneHours = lerp(1.dp, 3.dp, f)
  val waveBandDp = lerp(28.dp, 46.dp, f)
  val waveTextClearance = lerp(14.dp, 26.dp, f)
  val totalBottomInset = waveBandDp + waveTextClearance

  val textScale = 0.78f + 0.22f * f
  val timestampFontSize = (21.5f * textScale).sp
  val timestampLineHeight = (27f * textScale).sp
  val supportTitleSize = (16f * textScale).sp
  val supportPhoneSize = (17.5f * textScale).sp
  val supportHoursSize = (13.8f * textScale).sp
  val outlineBtnWidth = lerp(54.dp, 62.dp, f)
  val outlineBtnHeight = lerp(32.dp, 36.dp, f)
  val bellIconSize = lerp(25.dp, 30.dp, f)

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
      // Responsive wave band at bottom edge so S-curve amplitude never crowds text
      val waveBand = waveBandDp.toPx().coerceAtMost(height * 0.24f)
      val baseY = height - waveBand

      // Layer 1 (rear): Translucent smoky overlapping wave arching slightly higher on the left-to-center
      val rearShadowWave =
        Path().apply {
          moveTo(0f, baseY + waveBand * 0.28f)
          cubicTo(
            width * 0.22f,
            baseY + waveBand * 0.56f,
            width * 0.44f,
            baseY + waveBand * 0.02f,
            width * 0.66f,
            baseY + waveBand * 0.14f,
          )
          cubicTo(
            width * 0.82f,
            baseY + waveBand * 0.24f,
            width * 0.92f,
            baseY + waveBand * 0.58f,
            width,
            baseY + waveBand * 0.72f,
          )
          lineTo(width, height)
          cubicTo(
            width * 0.74f,
            baseY + waveBand * 0.56f,
            width * 0.44f,
            baseY + waveBand * 0.94f,
            width * 0.18f,
            baseY + waveBand * 0.74f,
          )
          cubicTo(
            width * 0.08f,
            baseY + waveBand * 0.66f,
            width * 0.03f,
            baseY + waveBand * 0.78f,
            0f,
            baseY + waveBand * 0.86f,
          )
          close()
        }
      drawPath(path = rearShadowWave, color = backWaveColor)

      // Layer 2 (middle): Secondary offset translucent shadow wave gently sweeping below the main S-curve
      val midShadowWave =
        Path().apply {
          moveTo(0f, baseY + waveBand * 0.42f)
          cubicTo(
            width * 0.18f,
            baseY + waveBand * 0.20f,
            width * 0.42f,
            baseY + waveBand * 0.82f,
            width * 0.70f,
            baseY + waveBand * 0.42f,
          )
          cubicTo(
            width * 0.84f,
            baseY + waveBand * 0.24f,
            width * 0.93f,
            baseY + waveBand * 0.52f,
            width,
            baseY + waveBand * 0.64f,
          )
          lineTo(width, baseY + waveBand * 0.94f)
          cubicTo(
            width * 0.76f,
            baseY + waveBand * 0.72f,
            width * 0.44f,
            baseY + waveBand * 0.98f,
            0f,
            baseY + waveBand * 0.84f,
          )
          close()
        }
      drawPath(path = midShadowWave, color = midWaveColor)

      // Layer 3 (front): Main solid gradient header with smooth S-curve bottom edge
      val headerWavePath =
        Path().apply {
          moveTo(0f, 0f)
          lineTo(width, 0f)
          lineTo(width, baseY + waveBand * 0.84f)
          cubicTo(
            width * 0.86f,
            baseY + waveBand * 0.46f,
            width * 0.72f,
            baseY + waveBand * 0.08f,
            width * 0.55f,
            baseY + waveBand * 0.10f,
          )
          cubicTo(
            width * 0.36f,
            baseY + waveBand * 0.12f,
            width * 0.18f,
            baseY + waveBand * 0.54f,
            0f,
            baseY + waveBand * 0.36f,
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
            top = topPad,
            start = horizontalPad,
            end = horizontalPad,
            bottom = totalBottomInset,
          ),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // Top bar: 메뉴 (left), 국방부 엠블럼 (center), 삭제 or Ringing Bell Icon (right)
      Box(modifier = Modifier.fillMaxWidth()) {
        HeaderOutlineButton(
          label = stringResource(R.string.action_menu),
          onClick = onMenuClick,
          testTag = "menu_button",
          buttonWidth = outlineBtnWidth,
          buttonHeight = outlineBtnHeight,
          textScale = textScale,
          modifier = Modifier.align(Alignment.CenterStart),
        )

        DefenseWingAnchorEmblem(
          modifier =
            Modifier.align(Alignment.Center)
              .size(width = emblemWidth, height = emblemHeight),
        )

        if (isBlocked) {
          HeaderBellIconButton(
            onClick = onRightActionClick,
            testTag = "blocked_header_icon_button",
            iconSize = bellIconSize,
            modifier = Modifier.align(Alignment.CenterEnd),
          )
        } else {
          HeaderOutlineButton(
            label = stringResource(R.string.action_delete),
            onClick = onRightActionClick,
            testTag = "delete_button",
            buttonWidth = outlineBtnWidth,
            buttonHeight = outlineBtnHeight,
            textScale = textScale,
            modifier = Modifier.align(Alignment.CenterEnd),
          )
        }
      }

      Spacer(modifier = Modifier.height(gapAfterEmblem))

      // Install date-time (large bold white text, synced across both unblocked and blocked states)
      Text(
        text = stringResource(R.string.install_datetime_format, installDateTimeText),
        style =
          MaterialTheme.typography.headlineSmall.copy(
            fontSize = timestampFontSize,
            lineHeight = timestampLineHeight,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.testTag("install_datetime_text"),
      )

      // Recorded blocked timestamp shown when blocked active state is enabled
      AnimatedVisibility(
        visible = isBlocked,
        enter = fadeIn(tween(200)) + expandVertically(tween(200)),
        exit = fadeOut(tween(150)) + shrinkVertically(tween(180)),
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          Spacer(modifier = Modifier.height(gapBetweenTimestamps))
          Text(
            text = stringResource(R.string.blocked_datetime_format, blockedTimestampText),
            style =
              MaterialTheme.typography.headlineSmall.copy(
                fontSize = timestampFontSize,
                lineHeight = timestampLineHeight,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-0.4).sp,
              ),
            color = colors.menuTextPrimary,
            textAlign = TextAlign.Center,
            maxLines = 1,
            modifier = Modifier.testTag("blocked_datetime_text"),
          )
        }
      }

      Spacer(modifier = Modifier.height(gapBeforeSupport))

      // Customer support center heading
      Text(
        text = stringResource(R.string.support_center_title),
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = supportTitleSize,
            lineHeight = (20f * textScale).sp,
            fontWeight = FontWeight.Bold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.testTag("main_support_center_title"),
      )

      Spacer(modifier = Modifier.height(gapSupportTitlePhone))

      // Bold highlighted phone number (static display only, no dial intent)
      Text(
        text = stringResource(R.string.support_center_phone),
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = supportPhoneSize,
            lineHeight = (21f * textScale).sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.1).sp,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.testTag("main_support_center_phone"),
      )

      Spacer(modifier = Modifier.height(gapSupportPhoneHours))

      // Support center operating hours
      Text(
        text = stringResource(R.string.support_center_hours),
        style =
          MaterialTheme.typography.bodyMedium.copy(
            fontSize = supportHoursSize,
            lineHeight = (17f * textScale).sp,
            fontWeight = FontWeight.SemiBold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
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
  buttonWidth: androidx.compose.ui.unit.Dp,
  buttonHeight: androidx.compose.ui.unit.Dp,
  textScale: Float,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }

  // 48dp minimum touch area wrapping the thin-outlined white rectangle button
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
        .padding(horizontal = 2.dp, vertical = 4.dp),
    contentAlignment = Alignment.Center,
  ) {
    Surface(
      shape = RoundedCornerShape(4.dp),
      color = Color.Transparent,
      border = BorderStroke(width = 1.3.dp, color = colors.headerOutlineWhite),
      modifier = Modifier.width(buttonWidth).height(buttonHeight),
    ) {
      Box(contentAlignment = Alignment.Center) {
        Text(
          text = label,
          style =
            MaterialTheme.typography.labelLarge.copy(
              fontSize = (14f * textScale).sp,
              fontWeight = FontWeight.Bold,
            ),
          color = colors.menuTextPrimary,
          maxLines = 1,
        )
      }
    }
  }
}

/**
 * Top-right crisp white ringing notification bell icon shown in the blocked active header state,
 * matching the close-up reference photo (no border box, ringing arcs on both sides).
 */
@Composable
private fun HeaderBellIconButton(
  onClick: () -> Unit,
  testTag: String,
  iconSize: androidx.compose.ui.unit.Dp,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }
  val bellDesc = stringResource(R.string.cd_header_lock_action)

  Box(
    modifier =
      modifier
        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        .clip(RoundedCornerShape(8.dp))
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colors.menuTextPrimary),
          role = Role.Button,
          onClick = onClick,
        )
        .semantics { contentDescription = bellDesc }
        .testTag(testTag)
        .padding(6.dp),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.size(iconSize)) {
      val w = size.width
      val h = size.height
      val cx = w * 0.5f
      val bellColor = colors.menuTextPrimary
      val stroke =
        Stroke(width = (w * 0.078f).coerceAtLeast(1.8.dp.toPx()), cap = StrokeCap.Round, join = StrokeJoin.Round)

      // Top small bell loop knob
      drawLine(
        color = bellColor,
        start = Offset(cx, h * 0.13f),
        end = Offset(cx, h * 0.21f),
        strokeWidth = (w * 0.088f).coerceAtLeast(2.0.dp.toPx()),
        cap = StrokeCap.Round,
      )

      // Bell dome & flared rim
      val bellBody =
        Path().apply {
          moveTo(w * 0.24f, h * 0.72f)
          quadraticTo(w * 0.29f, h * 0.65f, w * 0.30f, h * 0.44f)
          cubicTo(w * 0.30f, h * 0.27f, w * 0.39f, h * 0.21f, cx, h * 0.21f)
          cubicTo(w * 0.61f, h * 0.21f, w * 0.70f, h * 0.27f, w * 0.70f, h * 0.44f)
          quadraticTo(w * 0.71f, h * 0.65f, w * 0.76f, h * 0.72f)
          close()
        }
      drawPath(path = bellBody, color = bellColor, style = stroke)

      // Bottom clapper semicircle
      drawArc(
        color = bellColor,
        startAngle = 0f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - w * 0.085f, h * 0.72f - w * 0.04f),
        size = Size(w * 0.17f, w * 0.14f),
        style = stroke,
      )

      // Left & right ringing vibration arcs
      val arcStroke = Stroke(width = (w * 0.068f).coerceAtLeast(1.6.dp.toPx()), cap = StrokeCap.Round)
      drawArc(
        color = bellColor,
        startAngle = 148f,
        sweepAngle = 64f,
        useCenter = false,
        topLeft = Offset(w * 0.08f, h * 0.29f),
        size = Size(w * 0.26f, h * 0.36f),
        style = arcStroke,
      )
      drawArc(
        color = bellColor,
        startAngle = -32f,
        sweepAngle = 64f,
        useCenter = false,
        topLeft = Offset(w * 0.66f, h * 0.29f),
        size = Size(w * 0.26f, h * 0.36f),
        style = arcStroke,
      )
    }
  }
}
