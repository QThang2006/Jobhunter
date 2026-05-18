package com.example.dacs4.ui.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordSheet(
    onDismiss: () -> Unit,
    onSave: (old: String, new: String) -> Unit
) {
    var oldPass by remember { mutableStateOf("") }
    var oldPassVisible by remember { mutableStateOf(false) }

    var newPass by remember { mutableStateOf("") }
    var newPassVisible by remember { mutableStateOf(false) }

    var confirmPass by remember { mutableStateOf("") }
    var confirmPassVisible by remember { mutableStateOf(false) }

    val isNewPassValid = newPass.length >= 6
    val isConfirmValid = newPass == confirmPass
    val canSubmit = oldPass.isNotBlank() && newPass.isNotBlank() && isNewPassValid && isConfirmValid

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.BgPrimary,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Đổi mật khẩu", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)

            PasswordField(
                label = "Mật khẩu cũ",
                value = oldPass,
                onValueChange = { oldPass = it },
                isVisible = oldPassVisible,
                onToggleVisibility = { oldPassVisible = !oldPassVisible }
            )

            PasswordField(
                label = "Mật khẩu mới (ít nhất 6 ký tự)",
                value = newPass,
                onValueChange = { newPass = it },
                isVisible = newPassVisible,
                onToggleVisibility = { newPassVisible = !newPassVisible }
            )

            PasswordField(
                label = "Xác nhận mật khẩu mới",
                value = confirmPass,
                onValueChange = { confirmPass = it },
                isVisible = confirmPassVisible,
                onToggleVisibility = { confirmPassVisible = !confirmPassVisible },
                isError = confirmPass.isNotEmpty() && !isConfirmValid
            )
            if (confirmPass.isNotEmpty() && !isConfirmValid) {
                Text("Mật khẩu xác nhận không khớp.", color = AppColors.Error, fontSize = 12.sp)
            }

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = { onSave(oldPass, newPass) },
                enabled = canSubmit,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.AccentBlue,
                    disabledContainerColor = AppColors.BgSurface
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text(
                    "ĐỔI MẬT KHẨU",
                    fontWeight = FontWeight.Bold,
                    color = if (canSubmit) Color.White else AppColors.TextHint
                )
            }
        }
    }
}

@Composable
private fun PasswordField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isVisible: Boolean,
    onToggleVisibility: () -> Unit,
    isError: Boolean = false
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        isError = isError,
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (isVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
            IconButton(onClick = onToggleVisibility) {
                Icon(imageVector = image, contentDescription = null, tint = AppColors.TextSecondary)
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.AccentBlue,
            unfocusedBorderColor = AppColors.Border,
            errorBorderColor = AppColors.Error,
            focusedLabelColor = AppColors.AccentBlue,
            errorLabelColor = AppColors.Error
        )
    )
}
