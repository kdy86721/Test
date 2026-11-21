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

/**
 * Centered "국방헬프콜 1303" brand logo strip positioned right below the header wave transition:
 * - Left: colorful winged embracing dual-figure graphic
 * - Right: sky-blue "국방헬프콜" with curved handset swoosh + bold orange-gradient geometric "1303"
 */
@Composable
fun DefenseHelpCallBanner(modifier: Modifier = Modifier) {
  val colors = CameraGuardTheme.colors
  val logoDesc = stringResource(R.string.cd_help_call_logo)

  Row(
    modifier =
      modifier
        .fillMaxWidth()
        .semantics(mergeDescendants = true) { contentDescription = logoDesc }
        .testTag("defense_help_call_banner"),
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically,
  ) {
    // Colorful winged dual-figure symbol
    HelpCallFiguresEmblem(modifier = Modifier.size(width = 64.dp, height = 56.dp))

    Spacer(modifier = Modifier.width(8.dp))

    // "국방헬프콜" with handset underline swoosh + bold orange gradient "1303"
    Column(horizontalAlignment = Alignment.Start) {
      Box(modifier = Modifier.width(104.dp).height(23.dp)) {
        Text(
          text = stringResource(R.string.help_call_title),
          style =
            MaterialTheme.typography.titleMedium.copy(
              fontSize = 15.5.sp,
              lineHeight = 18.sp,
              fontWeight = FontWeight.ExtraBold,
              letterSpacing = (-0.4).sp,
            ),
          color = colors.helpCallBlue,
          maxLines = 1,
          modifier = Modifier.align(Alignment.TopStart),
        )

        // Stylized handset receiver on the right curving smoothly underneath as a blue swoosh
        Canvas(modifier = Modifier.matchParentSize()) {
          val w = size.width
          val h = size.height
          val handsetBrush =
            Brush.horizontalGradient(
              colors = listOf(colors.helpCallBlue, colors.helpCallBlueDeep),
            )
          val swooshPath =
            Path().apply {
              moveTo(w * 0.08f, h * 0.90f)
              quadraticTo(w * 0.56f, h * 1.00f, w * 0.87f, h * 0.83f)
              quadraticTo(w * 0.95f, h * 0.72f, w * 0.96f, h * 0.24f)
            }
          drawPath(
            path = swooshPath,
            brush = handsetBrush,
            style = Stroke(width = 2.0.dp.toPx(), cap = StrokeCap.Round),
          )
          // Small top-right receiver earpiece accent
          drawRoundRect(
            color = colors.helpCallBlueDeep,
            topLeft = Offset(w * 0.90f, h * 0.14f),
            size = Size(w * 0.065f, h * 0.22f),
            cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
          )
        }
      }

      Spacer(modifier = Modifier.height(2.dp))

      // Bold geometric orange-gradient "1303" logo digits
      OrangeBlock1303Logo(modifier = Modifier.width(102.dp).height(29.dp))
    }
  }
}

