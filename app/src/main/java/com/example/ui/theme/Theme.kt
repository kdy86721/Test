package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class CameraGuardColors(
  val mainBackground: Color = MainBackground,
  val mainBackgroundLower: Color = MainBackgroundLower,
  val blockedMainBackground: Color = BlockedMainBackground,
  val blockedMainBackgroundLower: Color = BlockedMainBackgroundLower,
  val mainFooterBackground: Color = MainFooterBackground,
  val menuBackground: Color = MenuBackground,
  val headerCrimson: Color = HeaderCrimson,
  val headerCrimsonTop: Color = HeaderCrimsonTop,
  val headerWaveShadow: Color = HeaderWaveShadow,
  val headerWaveSecondary: Color = HeaderWaveSecondary,
  val headerAmberTop: Color = HeaderAmberTop,
  val headerAmberBottom: Color = HeaderAmberBottom,
  val blockedWaveShadow: Color = BlockedWaveShadow,
  val blockedWaveSecondary: Color = BlockedWaveSecondary,
  val headerOutlineWhite: Color = HeaderOutlineWhite,
  val emblemWhite: Color = EmblemWhite,
  val emblemLineGray: Color = EmblemLineGray,
  val emblemStarGold: Color = EmblemStarGold,
  val emblemStarRed: Color = EmblemStarRed,
  val cameraRingTranslucent: Color = CameraRingTranslucent,
  val cameraStrokeWhite: Color = CameraStrokeWhite,
  val cameraBlockButtonFill: Color = CameraBlockButtonFill,
  val unblockButtonSlate: Color = UnblockButtonSlate,
  val blockedTickBright: Color = BlockedTickBright,
  val blockedTickMuted: Color = BlockedTickMuted,
  val blockedProhibitionRed: Color = BlockedProhibitionRed,
  val blockedCircleInnerBg: Color = BlockedCircleInnerBg,
  val versionTextMuted: Color = VersionTextMuted,
  val symbolBlue: Color = SymbolBlue,
  val symbolBlueDeep: Color = SymbolBlueDeep,
  val symbolYellow: Color = SymbolYellow,
  val symbolGreen: Color = SymbolGreen,
  val symbolCoral: Color = SymbolCoral,
  val helpCallBlue: Color = HelpCallBlue,
  val helpCallBlueDeep: Color = HelpCallBlueDeep,
  val helpCallOrangeTop: Color = HelpCallOrangeTop,
  val helpCallOrangeBottom: Color = HelpCallOrangeBottom,
  val menuHeaderDivider: Color = MenuHeaderDivider,
  val menuRowDivider: Color = MenuRowDivider,
  val menuTextPrimary: Color = MenuTextPrimary,
  val menuTextSection: Color = MenuTextSection,
  val menuTextSecondary: Color = MenuTextSecondary,
  val menuTextValue: Color = MenuTextValue,
  val menuIconTint: Color = MenuIconTint,
  val menuChevronTint: Color = MenuChevronTint,
  val menuCloseTint: Color = MenuCloseTint,
  val mintToggleActive: Color = MintToggleActive,
  val mintToggleInactive: Color = MintToggleInactive,
  val toggleThumbWhite: Color = ToggleThumbWhite,
  val bottomCoralRed: Color = BottomCoralRed,
)

val LocalCameraGuardColors = staticCompositionLocalOf { CameraGuardColors() }

private val CameraGuardDarkColorScheme =
  darkColorScheme(
    primary = HeaderCrimson,
    onPrimary = MenuTextPrimary,
    secondary = MintToggleActive,
    onSecondary = MainBackground,
    tertiary = CameraBlockButtonFill,
    onTertiary = MenuTextPrimary,
    background = MainBackground,
    onBackground = MenuTextPrimary,
    surface = MenuBackground,
    onSurface = MenuTextPrimary,
    surfaceVariant = MainBackgroundLower,
    onSurfaceVariant = MenuTextSecondary,
    outline = MenuRowDivider,
  )

object CameraGuardTheme {
  val colors: CameraGuardColors
    @Composable get() = LocalCameraGuardColors.current
}

@Composable
fun CameraGuardAppTheme(content: @Composable () -> Unit) {
  val appColors = CameraGuardColors()
  CompositionLocalProvider(LocalCameraGuardColors provides appColors) {
    MaterialTheme(
      colorScheme = CameraGuardDarkColorScheme,
      typography = AppTypography,
      content = content,
    )
  }
}
