package com.example.dacs4.ui.screens.job

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.dacs4.data.model.response.JobResponse
import com.example.dacs4.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyJobSheet(
    job: JobResponse,
    onDismiss: () -> Unit,
    onSuccess: () -> Unit,
    viewModel: ApplyJobViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context: Context = LocalContext.current

    // Launcher chọn file PDF/DOC
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.onFileSelected(it, context) }
    }

    // Xử lý thành công → đóng sheet
    LaunchedEffect(uiState) {
        if (uiState is ApplyUiState.Success) {
            onSuccess()
            viewModel.reset()
        }
    }

    ModalBottomSheet(
        onDismissRequest = {
            viewModel.reset()
            onDismiss()
        },
        containerColor = AppColors.BgPrimary,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ─── Tiêu đề ──────────────────────────────────────
            Text("Ứng tuyển vào", fontSize = 13.sp, color = AppColors.TextSecondary)
            Text(
                job.name,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = AppColors.TextPrimary
            )
            if (job.company?.name != null) {
                Text("tại ${job.company.name}", fontSize = 13.sp, color = AppColors.TextSecondary)
            }

            HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)

            // ─── Email (disabled) ──────────────────────────────
            val userEmail = viewModel.userEmail
            OutlinedTextField(
                value = userEmail,
                onValueChange = {},
                label = { Text("Email") },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    disabledTextColor = AppColors.TextSecondary,
                    disabledBorderColor = AppColors.Border,
                    disabledLabelColor = AppColors.TextHint
                )
            )

            // ─── File Picker button ────────────────────────────
            val isLoading = uiState is ApplyUiState.Uploading || uiState is ApplyUiState.Submitting
            Button(
                onClick = { if (!isLoading) filePickerLauncher.launch("*/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.BgSurface,
                    contentColor = AppColors.TextSecondary
                ),
                border = ButtonDefaults.outlinedButtonBorder,
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                Icon(Icons.Default.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Chọn file CV  (PDF, DOC, DOCX ≤ 5MB)", fontSize = 13.sp)
            }

            // ─── Tên file đã chọn ─────────────────────────────
            if (uiState is ApplyUiState.FileSelected) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AppColors.Success,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        (uiState as ApplyUiState.FileSelected).fileName,
                        fontSize = 13.sp,
                        color = AppColors.Success,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // ─── Error message ─────────────────────────────────
            if (uiState is ApplyUiState.Error) {
                Text(
                    (uiState as ApplyUiState.Error).message,
                    fontSize = 13.sp,
                    color = AppColors.Error
                )
            }

            // ─── Submit button ─────────────────────────────────
            val canSubmit = uiState is ApplyUiState.FileSelected
            Button(
                onClick = { viewModel.submit(job.id.toLongOrNull() ?: 0L, context) },
                enabled = canSubmit && !isLoading,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.AccentBlue,
                    disabledContainerColor = AppColors.BgSurface
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (uiState is ApplyUiState.Uploading) "Đang upload..." else "Đang nộp đơn...",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Text(
                        "ỨNG TUYỂN NGAY",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (canSubmit) Color.White else AppColors.TextHint
                    )
                }
            }
        }
    }
}