@Composable
private fun HelpCallFiguresEmblem(modifier: Modifier = Modifier) {
  val colors = CameraGuardTheme.colors

  Canvas(modifier = modifier) {
    val w = size.width
    val h = size.height

    val blueGrad =
      Brush.verticalGradient(
        colors = listOf(colors.symbolBlue, colors.symbolBlueDeep),
      )
    val yellowGreenGrad =
      Brush.verticalGradient(
        colors = listOf(colors.symbolYellow, colors.symbolGreen),
      )
    val coralGrad =
      Brush.horizontalGradient(
        colors = listOf(colors.helpCallOrangeBottom, colors.symbolCoral),
      )

    // Left winged blue figure - 3 soft layered wing tips on far left
    val wingPath =
      Path().apply {
        moveTo(w * 0.28f, h * 0.42f)
        quadraticTo(w * 0.14f, h * 0.36f, w * 0.02f, h * 0.36f)
        quadraticTo(w * 0.10f, h * 0.46f, w * 0.22f, h * 0.49f)
        quadraticTo(w * 0.11f, h * 0.50f, w * 0.06f, h * 0.51f)
        quadraticTo(w * 0.13f, h * 0.58f, w * 0.25f, h * 0.58f)
        quadraticTo(w * 0.17f, h * 0.62f, w * 0.13f, h * 0.64f)
        quadraticTo(w * 0.22f, h * 0.68f, w * 0.32f, h * 0.64f)
        close()
      }
    drawPath(path = wingPath, brush = blueGrad)

    // Right yellow-to-green figure head
    drawCircle(
      color = colors.symbolYellow,
      radius = w * 0.072f,
      center = Offset(w * 0.72f, h * 0.17f),
    )

    // Right yellow-to-green sweeping body
    val rightBody =
      Path().apply {
        moveTo(w * 0.64f, h * 0.32f)
        quadraticTo(w * 0.76f, h * 0.25f, w * 0.84f, h * 0.37f)
        quadraticTo(w * 0.87f, h * 0.54f, w * 0.78f, h * 0.72f)
        quadraticTo(w * 0.72f, h * 0.84f, w * 0.64f, h * 0.86f)
        quadraticTo(w * 0.67f, h * 0.70f, w * 0.63f, h * 0.52f)
        quadraticTo(w * 0.60f, h * 0.40f, w * 0.64f, h * 0.32f)
        close()
      }
    drawPath(path = rightBody, brush = yellowGreenGrad)

    // Warm orange-to-coral heart/embrace accent on right shoulder
    val embraceAccent =
      Path().apply {
        moveTo(w * 0.61f, h * 0.42f)
        quadraticTo(w * 0.78f, h * 0.34f, w * 0.90f, h * 0.37f)
        quadraticTo(w * 0.97f, h * 0.41f, w * 0.94f, h * 0.50f)
        quadraticTo(w * 0.89f, h * 0.58f, w * 0.63f, h * 0.57f)
        close()
      }
    drawPath(path = embraceAccent, brush = coralGrad)

    // Left blue figure head
    drawCircle(
      color = colors.symbolBlue,
      radius = w * 0.076f,
      center = Offset(w * 0.37f, h * 0.24f),
    )

    // Left blue figure torso sweeping down + reaching arm bridging across to right figure
    val blueBody =
      Path().apply {
        moveTo(w * 0.27f, h * 0.40f)
        quadraticTo(w * 0.34f, h * 0.34f, w * 0.45f, h * 0.39f)
        quadraticTo(w * 0.56f, h * 0.42f, w * 0.66f, h * 0.41f)
        lineTo(w * 0.66f, h * 0.55f)
        quadraticTo(w * 0.54f, h * 0.57f, w * 0.45f, h * 0.56f)
        quadraticTo(w * 0.48f, h * 0.74f, w * 0.46f, h * 0.96f)
        quadraticTo(w * 0.35f, h * 0.92f, w * 0.31f, h * 0.76f)
        quadraticTo(w * 0.26f, h * 0.60f, w * 0.27f, h * 0.40f)
        close()
      }
    drawPath(path = blueBody, brush = blueGrad)
  }
}

/**
 * Custom rounded-block geometric "1303" logo digits with a vivid yellow-orange to tangerine gradient.
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

    val stroke = h * 0.235f
    val r = CornerRadius(2.4.dp.toPx(), 2.4.dp.toPx())
    val gap = w * 0.055f

    val w1 = w * 0.145f
    val wDigit = (w - w1 - gap * 3f) / 3f

    // --- Digit '1' ---
    val x1 = 0f
    drawRoundRect(
      brush = orangeBrush,
      topLeft = Offset(x1, 0f),
      size = Size(w1, stroke),
      cornerRadius = r,
    )
    drawRoundRect(
      brush = orangeBrush,
      topLeft = Offset(x1 + w1 - stroke, 0f),
      size = Size(stroke, h),
      cornerRadius = r,
    )

    fun drawBlockThree(left: Float) {
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left, 0f),
        size = Size(wDigit, stroke),
        cornerRadius = r,
      )
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left + wDigit * 0.16f, (h - stroke) / 2f),
        size = Size(wDigit * 0.84f, stroke),
        cornerRadius = r,
      )
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left, h - stroke),
        size = Size(wDigit, stroke),
        cornerRadius = r,
      )
      drawRoundRect(
        brush = orangeBrush,
        topLeft = Offset(left + wDigit - stroke, 0f),
        size = Size(stroke, h),
        cornerRadius = r,
      )
    }

    // --- First Digit '3' ---
    val x2 = x1 + w1 + gap
    drawBlockThree(x2)

    // --- Digit '0' ---
    val x3 = x2 + wDigit + gap
    val zeroPath =
      Path().apply {
        fillType = PathFillType.EvenOdd
        addRoundRect(
          RoundRect(
            rect = Rect(left = x3, top = 0f, right = x3 + wDigit, bottom = h),
            cornerRadius = CornerRadius(3.5.dp.toPx(), 3.5.dp.toPx()),
          ),
        )
        addRoundRect(
          RoundRect(
            rect =
              Rect(
                left = x3 + stroke,
                top = stroke,
                right = x3 + wDigit - stroke,
                bottom = h - stroke,
              ),
            cornerRadius = CornerRadius(1.5.dp.toPx(), 1.5.dp.toPx()),
          ),
        )
      }
    drawPath(path = zeroPath, brush = orangeBrush)

    // --- Second Digit '3' ---
    val x4 = x3 + wDigit + gap
    drawBlockThree(x4)
  }
}

/**
 * Default (unblocked) central graphic: thick translucent smoky ring enclosing a white line camera.
 */
