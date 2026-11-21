package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
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
 * High-precision Ministry of National Defense emblem rendered in Compose Canvas:
 * - White silhouette combining Navy anchor (shackle ring, stock crossbar, draped chain links,
 *   vertical shaft, curved twin arrow flukes, pointed crown) and 3-tiered stepped Air Force eagle wings
 * - Fine pale steel-gray contour & feather separation lines defining internal relief
 * - Center 3D 10-facet 5-pointed star alternating gold (#F5C400) and crimson (#E52B20)
 * Shared across both Main Screen header and Menu/Settings Screen header.
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
    val cx = w * 0.5f

    val white = colors.emblemWhite
    val lineColor = colors.emblemLineGray
    val haloWidth = (w * 0.045f).coerceIn(2.0.dp.toPx(), 4.2.dp.toPx())
    val contourStroke =
      Stroke(
        width = (w * 0.0115f).coerceIn(0.75.dp.toPx(), 1.35.dp.toPx()),
        cap = StrokeCap.Round,
        join = StrokeJoin.Round,
      )
    val fineLineWidth = (w * 0.0095f).coerceIn(0.65.dp.toPx(), 1.15.dp.toPx())

    // Build reusable paths for outer white halo pass + crisp foreground & contour pass
    val wingsPath = buildThreeTierWingsPath(w, h, cx)
    val anchorCrownAndFlukesPath = buildAnchorArmsPath(w, h, cx)
    val anchorShaftPath = buildAnchorShaftPath(w, h, cx)
    val anchorCrossbarPath = buildAnchorCrossbarPath(w, h, cx)

    // --- PASS 1: Crisp outer white silhouette backing (unifies emblem into clean white badge) ---
    val haloStroke = Stroke(width = haloWidth, cap = StrokeCap.Round, join = StrokeJoin.Round)
    drawPath(path = wingsPath, color = white, style = haloStroke)
    drawPath(path = anchorCrownAndFlukesPath, color = white, style = haloStroke)
    drawPath(path = anchorShaftPath, color = white, style = haloStroke)
    drawPath(path = anchorCrossbarPath, color = white, style = haloStroke)
    drawCircle(
      color = white,
      radius = h * 0.095f,
      center = Offset(cx, h * 0.145f),
      style = Stroke(width = h * 0.08f + haloWidth * 0.5f),
    )
    drawAnchorChainLinks(
      w = w,
      h = h,
      cx = cx,
      fillColor = white,
      outlineColor = white,
      isHaloPass = true,
      haloWidth = haloWidth * 0.6f,
      contourWidth = fineLineWidth,
    )

    // --- PASS 2: 3-Tiered Air Force Eagle Wings (fill + subtle steel-gray contour & feather lines) ---
    drawPath(path = wingsPath, color = white)
    drawPath(path = wingsPath, color = lineColor, style = contourStroke)

    // Horizontal feather tier separation lines & subtle inner feather ribs on both wings
    val tier1Y = h * 0.485f
    val tier2Y = h * 0.575f
    // Left wing feather dividers
    drawLine(
      color = lineColor,
      start = Offset(w * 0.115f, tier1Y),
      end = Offset(w * 0.40f, tier1Y),
      strokeWidth = fineLineWidth,
      cap = StrokeCap.Round,
    )
    drawLine(
      color = lineColor,
      start = Offset(w * 0.185f, tier2Y),
      end = Offset(w * 0.40f, tier2Y),
      strokeWidth = fineLineWidth,
      cap = StrokeCap.Round,
    )
    // Subtle upper feather sheen line on left wing
    drawLine(
      color = lineColor.copy(alpha = 0.65f),
      start = Offset(w * 0.085f, h * 0.432f),
      end = Offset(w * 0.36f, h * 0.432f),
      strokeWidth = fineLineWidth * 0.78f,
      cap = StrokeCap.Round,
    )
    // Right wing feather dividers (mirrored)
    drawLine(
      color = lineColor,
      start = Offset(w * 0.885f, tier1Y),
      end = Offset(w * 0.60f, tier1Y),
      strokeWidth = fineLineWidth,
      cap = StrokeCap.Round,
    )
    drawLine(
      color = lineColor,
      start = Offset(w * 0.815f, tier2Y),
      end = Offset(w * 0.60f, tier2Y),
      strokeWidth = fineLineWidth,
      cap = StrokeCap.Round,
    )
    drawLine(
      color = lineColor.copy(alpha = 0.65f),
      start = Offset(w * 0.915f, h * 0.432f),
      end = Offset(w * 0.64f, h * 0.432f),
      strokeWidth = fineLineWidth * 0.78f,
      cap = StrokeCap.Round,
    )

    // --- PASS 3: Navy Anchor (ring, crossbar, draped chain links, vertical shaft, twin flukes) ---
    // Top circular shackle ring (white ring with crisp inner & outer gray contours)
    val ringCenter = Offset(cx, h * 0.145f)
    val ringOuterR = h * 0.095f
    val ringInnerR = h * 0.044f
    drawCircle(
      color = white,
      radius = (ringOuterR + ringInnerR) * 0.5f,
      center = ringCenter,
      style = Stroke(width = ringOuterR - ringInnerR),
    )
    drawCircle(
      color = lineColor,
      radius = ringOuterR,
      center = ringCenter,
      style = contourStroke,
    )
    drawCircle(
      color = lineColor,
      radius = ringInnerR,
      center = ringCenter,
      style = Stroke(width = fineLineWidth),
    )

    // Horizontal anchor stock crossbar
    drawPath(path = anchorCrossbarPath, color = white)
    drawPath(path = anchorCrossbarPath, color = lineColor, style = contourStroke)

    // Vertical anchor shaft
    drawPath(path = anchorShaftPath, color = white)
    drawPath(path = anchorShaftPath, color = lineColor, style = contourStroke)

    // Draped 3-link diagonal chain from right side of shackle ring down toward upper-right wing
    drawAnchorChainLinks(
      w = w,
      h = h,
      cx = cx,
      fillColor = white,
      outlineColor = lineColor,
      isHaloPass = false,
      haloWidth = 0f,
      contourWidth = fineLineWidth,
    )

    // Curved lower anchor arms with pointed twin arrow flukes + sharp diamond bottom crown
    drawPath(path = anchorCrownAndFlukesPath, color = white)
    drawPath(path = anchorCrownAndFlukesPath, color = lineColor, style = contourStroke)

    // Subtle vertical center rib on lower anchor crown
    drawLine(
      color = lineColor.copy(alpha = 0.75f),
      start = Offset(cx, h * 0.72f),
      end = Offset(cx, h * 0.90f),
      strokeWidth = fineLineWidth * 0.85f,
      cap = StrokeCap.Round,
    )

    // --- PASS 4: Center 3D Faceted 5-Pointed Star (gold #F5C400 & crimson #E52B20) ---
    val starCenter = Offset(cx, h * 0.515f)
    val outerRadius = h * 0.275f
    val innerRadius = outerRadius * 0.382f
    val vertices = Array(10) { idx ->
      val angleDeg = -90.0 + idx * 36.0
      val angleRad = angleDeg * PI / 180.0
      val r = if (idx % 2 == 0) outerRadius else innerRadius
      Offset(
        x = starCenter.x + (r * cos(angleRad)).toFloat(),
        y = starCenter.y + (r * sin(angleRad)).toFloat(),
      )
    }

    // Thin white separation rim behind the star so it pops cleanly over the anchor/wings
    val starOutlinePath =
      Path().apply {
        moveTo(vertices[0].x, vertices[0].y)
        for (i in 1 until 10) {
          lineTo(vertices[i].x, vertices[i].y)
        }
        close()
      }
    drawPath(
      path = starOutlinePath,
      color = white,
      style = Stroke(width = fineLineWidth * 2.1f, join = StrokeJoin.Miter),
    )

    // 10 alternating triangular 3D facets:
    // Left half of top point = Gold (#F5C400), Right half of top point = Red (#E52B20)
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

    // Ultra-crisp subtle outer edge around the star
    drawPath(
      path = starOutlinePath,
      color = colors.emblemStarRed.copy(alpha = 0.28f),
      style = Stroke(width = fineLineWidth * 0.65f, join = StrokeJoin.Miter),
    )
  }
}

