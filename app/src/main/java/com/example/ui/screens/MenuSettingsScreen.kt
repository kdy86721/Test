package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.DefenseWingAnchorEmblem
import com.example.ui.components.MintToggleSwitch
import com.example.ui.theme.CameraGuardAppTheme
import com.example.ui.theme.CameraGuardTheme

/**
 * Screen 2: Menu / Settings Screen (UI mockup only).
 * Displays close (X) action, support center info, app info links, visual-only alert toggles,
 * static dummy system info, and bottom coral-red placeholder action area.
 */
@Composable
fun MenuSettingsScreen(
  isBlocked: Boolean,
  manufacturer: String,
  modelName: String,
  osVersion: String,
  voiceAlertEnabled: Boolean,
  vibrationAlertEnabled: Boolean,
  onToggleVoiceAlert: () -> Unit,
  onToggleVibrationAlert: () -> Unit,
  onClose: () -> Unit,
  modifier: Modifier = Modifier,
) {
  BackHandler(onBack = onClose)

  val colors = CameraGuardTheme.colors

  Box(
    modifier =
      modifier
        .fillMaxSize()
        .background(colors.menuBackground)
        .statusBarsPadding()
        .navigationBarsPadding(),
    contentAlignment = Alignment.TopCenter,
  ) {
    Column(
      modifier = Modifier.fillMaxSize().widthIn(max = 560.dp),
    ) {
      Column(
        modifier = Modifier.weight(1f).fillMaxWidth().verticalScroll(rememberScrollState()),
      ) {
        // Top header: close (X) on top-right, reserved masked space, and customer support info
        MenuHeaderSection(onClose = onClose)

        // Stronger primary divider right below support phone info
        HorizontalDivider(
          modifier = Modifier.padding(horizontal = 12.dp),
          thickness = 1.5.dp,
          color = colors.menuHeaderDivider,
        )

        // Section 1: 앱 정보
        MenuSectionHeader(title = stringResource(R.string.section_app_info))
        MenuThinDivider()

        MenuNavigationRow(
          icon = Icons.Outlined.Description,
          title = stringResource(R.string.menu_user_guide),
          onClick = { /* UI mockup only */ },
          testTag = "menu_user_guide_row",
        )
        MenuThinDivider()

        MenuNavigationRow(
          icon = Icons.AutoMirrored.Outlined.FormatListBulleted,
          title = stringResource(R.string.menu_view_logs),
          onClick = { /* UI mockup only */ },
          testTag = "menu_view_logs_row",
        )
        MenuThinDivider()

        // Notification visual toggle rows
        MenuToggleRow(
          icon = Icons.Outlined.Mic,
          title = stringResource(R.string.setting_voice_alert_title),
          subtitle = stringResource(R.string.setting_voice_alert_desc),
          checked = voiceAlertEnabled,
          onToggle = onToggleVoiceAlert,
          rowTestTag = "voice_alert_row",
          switchTestTag = "voice_alert_switch",
        )
        MenuThinDivider()

        MenuToggleRow(
          icon = Icons.Outlined.Vibration,
          title = stringResource(R.string.setting_vibration_alert_title),
          subtitle = stringResource(R.string.setting_vibration_alert_desc),
          checked = vibrationAlertEnabled,
          onToggle = onToggleVibrationAlert,
          rowTestTag = "vibration_alert_row",
          switchTestTag = "vibration_alert_switch",
        )
        MenuThinDivider()

        // Shown only when camera is in blocked (locked) visual state
        if (isBlocked) {
          MenuNavigationRow(
            icon = Icons.Outlined.LocationOn,
            title = stringResource(R.string.menu_location_unblock),
            onClick = { /* UI mockup only - no GPS or location logic */ },
            testTag = "menu_location_unblock_row",
          )
          MenuThinDivider()
        }

        // Section 2: 시스템 정보 (synced with hidden display-info editor state)
        MenuSectionHeader(title = stringResource(R.string.section_system_info))
        MenuThinDivider()

        SystemInfoRow(
          label = stringResource(R.string.sys_info_manufacturer_label),
          value = manufacturer,
          testTag = "sys_info_manufacturer_row",
        )
        MenuThinDivider()

        SystemInfoRow(
          label = stringResource(R.string.sys_info_model_label),
          value = modelName,
          testTag = "sys_info_model_row",
        )
        MenuThinDivider()

        SystemInfoRow(
          label = stringResource(R.string.sys_info_os_label),
          value = osVersion,
          testTag = "sys_info_os_row",
        )

        Spacer(modifier = Modifier.height(24.dp))
      }

      // Bottom wide red button bar ("국방모바일보안앱 삭제", ripple-only UI mockup)
      BottomDeleteAppButton(
        onClick = { /* UI mockup only - no uninstall or system settings action */ },
        modifier =
          Modifier.fillMaxWidth().padding(start = 14.dp, end = 14.dp, top = 8.dp, bottom = 14.dp),
      )
    }
  }
}

