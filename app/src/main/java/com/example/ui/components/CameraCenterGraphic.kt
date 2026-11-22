package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.CameraGuardTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.ui.unit.Dp

/**
 * Centered "국방헬프콜 1303" brand logo strip positioned right below the header wave transition:
 * - Left: colorful winged embracing dual-figure graphic (sky-blue→deep-blue left figure with
 *   3-feather wing + yellow→lime-green right figure with vivid coral-red embrace overlap)
 * - Right top: bold blue "국방헬프콜" with curved upward-tilted blue telephone handset accent
 * - Right bottom: thick square/techno geometric "1303" with vertical gold (#FFB300) → orange-red (#F4511E) gradient
 */
@Composable
fun DefenseHelpCallBanner(
  modifier: Modifier = Modifier,
  scale: Float = 1f,
) {
  val colors = CameraGuardTheme.colors
  val logoDesc = stringResource(R.string.cd_help_call_logo)
  val s = scale.coerceIn(0.68f, 1.05f)

  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .semantics(mergeDescendants = true) { contentDescription = logoDesc }
        .testTag("defense_help_call_banner"),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // Colorful winged embracing dual-figure symbol
    HelpCallFiguresEmblem(modifier = Modifier.size(width = (74f * s).dp, height = (62f * s).dp))

    Spacer(modifier = Modifier.width((8f * s).dp))

    // Right stacked text block: "국방헬프콜" + curved blue handset on top, sharp block "1303" below
    Column(horizontalAlignment = Alignment.Start) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Text(
          text = stringResource(R.string.help_call_title),
          style =
            MaterialTheme.typography.titleMedium.copy(
              fontSize = (17f * s).sp,
              lineHeight = (20f * s).sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = (-0.5f * s).sp,
            ),
          color = colors.helpCallBlue,
          maxLines = 1,
        )

        Spacer(modifier = Modifier.width((2f * s).dp))

        // Upward-curving blue telephone handset / smile accent right beside "국방헬프콜"
        Canvas(modifier = Modifier.size(width = (16f * s).dp, height = (20f * s).dp)) {
          val w = size.width
          val h = size.height
          val handsetBrush =
            Brush.verticalGradient(
              colors = listOf(colors.helpCallBlueDeep, colors.helpCallBlue),
            )
          val handsetBody =
            Path().apply {
              moveTo(w * 0.08f, h * 0.86f)
              cubicTo(w * 0.46f, h * 0.84f, w * 0.72f, h * 0.56f, w * 0.76f, h * 0.14f)
            }
          drawPath(
            path = handsetBody,
            brush = handsetBrush,
            style = Stroke(width = w * 0.19f, cap = StrokeCap.Round),
          )
          drawRoundRect(
            color = colors.helpCallBlueDeep,
            topLeft = Offset(w * 0.46f, h * 0.08f),
            size = Size(w * 0.36f, h * 0.24f),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
          )
          drawRoundRect(
            color = colors.helpCallBlue,
            topLeft = Offset(w * 0.02f, h * 0.72f),
            size = Size(w * 0.34f, h * 0.22f),
            cornerRadius = CornerRadius(w * 0.14f, w * 0.14f),
          )
        }
      }

      Spacer(modifier = Modifier.height((3f * s).dp))

      // Bold square/techno vertical-gradient "1303" logo digits
      OrangeBlock1303Logo(modifier = Modifier.width((114f * s).dp).height((31f * s).dp))
    }
  }
}