private fun buildThreeTierWingsPath(w: Float, h: Float, cx: Float): Path =
  Path().apply {
    // Top straight leading edge sweeping from center out to left wing tip
    moveTo(cx, h * 0.39f)
    lineTo(w * 0.068f, h * 0.39f)
    // Tier 1 outer rounded feather tip (left)
    cubicTo(w * 0.042f, h * 0.39f, w * 0.038f, h * 0.465f, w * 0.095f, h * 0.485f)
    lineTo(w * 0.13f, h * 0.485f)
    // Tier 2 stepped feather tip (left)
    cubicTo(w * 0.105f, h * 0.515f, w * 0.112f, h * 0.568f, w * 0.17f, h * 0.575f)
    lineTo(w * 0.20f, h * 0.575f)
    // Tier 3 lowest stepped feather tip (left)
    cubicTo(w * 0.185f, h * 0.605f, w * 0.195f, h * 0.652f, w * 0.255f, h * 0.660f)
    lineTo(cx, h * 0.660f)

    // Right wing (exact mirror)
    lineTo(w * 0.745f, h * 0.660f)
    cubicTo(w * 0.805f, h * 0.652f, w * 0.815f, h * 0.605f, w * 0.80f, h * 0.575f)
    lineTo(w * 0.83f, h * 0.575f)
    cubicTo(w * 0.888f, h * 0.568f, w * 0.895f, h * 0.515f, w * 0.87f, h * 0.485f)
    lineTo(w * 0.905f, h * 0.485f)
    cubicTo(w * 0.962f, h * 0.465f, w * 0.958f, h * 0.39f, w * 0.932f, h * 0.39f)
    close()
  }

