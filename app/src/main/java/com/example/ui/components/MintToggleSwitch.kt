package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CameraGuardTheme

/**
 * Mint-green visual toggle switch matching the settings screenshot, with smooth
 * spring thumb motion and 48dp minimum touch target.
 */
@Composable
fun MintToggleSwitch(
  checked: Boolean,
  onCheckedChange: (Boolean) -> Unit,
  testTag: String,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val trackWidth = 46.dp
  val trackHeight = 26.dp
  val thumbSize = 21.dp
  val thumbPadding = 2.5.dp

  val trackColor by
    animateColorAsState(
      targetValue = if (checked) colors.mintToggleActive else colors.mintToggleInactive,
      animationSpec = tween(durationMillis = 180),
      label = "toggleTrackColor",
    )

  val thumbOffset by
    animateDpAsState(
      targetValue = if (checked) trackWidth - thumbSize - thumbPadding else thumbPadding,
      animationSpec =
        spring(
          dampingRatio = Spring.DampingRatioMediumBouncy,
          stiffness = Spring.StiffnessMediumLow,
        ),
      label = "toggleThumbOffset",
    )

  val interactionSource = remember { MutableInteractionSource() }

  Box(
    modifier =
      modifier
        .defaultMinSize(minWidth = 48.dp, minHeight = 48.dp)
        .toggleable(
          value = checked,
          interactionSource = interactionSource,
          indication = null,
          role = Role.Switch,
          onValueChange = onCheckedChange,
        )
        .testTag(testTag)
        .padding(vertical = 8.dp),
    contentAlignment = Alignment.CenterEnd,
  ) {
    Box(
      modifier =
        Modifier.size(width = trackWidth, height = trackHeight)
          .clip(RoundedCornerShape(percent = 50))
          .background(trackColor),
      contentAlignment = Alignment.CenterStart,
    ) {
      Box(
        modifier =
          Modifier.offset(x = thumbOffset)
            .size(thumbSize)
            .shadow(elevation = 2.dp, shape = CircleShape)
            .clip(CircleShape)
            .background(colors.toggleThumbWhite),
      )
    }
  }
}