@Composable
private fun HelpCallFiguresEmblem(modifier: Modifier = Modifier) {
  val colors = CameraGuardTheme.colors

  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    val blueHeadGrad =
      Brush.linearGradient(
        colors = listOf(colors.symbolBlue, colors.symbolBlueDeep),
        start = Offset(w * 0.28f, h * 0.14f),
        end = Offset(w * 0.48f, h * 0.36f),
      )
    val blueBodyGrad =
      Brush.verticalGradient(
        colors = listOf(colors.symbolBlue, colors.symbolBlueDeep),
        startY = h * 0.34f,
        endY = h * 0.96f,
      )
    val yellowGreenHeadGrad =
      Brush.verticalGradient(
        colors = listOf(colors.symbolYellow, colors.symbolGreen),
        startY = h * 0.06f,
        endY = h * 0.28f,
      )
    val yellowGreenBodyGrad =
      Brush.verticalGradient(
        colors = listOf(colors.symbolYellow, colors.symbolGreen),
        startY = h * 0.24f,
        endY = h * 0.86f,
      )
    val embraceOverlapGrad =
      Brush.horizontalGradient(
        colors = listOf(colors.helpCallOrangeTop, colors.helpCallOrangeBottom, colors.symbolCoral),
        startX = w * 0.58f,
        endX = w * 0.96f,
      )

    // 1. Left blue figure's 3-tiered sweeping wing extending upward-left
    val wingPath =
      Path().apply {
        moveTo(w * 0.29f, h * 0.40f)
        cubicTo(w * 0.18f, h * 0.36f, w * 0.08f, h * 0.33f, w * 0.02f, h * 0.31f)
        cubicTo(w * 0.06f, h * 0.42f, w * 0.14f, h * 0.47f, w * 0.22f, h * 0.49f)
        cubicTo(w * 0.14f, h * 0.49f, w * 0.08f, h * 0.49f, w * 0.06f, h * 0.50f)
        cubicTo(w * 0.10f, h * 0.57f, w * 0.18f, h * 0.59f, w * 0.25f, h * 0.59f)
        cubicTo(w * 0.18f, h * 0.62f, w * 0.13f, h * 0.64f, w * 0.12f, h * 0.65f)
        cubicTo(w * 0.19f, h * 0.69f, w * 0.26f, h * 0.68f, w * 0.32f, h * 0.65f)
        close()
      }
    drawPath(path = wingPath, brush = blueBodyGrad)

    // 2. Right yellow-to-lime figure head (positioned slightly higher than left figure)
    drawCircle(
      brush = yellowGreenHeadGrad,
      radius = w * 0.070f,
      center = Offset(w * 0.69f, h * 0.145f),
    )

    // 3. Right yellow-to-lime sweeping curved body (curving smoothly down-right)
    val rightBody =
      Path().apply {
        moveTo(w * 0.63f, h * 0.28f)
        cubicTo(w * 0.72f, h * 0.23f, w * 0.82f, h * 0.29f, w * 0.83f, h * 0.44f)
        cubicTo(w * 0.84f, h * 0.60f, w * 0.77f, h * 0.77f, w * 0.65f, h * 0.85f)
        cubicTo(w * 0.62f, h * 0.86f, w * 0.61f, h * 0.83f, w * 0.63f, h * 0.78f)
        cubicTo(w * 0.66f, h * 0.67f, w * 0.64f, h * 0.54f, w * 0.61f, h * 0.42f)
        cubicTo(w * 0.59f, h * 0.35f, w * 0.60f, h * 0.30f, w * 0.63f, h * 0.28f)
        close()
      }
    drawPath(path = rightBody, brush = yellowGreenBodyGrad)

    // 4. Warm gold -> orange -> coral-red embrace overlap lobe where both arms meet
    val embraceLobe =
      Path().apply {
        moveTo(w * 0.59f, h * 0.40f)
        cubicTo(w * 0.72f, h * 0.35f, w * 0.86f, h * 0.34f, w * 0.93f, h * 0.40f)
        cubicTo(w * 0.98f, h * 0.45f, w * 0.95f, h * 0.54f, w * 0.86f, h * 0.57f)
        cubicTo(w * 0.76f, h * 0.59f, w * 0.66f, h * 0.57f, w * 0.59f, h * 0.55f)
        close()
      }
    drawPath(path = embraceLobe, brush = embraceOverlapGrad)

    // 5. Left sky-blue figure head
    drawCircle(
      brush = blueHeadGrad,
      radius = w * 0.080f,
      center = Offset(w * 0.375f, h * 0.24f),
    )

    // 6. Left blue streamlined torso sweeping down + embracing arm reaching across to right figure
    val blueBody =
      Path().apply {
        moveTo(w * 0.27f, h * 0.40f)
        cubicTo(w * 0.33f, h * 0.35f, w * 0.42f, h * 0.38f, w * 0.50f, h * 0.40f)
        cubicTo(w * 0.56f, h * 0.41f, w * 0.61f, h * 0.40f, w * 0.64f, h * 0.40f)
        lineTo(w * 0.64f, h * 0.55f)
        cubicTo(w * 0.57f, h * 0.56f, w * 0.50f, h * 0.55f, w * 0.455f, h * 0.56f)
        cubicTo(w * 0.475f, h * 0.72f, w * 0.47f, h * 0.89f, w * 0.44f, h * 0.96f)
        cubicTo(w * 0.36f, h * 0.93f, w * 0.31f, h * 0.79f, w * 0.29f, h * 0.64f)
        cubicTo(w * 0.26f, h * 0.52f, w * 0.25f, h * 0.44f, w * 0.27f, h * 0.40f)
        close()
      }
    drawPath(path = blueBody, brush = blueBodyGrad)
  }
}

