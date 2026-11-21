package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.CameraGuardTheme

/**
 * Hidden dark-theme modal dialog triggered by triple-tapping the "삭제" button within 1.5 seconds.
 * Allows editing install date-time and dummy system info values displayed across Main & Menu screens.
 */
@Composable
fun HiddenInfoEditDialog(
  initialInstallDateTime: String,
  initialManufacturer: String,
  initialModelName: String,
  initialOsVersion: String,
  onDismiss: () -> Unit,
  onSave: (
    installDateTime: String,
    manufacturer: String,
    modelName: String,
    osVersion: String,
  ) -> Unit,
) {
  val colors = CameraGuardTheme.colors

  var installDateTime by remember(initialInstallDateTime) { mutableStateOf(initialInstallDateTime) }
  var manufacturer by remember(initialManufacturer) { mutableStateOf(initialManufacturer) }
  var modelName by remember(initialModelName) { mutableStateOf(initialModelName) }
  var osVersion by remember(initialOsVersion) { mutableStateOf(initialOsVersion) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false),
  ) {
    Surface(
      shape = RoundedCornerShape(16.dp),
      color = colors.menuBackground,
      border = BorderStroke(1.dp, colors.menuRowDivider),
      tonalElevation = 6.dp,
      modifier =
        Modifier.fillMaxWidth()
          .padding(horizontal = 24.dp)
          .widthIn(max = 400.dp)
          .testTag("hidden_edit_dialog"),
    ) {
      Column(
        modifier =
          Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
      ) {
        Text(
          text = stringResource(R.string.edit_dialog_title),
          style =
            MaterialTheme.typography.titleMedium.copy(
              fontSize = 18.sp,
              fontWeight = FontWeight.Bold,
            ),
          color = colors.menuTextPrimary,
        )

        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(thickness = 1.dp, color = colors.menuRowDivider)
        Spacer(modifier = Modifier.height(14.dp))

        DarkEditField(
          label = stringResource(R.string.edit_field_install_datetime),
          value = installDateTime,
          onValueChange = { installDateTime = it },
          imeAction = ImeAction.Next,
          testTag = "edit_install_datetime_input",
        )

        Spacer(modifier = Modifier.height(12.dp))

        DarkEditField(
          label = stringResource(R.string.sys_info_manufacturer_label),
          value = manufacturer,
          onValueChange = { manufacturer = it },
          imeAction = ImeAction.Next,
          testTag = "edit_manufacturer_input",
        )

        Spacer(modifier = Modifier.height(12.dp))

        DarkEditField(
          label = stringResource(R.string.sys_info_model_label),
          value = modelName,
          onValueChange = { modelName = it },
          imeAction = ImeAction.Next,
          testTag = "edit_model_name_input",
        )

        Spacer(modifier = Modifier.height(12.dp))

        DarkEditField(
          label = stringResource(R.string.sys_info_os_label),
          value = osVersion,
          onValueChange = { osVersion = it },
          imeAction = ImeAction.Done,
          testTag = "edit_os_version_input",
        )

        Spacer(modifier = Modifier.height(22.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
          OutlinedButton(
            onClick = onDismiss,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, colors.menuRowDivider),
            colors =
              ButtonDefaults.outlinedButtonColors(
                contentColor = colors.menuTextSecondary,
              ),
            modifier =
              Modifier.weight(1f)
                .height(48.dp)
                .testTag("edit_dialog_cancel_button"),
          ) {
            Text(
              text = stringResource(R.string.action_cancel),
              style = MaterialTheme.typography.labelLarge,
            )
          }

          Button(
            onClick = { onSave(installDateTime, manufacturer, modelName, osVersion) },
            shape = RoundedCornerShape(8.dp),
            colors =
              ButtonDefaults.buttonColors(
                containerColor = colors.headerCrimson,
                contentColor = colors.menuTextPrimary,
              ),
            modifier =
              Modifier.weight(1f)
                .height(48.dp)
                .testTag("edit_dialog_save_button"),
          ) {
            Text(
              text = stringResource(R.string.action_save),
              style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            )
          }
        }
      }
    }
  }
}

@Composable
private fun DarkEditField(
  label: String,
  value: String,
  onValueChange: (String) -> Unit,
  imeAction: ImeAction,
  testTag: String,
) {
  val colors = CameraGuardTheme.colors

  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = {
      Text(
        text = label,
        style = MaterialTheme.typography.bodySmall,
      )
    },
    singleLine = true,
    keyboardOptions = KeyboardOptions(imeAction = imeAction),
    shape = RoundedCornerShape(8.dp),
    colors =
      OutlinedTextFieldDefaults.colors(
        focusedTextColor = colors.menuTextPrimary,
        unfocusedTextColor = colors.menuTextPrimary,
        cursorColor = colors.mintToggleActive,
        focusedBorderColor = colors.mintToggleActive,
        unfocusedBorderColor = colors.menuRowDivider,
        focusedLabelColor = colors.mintToggleActive,
        unfocusedLabelColor = colors.menuTextSecondary,
        focusedContainerColor = colors.mainBackground,
        unfocusedContainerColor = colors.mainBackground,
      ),
    modifier = Modifier.fillMaxWidth().testTag(testTag),
  )
}
