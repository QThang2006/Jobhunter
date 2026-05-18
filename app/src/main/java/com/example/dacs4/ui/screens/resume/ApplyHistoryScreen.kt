package com.example.dacs4.ui.screens.resume

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.dacs4.data.model.response.ResumeResponse
import com.example.dacs4.ui.theme.AppColors
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyHistoryScreen(
    onBack: () -> Unit,
    viewModel: ApplyHistoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lịch sử ứng tuyển", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AppColors.BgPrimary,
                    titleContentColor = AppColors.TextPrimary,
                    navigationIconContentColor = AppColors.TextPrimary
                )
            )
        },
        containerColor = AppColors.BgPrimary
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            val state = uiState
            
            // ─── Case: Loading (first time, no data) ─────────────────────────
            if (state.isLoading && state.resumes.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center), 
                    color = AppColors.AccentBlue,
                    strokeWidth = 2.dp
                )
            } 
            // ─── Case: Error (and no data) ──────────────────────────────────
            else if (state.error != null && state.resumes.isEmpty()) {
                Text(
                    text = state.error,
                    color = AppColors.Error,
                    modifier = Modifier.align(Alignment.Center).padding(16.dp),
                    textAlign = TextAlign.Center
                )
            }
            // ─── Case: Data (or loading with existing data) ──────────────────
            else if (state.resumes.isEmpty()) {
                EmptyHistoryState()
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(top = 16.dp, bottom = 110.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(state.resumes, key = { it.id }) { resume ->
                        ResumeCard(resume)
                    }
                }
            }
        }
    }
}

@Composable
private fun ResumeCard(resume: ResumeResponse) {
    val context = LocalContext.current
    val (statusLabel, textColor, bgColor) = getStatusConfig(resume.status)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(0.5.dp, AppColors.Border, RoundedCornerShape(14.dp))
            .clip(RoundedCornerShape(14.dp))
            .background(AppColors.BgPrimary)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Placeholder Logo (Backend hiện không trả về logo cty cho lịch sử nộp)
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(AppColors.BgSurface),
                contentAlignment = Alignment.Center
            ) {
                val initName = resume.companyName?.firstOrNull()?.uppercase() ?: resume.job?.name?.firstOrNull()?.uppercase() ?: "J"
                Text(initName, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AppColors.TextSecondary)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = resume.job?.name ?: "Vị trí không xác định",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.TextPrimary
                )
                if (!resume.companyName.isNullOrBlank()) {
                    Text(
                        text = resume.companyName,
                        fontSize = 13.sp,
                        color = AppColors.TextSecondary
                    )
                }
                Text(
                    text = formatDate(resume.createdAt),
                    fontSize = 12.sp,
                    color = AppColors.TextHint,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 0.5.dp, color = AppColors.Border)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(bgColor)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(statusLabel, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = textColor)
            }

            TextButton(
                onClick = {
                    // Sửa ở đây: Sử dụng trực tiếp URL từ server
                    val url = resume.url
                    if (!url.isNullOrBlank()) {
                        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                    }
                },
                contentPadding = PaddingValues(horizontal = 8.dp)
            ) {
                Text("Xem CV ›", color = AppColors.AccentBlue, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun EmptyHistoryState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("📭", fontSize = 48.sp)
        Spacer(Modifier.height(16.dp))
        Text("Bạn chưa ứng tuyển việc làm nào", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(Modifier.height(8.dp))
        Text("Khám phá việc làm phù hợp với bạn", color = AppColors.TextSecondary, fontSize = 14.sp)
    }
}

private fun getStatusConfig(status: String): Triple<String, Color, Color> {
    return when (status.uppercase()) {
        "PENDING"   -> Triple("CHỜ XÉT DUYỆT", AppColors.Warning, Color(0xFFFEF3C7))
        "REVIEWING" -> Triple("ĐANG XEM XÉT", AppColors.AccentBlue, AppColors.AccentBlueLight)
        "APPROVED"  -> Triple("ĐÃ DUYỆT", AppColors.Success, Color(0xFFDCFCE7))
        "REJECTED"  -> Triple("KHÔNG PHÙ HỢP", AppColors.Error, Color(0xFFFEE2E2))
        else        -> Triple(status, AppColors.TextSecondary, AppColors.Border)
    }
}

private fun formatDate(isoString: String?): String {
    if (isoString.isNullOrBlank()) return ""
    return try {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val instant = java.time.Instant.parse(isoString)
            val formatter = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")
                .withZone(java.time.ZoneId.systemDefault())
            formatter.format(instant)
        } else {
            // Fallback for older devices (though unlikely to be hit in this app)
            val formatIn = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
            val date = formatIn.parse(isoString.substring(0, 19)) ?: return isoString
            val formatOut = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            formatOut.format(date)
        }
    } catch (e: Exception) {
        isoString
    }
}