/**
 * Custom sharp square/techno geometric "1303" logo digits with vertical
 * bright gold (#FFB300) to deep orange-red (#F4511E) gradient matching the close-up photo.
 */
@Composable
private fun OrangeBlock1303Logo(modifier: Modifier = Modifier) {
  val colors = CameraGuardTheme.colors

  Canvas(modifier = modifier.testTag("help_call_1303_logo")) {
    val w = size.width
    val h = size.height
    val orangeBrush =
      Brush.verticalGradient(
        colors = listOf(colors.helpCallOrangeTop, colors.helpCallOrangeBottom),
      )

    val stroke = h * 0.245f
    val corner = CornerRadius(h * 0.042f, h * 0.042f)
    val gap = w * 0.052f

    // Compact '1' width + 3 equal square-proportioned wide block digits ('3', '0', '3')
    val w1 = w * 0.145f
    val wDigit = (w - w1 - gap * 3f) / 3f

    // --- Digit '1' ---
    val x1 = 0f
    drawRoundRect(
      brush = orangeBrush,
      topLeft = Offset(x1, 0f),
      size = Size(w1, stroke),
      cornerRadius = corner,
    )
    drawRoundRect(
      brush = orangeBrush,
      topLeft = Offset(x1 + w1 - stroke * 0.95f, 0f),
      size = Size(stroke * 0.95f, h),
      cornerRadius = corner,
    )

    fun drawTechnoThree(left: Float) {
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left, 0f),
        size = Size(wDigit, stroke),
        cornerRadius = corner,
      )
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left + wDigit * 0.14f, (h - stroke) / 2f),
        size = Size(wDigit * 0.86f, stroke),
        cornerRadius = corner,
      )
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left, h - stroke),
        size = Size(wDigit, stroke),
        cornerRadius = corner,
      )
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left + wDigit - stroke * 0.95f, 0f),
        size = Size(stroke * 0.95f, h),
        cornerRadius = corner,
      )
    }

    // --- First Digit '3' ---
    val x2 = x1 + w1 + gap
    drawTechnoThree(x2)

    // --- Digit '0' ---
    val x3 = x2 + wDigit + gap
    val zeroPath =
      Path().apply {
        fillType = PathFillType.EvenOdd
        addRoundRect(
          RoundRect(
            rect = Rect(left = x3, top = 0f, right = x3 + wDigit, bottom = h),
            cornerRadius = CornerRadius(h * 0.065f, h * 0.065f),
          ),
        )
        addRoundRect(
          RoundRect(
            rect =
              Rect(
                left = x3 + stroke * 0.95f,
                top = stroke,
                right = x3 + wDigit - stroke * 0.95f,
                bottom = h - stroke,
              ),
            cornerRadius = CornerRadius(h * 0.026f, h * 0.026f),
          ),
        )
      }
    drawPath(path = zeroPath, brush = orangeBrush)

    // --- Second Digit '3' ---
    val x4 = x3 + wDigit + gap
    drawTechnoThree(x4)
  }
}

/**
 * Default (unblocked) central graphic: thick translucent smoky ring enclosing a white line camera.
 * Scales all strokes proportionally to [ringSize] so it never distorts on compact screens.
 */