@Composable
private fun MenuHeaderSection(onClose: () -> Unit) {
  val colors = CameraGuardTheme.colors

  Box(
    modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
  ) {
    // Top-right Close (X) button with 48dp accessible hit area
    IconButton(
      onClick = onClose,
      modifier =
        Modifier.align(Alignment.TopEnd)
          .padding(end = 6.dp)
          .size(48.dp)
          .testTag("close_menu_button"),
    ) {
      Icon(
        imageVector = Icons.Rounded.Close,
        contentDescription = stringResource(R.string.cd_close_menu),
        tint = colors.menuCloseTint,
        modifier = Modifier.size(28.dp),
      )
    }

    Column(
      modifier = Modifier.fillMaxWidth().padding(top = 10.dp, start = 24.dp, end = 24.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
    ) {
      // 1) Shared top-center winged-anchor-star emblem
      DefenseWingAnchorEmblem(
        modifier = Modifier.size(width = 66.dp, height = 48.dp),
        testTag = "menu_defense_emblem",
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 2) Large bold white gothic brand title ("국방모바일보안")
      Text(
        text = stringResource(R.string.menu_brand_title),
        style =
          MaterialTheme.typography.headlineSmall.copy(
            fontSize = 22.sp,
            lineHeight = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = (-0.4).sp,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("menu_brand_title"),
      )

      Spacer(modifier = Modifier.height(6.dp))

      // 3) Medium bold white subtitle ("고객지원센터")
      Text(
        text = stringResource(R.string.support_center_title),
        style =
          MaterialTheme.typography.titleSmall.copy(
            fontSize = 13.5.sp,
            lineHeight = 18.sp,
            fontWeight = FontWeight.Bold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("support_center_title"),
      )

      Spacer(modifier = Modifier.height(2.dp))

      // 4) Bold white static phone number (intentionally non-dialing per mockup requirements)
      Text(
        text = stringResource(R.string.support_center_phone),
        style =
          MaterialTheme.typography.titleSmall.copy(
            fontSize = 14.5.sp,
            lineHeight = 19.sp,
            fontWeight = FontWeight.ExtraBold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        modifier = Modifier.testTag("support_center_phone"),
      )
    }
  }
}

@Composable
private fun MenuSectionHeader(title: String) {
  val colors = CameraGuardTheme.colors

  Box(
    modifier =
      Modifier.fillMaxWidth()
        .padding(start = 26.dp, end = 20.dp, top = 14.dp, bottom = 12.dp),
    contentAlignment = Alignment.CenterStart,
  ) {
    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = colors.menuTextSection,
    )
  }
}

@Composable
private fun MenuNavigationRow(
  icon: ImageVector,
  title: String,
  onClick: () -> Unit,
  testTag: String,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .defaultMinSize(minHeight = 48.dp)
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colors.menuTextPrimary),
          role = Role.Button,
          onClick = onClick,
        )
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = colors.menuIconTint,
      modifier = Modifier.size(18.dp),
    )

    Spacer(modifier = Modifier.width(12.dp))

    Text(
      text = title,
      style = MaterialTheme.typography.titleMedium,
      color = colors.menuTextPrimary,
      modifier = Modifier.weight(1f),
    )

    Icon(
      imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
      contentDescription = stringResource(R.string.cd_arrow_next),
      tint = colors.menuChevronTint,
      modifier = Modifier.size(22.dp),
    )
  }
}

