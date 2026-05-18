package com.example.dacs4.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.dacs4.data.model.response.UserDetailResponse
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditSheet(
    user: UserDetailResponse,
    onDismiss: () -> Unit,
    onSave: (name: String, age: Int, gender: String, address: String) -> Unit
) {
    var name    by remember { mutableStateOf(user.name) }
    var age     by remember { mutableStateOf(user.age?.toString() ?: "") }
    var address by remember { mutableStateOf(user.address ?: "") }

    // Gender dropdown
    val genderOptions = listOf("Nam" to "MALE", "Nữ" to "FEMALE", "Khác" to "OTHER")
    val currentGenderLabel = genderOptions.find { it.second == user.gender }?.first ?: "Nam"
    var selectedGender by remember { mutableStateOf(currentGenderLabel) }
    var expanded by remember { mutableStateOf(false) }

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
            Text("Cập nhật thông tin", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppColors.TextPrimary)
            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)

            ProfileTextField(label = "Họ tên *", value = name, onValueChange = { name = it })
            ProfileTextField(label = "Tuổi *", value = age, onValueChange = { age = it }, isNumber = true)

            // Gender Dropdown
            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                OutlinedTextField(
                    value = selectedGender,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Giới tính *") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                    modifier = Modifier.fillMaxWidth().menuAnchor(),
                    shape = RoundedCornerShape(10.dp),
                    colors = profileTextFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.background(AppColors.BgPrimary)
                ) {
                    genderOptions.forEach { (label, _) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = { selectedGender = label; expanded = false }
                        )
                    }
                }
            }

            ProfileTextField(label = "Địa chỉ *", value = address, onValueChange = { address = it })
            ProfileTextField(label = "Email", value = user.email, onValueChange = {}, enabled = false)

            Button(
                onClick = {
                    val genderValue = genderOptions.find { it.first == selectedGender }?.second ?: "MALE"
                    onSave(name, age.toIntOrNull() ?: 0, genderValue, address)
                },
                enabled = name.isNotBlank() && age.isNotBlank(),
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.AccentBlue),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Text("CẬP NHẬT", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

@Composable
private fun ProfileTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isNumber: Boolean = false,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        enabled = enabled,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        keyboardOptions = if (isNumber)
            androidx.compose.foundation.text.KeyboardOptions(keyboardType = androidx.compose.ui.text.input.KeyboardType.Number)
        else
            androidx.compose.foundation.text.KeyboardOptions.Default,
        colors = profileTextFieldColors()
    )
}

@Composable
fun profileTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor   = AppColors.AccentBlue,
    unfocusedBorderColor = AppColors.Border,
    focusedLabelColor    = AppColors.AccentBlue,
    disabledTextColor    = AppColors.TextSecondary,
    disabledBorderColor  = AppColors.Border,
    disabledLabelColor   = AppColors.TextHint
)