@Composable
fun CameraRingIcon(
  modifier: Modifier = Modifier,
  ringSize: Dp = 216.dp,
) {
  val colors = CameraGuardTheme.colors
  val cameraDesc = stringResource(R.string.cd_camera_status_icon)

  Box(
    modifier =
      modifier
        .size(ringSize)
        .semantics { contentDescription = cameraDesc }
        .testTag("camera_ring_icon"),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val minDim = size.minDimension
      val ringStrokePx = minDim * 0.102f
      val ringRadius = (minDim - ringStrokePx) / 2f

      drawCircle(
        color = colors.cameraRingTranslucent,
        radius = ringRadius,
        center = center,
        style = Stroke(width = ringStrokePx),
      )

      drawWhiteLineCamera(
        camCenter = Offset(center.x, center.y + minDim * 0.01f),
        camW = minDim * 0.43f,
        strokeColor = colors.cameraStrokeWhite,
        strokeWidthPx = (minDim * 0.012f).coerceAtLeast(1.4.dp.toPx()),
      )
    }
  }
}

/**
 * Blocked active state central emblem:
 * - Outer 28-segment radial rectangular tick-mark ring (subtle bright-to-muted dial shading)
 * - Inner dark circle framed by a bold vivid red prohibition ring and diagonal slash (/)
 * - White line-art camera icon inside
 * - Real-time UI count-up elapsed days and HH : MM : SS display at the lower inner center
 *
 * Every geometric dimension and text metric scales proportionally to the rendered size via
 * BoxWithConstraints so compact/split-screen windows keep crisp proportions without distortion.
 */
@Composable
fun BlockedCameraRingIcon(
  elapsedDays: Long,
  elapsedTimeText: String,
  modifier: Modifier = Modifier,
  ringSize: Dp = 242.dp,
) {
  val colors = CameraGuardTheme.colors
  val blockedDesc = stringResource(R.string.cd_blocked_camera_icon)

  BoxWithConstraints(
    modifier =
      modifier
        .size(ringSize)
        .semantics(mergeDescendants = true) { contentDescription = blockedDesc }
        .testTag("blocked_camera_ring_icon"),
    contentAlignment = Alignment.Center,
  ) {
    val actualSize = minOf(maxWidth, maxHeight)
    val sizeRatio = (actualSize.value / 242f).coerceIn(0.45f, 1.15f)

    Canvas(modifier = Modifier.fillMaxSize()) {
      val minDim = size.minDimension

      // 1. Outer radial rectangular tick marks (28 evenly spaced segments, proportional to minDim)
      val tickCount = 28
      val tickLength = minDim * 0.062f
      val tickWidth = minDim * 0.031f
      val tickOuterRadius = minDim * 0.495f
      val tickCorner = CornerRadius(minDim * 0.005f, minDim * 0.005f)

      for (i in 0 until tickCount) {
        val deg = i * (360f / tickCount)
        val isBrightSegment = deg >= 195f || deg <= 22f
        val tickColor = if (isBrightSegment) colors.blockedTickBright else colors.blockedTickMuted

        rotate(degrees = deg, pivot = center) {
          drawRoundRect(
            color = tickColor,
            topLeft = Offset(center.x - tickWidth / 2f, center.y - tickOuterRadius),
            size = Size(tickWidth, tickLength),
            cornerRadius = tickCorner,
          )
        }
      }

      // 2. Inner dark circle + bold red prohibition border ring (proportional stroke)
      val redRingStroke = minDim * 0.039f
      val redRingRadius = minDim * 0.345f

      drawCircle(
        color = colors.blockedCircleInnerBg,
        radius = redRingRadius,
        center = center,
      )
      drawCircle(
        color = colors.blockedProhibitionRed,
        radius = redRingRadius,
        center = center,
        style = Stroke(width = redRingStroke),
      )

      // 3. White line-art camera positioned slightly above center to leave room for elapsed text
      drawWhiteLineCamera(
        camCenter = Offset(center.x, center.y - minDim * 0.060f),
        camW = minDim * 0.355f,
        strokeColor = colors.cameraStrokeWhite,
        strokeWidthPx = (minDim * 0.010f).coerceAtLeast(1.3.dp.toPx()),
      )

      // 4. Bold diagonal prohibition slash (/) running from bottom-left to top-right across camera
      val slashRadius = redRingRadius - redRingStroke * 0.15f
      val radBottomLeft = 135.0 * PI / 180.0
      val radTopRight = -45.0 * PI / 180.0
      val start =
        Offset(
          x = center.x + (slashRadius * cos(radBottomLeft)).toFloat(),
          y = center.y + (slashRadius * sin(radBottomLeft)).toFloat(),
        )
      val end =
        Offset(
          x = center.x + (slashRadius * cos(radTopRight)).toFloat(),
          y = center.y + (slashRadius * sin(radTopRight)).toFloat(),
        )
      drawLine(
        color = colors.blockedProhibitionRed,
        start = start,
        end = end,
        strokeWidth = minDim * 0.031f,
        cap = StrokeCap.Butt,
      )
    }

    // Live UI count-up elapsed labels positioned proportionally inside the lower half of the red ring
    val labelBottomPad = actualSize * 0.21f
    val labelFontSize = (12.5f * sizeRatio).coerceIn(8.5f, 13.5f).sp
    val labelLineHeight = (15f * sizeRatio).coerceIn(10f, 16f).sp

    Column(
      modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = labelBottomPad),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = stringResource(R.string.blocked_elapsed_days_format, elapsedDays),
        style =
          MaterialTheme.typography.labelMedium.copy(
            fontSize = labelFontSize,
            lineHeight = labelLineHeight,
            fontWeight = FontWeight.Bold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.testTag("blocked_elapsed_days_text"),
      )
      Spacer(modifier = Modifier.height((1.dp * sizeRatio).coerceAtLeast(0.5.dp)))
      Text(
        text = elapsedTimeText,
        style =
          MaterialTheme.typography.labelMedium.copy(
            fontSize = labelFontSize,
            lineHeight = labelLineHeight,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (0.2f * sizeRatio).sp,
            fontFeatureSettings = "tnum",
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
        modifier = Modifier.testTag("blocked_elapsed_time_text"),
      )
    }
  }
}