@Composable
private fun MenuToggleRow(
  icon: ImageVector,
  title: String,
  subtitle: String,
  checked: Boolean,
  onToggle: () -> Unit,
  rowTestTag: String,
  switchTestTag: String,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .defaultMinSize(minHeight = 64.dp)
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colors.menuTextPrimary),
          onClick = onToggle,
        )
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .testTag(rowTestTag),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = icon,
      contentDescription = null,
      tint = colors.menuIconTint,
      modifier = Modifier.size(18.dp),
    )

    Spacer(modifier = Modifier.width(12.dp))

    Column(
      modifier = Modifier.weight(1f).padding(end = 12.dp),
      verticalArrangement = Arrangement.Center,
    ) {
      Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = colors.menuTextPrimary,
      )
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = subtitle,
        style = MaterialTheme.typography.bodySmall,
        color = colors.menuTextSecondary,
      )
    }

    MintToggleSwitch(
      checked = checked,
      onCheckedChange = { onToggle() },
      testTag = switchTestTag,
    )
  }
}

@Composable
private fun SystemInfoRow(
  label: String,
  value: String,
  testTag: String,
) {
  val colors = CameraGuardTheme.colors

  Row(
    modifier =
      Modifier.fillMaxWidth()
        .defaultMinSize(minHeight = 46.dp)
        .padding(horizontal = 20.dp, vertical = 12.dp)
        .testTag(testTag),
    verticalAlignment = Alignment.CenterVertically,
  ) {
    Icon(
      imageVector = Icons.Rounded.Check,
      contentDescription = null,
      tint = colors.menuIconTint,
      modifier = Modifier.size(14.dp),
    )

    Spacer(modifier = Modifier.width(10.dp))

    Text(
      text = label,
      style = MaterialTheme.typography.titleMedium,
      color = colors.menuTextPrimary,
      modifier = Modifier.weight(1f),
    )

    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = colors.menuTextValue,
      textAlign = TextAlign.End,
    )
  }
}

@Composable
private fun MenuThinDivider() {
  val colors = CameraGuardTheme.colors
  HorizontalDivider(
    modifier = Modifier.padding(horizontal = 12.dp),
    thickness = 0.8.dp,
    color = colors.menuRowDivider,
  )
}

@Composable
private fun BottomDeleteAppButton(
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val colors = CameraGuardTheme.colors
  val interactionSource = remember { MutableInteractionSource() }

  Surface(
    shape = RoundedCornerShape(3.dp),
    color = colors.bottomCoralRed,
    modifier =
      modifier
        .height(48.dp)
        .clip(RoundedCornerShape(3.dp))
        .clickable(
          interactionSource = interactionSource,
          indication = ripple(color = colors.menuTextPrimary),
          role = Role.Button,
          onClick = onClick,
        )
        .testTag("bottom_delete_app_button"),
  ) {
    Box(
      modifier = Modifier.fillMaxSize(),
      contentAlignment = Alignment.Center,
    ) {
      Text(
        text = stringResource(R.string.action_uninstall_app),
        style =
          MaterialTheme.typography.titleMedium.copy(
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
          ),
        color = colors.menuTextPrimary,
        textAlign = TextAlign.Center,
        maxLines = 1,
      )
    }
  }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun MenuSettingsScreenPreview() {
  CameraGuardAppTheme {
    MenuSettingsScreen(
      isBlocked = true,
      manufacturer = "samsung",
      modelName = "SM-G991N",
      osVersion = "15",
      voiceAlertEnabled = true,
      vibrationAlertEnabled = true,
      onToggleVoiceAlert = {},
      onToggleVibrationAlert = {},
      onClose = {},
    )
  }
}