@Composable
fun CameraRingIcon(modifier: Modifier = Modifier) {
  val colors = CameraGuardTheme.colors
  val cameraDesc = stringResource(R.string.cd_camera_status_icon)

  Box(
    modifier =
      modifier
        .size(216.dp)
        .semantics { contentDescription = cameraDesc }
        .testTag("camera_ring_icon"),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val ringStrokePx = 22.dp.toPx()
      val ringRadius = (size.minDimension - ringStrokePx) / 2f

      drawCircle(
        color = colors.cameraRingTranslucent,
        radius = ringRadius,
        center = center,
        style = Stroke(width = ringStrokePx),
      )

      drawWhiteLineCamera(
        camCenter = Offset(center.x, center.y + 2.dp.toPx()),
        camW = size.width * 0.43f,
        strokeColor = colors.cameraStrokeWhite,
        strokeWidthPx = 2.6.dp.toPx(),
      )
    }
  }
}

/**
 * Blocked active state central emblem matching the sixth reference image:
 * - Outer 28-segment radial rectangular tick-mark ring (subtle bright-to-muted dial shading)
 * - Inner dark circle framed by a bold vivid red prohibition ring and diagonal slash (/)
 * - White line-art camera icon inside
 * - Real-time UI count-up elapsed days and HH : MM : SS display at the lower inner center
 */
@Composable
fun BlockedCameraRingIcon(
  elapsedDays: Long,
  elapsedTimeText: String,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val blockedDesc = stringResource(R.string.cd_blocked_camera_icon)

  Box(
    modifier =
      modifier
        .size(242.dp)
        .semantics(mergeDescendants = true) { contentDescription = blockedDesc }
        .testTag("blocked_camera_ring_icon"),
    contentAlignment = Alignment.Center,
  ) {
    Canvas(modifier = Modifier.fillMaxSize()) {
      val minDim = size.minDimension

      // 1. Outer radial rectangular tick marks (28 evenly spaced segments)
      val tickCount = 28
      val tickLength = 15.dp.toPx()
      val tickWidth = 7.5.dp.toPx()
      val tickOuterRadius = minDim * 0.495f
      val tickCorner = CornerRadius(1.2.dp.toPx(), 1.2.dp.toPx())

      for (i in 0 until tickCount) {
        // Angle 0° = 12 o'clock, progressing clockwise
        val deg = i * (360f / tickCount)
        // Upper-left through top-right arc (~205°..360° & 0°..25°) brighter, lower-right muted
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

      // 2. Inner dark circle + bold red prohibition border ring
      val redRingStroke = 9.5.dp.toPx()
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

      // 3. White line-art camera positioned slightly above vertical center to leave room for text
      drawWhiteLineCamera(
        camCenter = Offset(center.x, center.y - 14.dp.toPx()),
        camW = minDim * 0.355f,
        strokeColor = colors.cameraStrokeWhite,
        strokeWidthPx = 2.4.dp.toPx(),
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
        strokeWidth = 7.5.dp.toPx(),
        cap = StrokeCap.Butt,
      )
    }

    // Live UI count-up elapsed labels centered inside the lower half of the red ring
    Column(
      modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 52.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      Text(
        text = stringResource(R.string.blocked_elapsed_days_format, elapsedDays),
        style =
          MaterialTheme.typography.labelMedium.copy(
            fontSize = 12.5.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.Bold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("blocked_elapsed_days_text"),
      )
      Spacer(modifier = Modifier.height(1.dp))
      Text(
        text = elapsedTimeText,
        style =
          MaterialTheme.typography.labelMedium.copy(
            fontSize = 12.5.sp,
            lineHeight = 15.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 0.2.sp,
            fontFeatureSettings = "tnum",
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
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
  val corner = 5.5.dp.toPx()
  val camStroke = Stroke(width = strokeWidthPx, cap = StrokeCap.Round, join = StrokeJoin.Round)

  // Small top-left shutter button bump
  drawRoundRect(
    color = strokeColor,
    topLeft = Offset(left + camW * 0.11f, bodyTop - camH * 0.09f),
    size = Size(camW * 0.13f, camH * 0.09f),
    cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
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