/**
 * Shared crisp white line-art DSLR camera silhouette used by both default and blocked emblems.
 */
private fun DrawScope.drawWhiteLineCamera(
  camCenter: Offset,
  camW: Float,
  strokeColor: Color,
  strokeWidthPx: Float,
) {
  val camH = camW * 0.70f
  val left = camCenter.x - camW / 2f
  val top = camCenter.y - camH / 2f
  val bodyTop = top + camH * 0.16f
  val bodyBottom = top + camH
  val corner = camW * 0.062f
  val camStroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round, join = StrokeJoin.Round)

  // Small top-left shutter button bump
  drawRoundRect(
    color = strokeColor,
    topLeft = Offset(left + camW * 0.11f, bodyTop - camH * 0.09f),
    size = Size(camW * 0.13f, camH * 0.09f),
    cornerRadius = CornerRadius(camW * 0.022f, camW * 0.022f),
    style = camStroke,
  )

  // Continuous outline of camera body with integrated trapezoidal top viewfinder
  val bodyPath =
    Path().apply {
      moveTo(left + camW * 0.30f, bodyTop)
      lineTo(left + camW * 0.37f, top)
      lineTo(left + camW * 0.63f, top)
      lineTo(left + camW * 0.70f, bodyTop)
      lineTo(left + camW - corner, bodyTop)
      quadraticTo(left + camW, bodyTop, left + camW, bodyTop + corner)
      lineTo(left + camW, bodyBottom - corner)
      quadraticTo(left + camW, bodyBottom, left + camW - corner, bodyBottom)
      lineTo(left + corner, bodyBottom)
      quadraticTo(left, bodyBottom, left, bodyBottom - corner)
      lineTo(left, bodyTop + corner)
      quadraticTo(left, bodyTop, left + corner, bodyTop)
      close()
    }
  drawPath(path = bodyPath, color = strokeColor, style = camStroke)

  // Concentric double-circle camera lens
  val lensCenter = Offset(camCenter.x, (bodyTop + bodyBottom) / 2f)
  drawCircle(
    color = strokeColor,
    radius = camH * 0.31f,
    center = lensCenter,
    style = camStroke,
  )
  drawCircle(
    color = strokeColor,
    radius = camH * 0.20f,
    center = lensCenter,
    style = Stroke(width = (strokeWidthPx * 0.78f)),
  )

  // Small circular flash/indicator dot at upper-right of body
  drawCircle(
    color = strokeColor,
    radius = camW * 0.042f,
    center = Offset(left + camW * 0.855f, bodyTop + (bodyBottom - bodyTop) * 0.24f),
    style = Stroke(width = (strokeWidthPx * 0.75f)),
  )
}
