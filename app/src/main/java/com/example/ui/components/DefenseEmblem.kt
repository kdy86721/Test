package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.theme.CameraGuardTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Reusable white winged-anchor emblem topped with a 3D faceted gold/crimson 5-pointed star.
 * Shared across the Main Screen header and the Menu/Settings Screen header.
 */
@Composable
fun DefenseWingAnchorEmblem(
  modifier: Modifier = Modifier,
  testTag: String = "defense_header_emblem",
) {
  val colors = CameraGuardTheme.colors
  val emblemDesc = stringResource(R.string.cd_defense_emblem)

  Canvas(
    modifier =
      modifier
        .semantics { contentDescription = emblemDesc }
        .testTag(testTag),
  ) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val lineStroke = Stroke(width = 1.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)

    // 1. Symmetric white layered wings
    val wingPath =
      Path().apply {
        // Left outer wing
        moveTo(cx, h * 0.37f)
        lineTo(w * 0.08f, h * 0.37f)
        quadraticTo(w * 0.09f, h * 0.47f, w * 0.17f, h * 0.50f)
        quadraticTo(w * 0.14f, h * 0.58f, w * 0.22f, h * 0.61f)
        quadraticTo(w * 0.21f, h * 0.68f, w * 0.30f, h * 0.69f)
        lineTo(cx, h * 0.69f)
        // Right outer wing (mirrored)
        lineTo(w * 0.70f, h * 0.69f)
        quadraticTo(w * 0.79f, h * 0.68f, w * 0.78f, h * 0.61f)
        quadraticTo(w * 0.86f, h * 0.58f, w * 0.83f, h * 0.50f)
        quadraticTo(w * 0.91f, h * 0.47f, w * 0.92f, h * 0.37f)
        close()
      }
    drawPath(path = wingPath, color = colors.emblemWhite)
    drawPath(path = wingPath, color = colors.emblemLineGray, style = lineStroke)

    // Subtle feather separation lines on left & right wings
    drawLine(
      color = colors.emblemLineGray,
      start = Offset(w * 0.17f, h * 0.50f),
      end = Offset(w * 0.36f, h * 0.50f),
      strokeWidth = 0.9.dp.toPx(),
    )
    drawLine(
      color = colors.emblemLineGray,
      start = Offset(w * 0.22f, h * 0.60f),
      end = Offset(w * 0.36f, h * 0.60f),
      strokeWidth = 0.9.dp.toPx(),
    )
    drawLine(
      color = colors.emblemLineGray,
      start = Offset(w * 0.83f, h * 0.50f),
      end = Offset(w * 0.64f, h * 0.50f),
      strokeWidth = 0.9.dp.toPx(),
    )
    drawLine(
      color = colors.emblemLineGray,
      start = Offset(w * 0.78f, h * 0.60f),
      end = Offset(w * 0.64f, h * 0.60f),
      strokeWidth = 0.9.dp.toPx(),
    )

    // 2. White maritime anchor behind the star
    // Top shackle ring
    drawCircle(
      color = colors.emblemWhite,
      radius = h * 0.085f,
      center = Offset(cx, h * 0.11f),
      style = Stroke(width = 2.4.dp.toPx()),
    )
    // Upper horizontal crossbar (stock)
    drawRoundRect(
      color = colors.emblemWhite,
      topLeft = Offset(cx - w * 0.11f, h * 0.19f),
      size = Size(w * 0.22f, h * 0.075f),
      cornerRadius = CornerRadius(3.dp.toPx(), 3.dp.toPx()),
    )
    // Vertical anchor shaft
    drawRoundRect(
      color = colors.emblemWhite,
      topLeft = Offset(cx - w * 0.042f, h * 0.19f),
      size = Size(w * 0.084f, h * 0.68f),
      cornerRadius = CornerRadius(2.dp.toPx(), 2.dp.toPx()),
    )
    // Curved lower anchor arms + pointed crown at bottom
    val anchorArmsPath =
      Path().apply {
        moveTo(cx - w * 0.22f, h * 0.68f)
        quadraticTo(cx - w * 0.18f, h * 0.86f, cx, h * 0.94f)
        quadraticTo(cx + w * 0.18f, h * 0.86f, cx + w * 0.22f, h * 0.68f)
        lineTo(cx + w * 0.15f, h * 0.70f)
        quadraticTo(cx + w * 0.12f, h * 0.81f, cx, h * 0.86f)
        quadraticTo(cx - w * 0.12f, h * 0.81f, cx - w * 0.15f, h * 0.70f)
        close()
      }
    drawPath(path = anchorArmsPath, color = colors.emblemWhite)
    drawPath(path = anchorArmsPath, color = colors.emblemLineGray, style = lineStroke)

    // Pointed bottom anchor diamond tip
    val bottomCrown =
      Path().apply {
        moveTo(cx, h * 0.96f)
        lineTo(cx - w * 0.055f, h * 0.87f)
        lineTo(cx + w * 0.055f, h * 0.87f)
        close()
      }
    drawPath(path = bottomCrown, color = colors.emblemWhite)

    // 3. Center faceted 5-pointed gold & crimson star (10 alternating triangular facets)
    val starCenter = Offset(cx, h * 0.51f)
    val outerRadius = h * 0.24f
    val innerRadius = outerRadius * 0.39f
    val vertices = Array(10) { idx ->
      val angleDeg = -90.0 + idx * 36.0
      val angleRad = angleDeg * PI / 180.0
      val r = if (idx % 2 == 0) outerRadius else innerRadius
      Offset(
        x = starCenter.x + (r * cos(angleRad)).toFloat(),
        y = starCenter.y + (r * sin(angleRad)).toFloat(),
      )
    }

    for (i in 0 until 10) {
      val next = (i + 1) % 10
      val facet =
        Path().apply {
          moveTo(starCenter.x, starCenter.y)
          lineTo(vertices[i].x, vertices[i].y)
          lineTo(vertices[next].x, vertices[next].y)
          close()
        }
      val facetColor = if (i % 2 == 0) colors.emblemStarRed else colors.emblemStarGold
      drawPath(path = facet, color = facetColor)
    }
  }
}