private fun buildAnchorCrossbarPath(w: Float, h: Float, cx: Float): Path =
  Path().apply {
    val left = cx - w * 0.105f
    val right = cx + w * 0.105f
    val top = h * 0.235f
    val bottom = h * 0.305f
    val r = (bottom - top) * 0.45f
    moveTo(left + r, top)
    lineTo(right - r, top)
    quadraticTo(right, top, right, (top + bottom) * 0.5f)
    quadraticTo(right, bottom, right - r, bottom)
    lineTo(left + r, bottom)
    quadraticTo(left, bottom, left, (top + bottom) * 0.5f)
    quadraticTo(left, top, left + r, top)
    close()
  }

private fun buildAnchorShaftPath(w: Float, h: Float, cx: Float): Path =
  Path().apply {
    val halfTop = w * 0.032f
    val halfBottom = w * 0.046f
    moveTo(cx - halfTop, h * 0.235f)
    lineTo(cx + halfTop, h * 0.235f)
    lineTo(cx + halfBottom, h * 0.84f)
    lineTo(cx - halfBottom, h * 0.84f)
    close()
  }

private fun buildAnchorArmsPath(w: Float, h: Float, cx: Float): Path =
  Path().apply {
    // Left pointed arrow fluke barb
    moveTo(cx - w * 0.225f, h * 0.655f)
    lineTo(cx - w * 0.145f, h * 0.695f)
    lineTo(cx - w * 0.170f, h * 0.718f)
    // Inner curve down to center shaft junction
    quadraticTo(cx - w * 0.115f, h * 0.805f, cx, h * 0.835f)
    // Inner curve up to right fluke barb
    quadraticTo(cx + w * 0.115f, h * 0.805f, cx + w * 0.170f, h * 0.718f)
    lineTo(cx + w * 0.145f, h * 0.695f)
    lineTo(cx + w * 0.225f, h * 0.655f)
    lineTo(cx + w * 0.228f, h * 0.752f)
    lineTo(cx + w * 0.200f, h * 0.735f)
    // Outer graceful curve down to sharp bottom crown tip
    quadraticTo(cx + w * 0.148f, h * 0.862f, cx, h * 0.942f)
    quadraticTo(cx - w * 0.148f, h * 0.862f, cx - w * 0.200f, h * 0.735f)
    lineTo(cx - w * 0.228f, h * 0.752f)
    close()
  }

private fun DrawScope.drawAnchorChainLinks(
  w: Float,
  h: Float,
  cx: Float,
  fillColor: Color,
  outlineColor: Color,
  isHaloPass: Boolean,
  haloWidth: Float,
  contourWidth: Float,
) {
  // Two tilted oval chain links draping from upper-right of shackle ring toward upper-right wing
  val links =
    listOf(
      Triple(Offset(cx + w * 0.088f, h * 0.245f), 42f, Size(w * 0.075f, h * 0.125f)),
      Triple(Offset(cx + w * 0.142f, h * 0.345f), 32f, Size(w * 0.068f, h * 0.115f)),
    )

  for ((centerPos, deg, linkSize) in links) {
    rotate(degrees = deg, pivot = centerPos) {
      val topLeft = Offset(centerPos.x - linkSize.width / 2f, centerPos.y - linkSize.height / 2f)
      val cr = CornerRadius(linkSize.width * 0.48f, linkSize.width * 0.48f)
      val linkThickness = linkSize.width * 0.34f
      if (isHaloPass) {
        drawRoundRect(
          color = fillColor,
          topLeft = topLeft,
          size = linkSize,
          cornerRadius = cr,
          style = Stroke(width = linkThickness + haloWidth),
        )
      } else {
        drawRoundRect(
          color = fillColor,
          topLeft = topLeft,
          size = linkSize,
          cornerRadius = cr,
          style = Stroke(width = linkThickness),
        )
        drawRoundRect(
          color = outlineColor,
          topLeft = topLeft,
          size = linkSize,
          cornerRadius = cr,
          style = Stroke(width = contourWidth),
        )
      }
    }
  }
}
