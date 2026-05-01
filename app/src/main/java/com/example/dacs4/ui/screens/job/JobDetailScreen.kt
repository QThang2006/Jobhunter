package com.example.dacs4.ui.screens.job

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.dacs4.ui.screens.job.JobDetailUiState
import com.example.dacs4.ui.screens.job.JobDetailViewModel
import java.text.NumberFormat
import java.util.Locale
import android.widget.TextView
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JobDetailScreen(
    jobId: String,
    onBack: () -> Unit,
    viewModel: JobDetailViewModel = hiltViewModel()
) {
    val BgColor = Color(0xFF1E2022)
    val CardBg = Color(0xFF252829)
    val AccentColor = Color(0xFF58AAAB)
    val imageBaseUrl = "http://10.0.2.2:8080/storage/company/"

    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(jobId) {
        viewModel.fetchJobDetail(jobId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Chi tiết công việc", color = Color.White, fontSize = 18.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = CardBg)
            )
        },
        containerColor = BgColor
    ) { padding ->
        when (val state = uiState) {
            is JobDetailUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = AccentColor)
                }
            }
            is JobDetailUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = Color.White)
                }
            }
            is JobDetailUiState.Success -> {
                val job = state.job
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Header: Logo & Tên
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        AsyncImage(
                            model = if (job.company?.logo != null) "$imageBaseUrl${job.company.logo}" else null,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)).background(CardBg)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(job.name, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(job.company?.name ?: "", color = Color.Gray, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Info Cards
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        InfoBox(label = "Mức lương", value = formatSalary(job.salary), modifier = Modifier.weight(1f))
                        InfoBox(label = "Địa điểm", value = job.location, modifier = Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description
                    Text("Mô tả công việc", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    AndroidView(
                        factory = { context ->
                            TextView(context).apply {
                                setTextColor(android.graphics.Color.LTGRAY)
                                textSize = 14f
                            }
                        },
                        update = { textView ->
                            textView.text = HtmlCompat.fromHtml(
                                job.description ?: "Không có mô tả chi tiết cho công việc này.",
                                HtmlCompat.FROM_HTML_MODE_COMPACT
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Button Apply
                    Button(
                        onClick = { /* Handle Apply */ },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentColor),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ứng tuyển ngay", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun InfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(Color(0xFF2A2D2F), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Text(label, color = Color.Gray, fontSize = 12.sp)
        Text(value, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold, maxLines = 1)
    }
}

private fun formatSalary(salary: Double): String {
    return if (salary >= 1_000_000) {
        "${salary / 1_000_000}M VNĐ"
    } else {
        NumberFormat.getInstance(Locale("vi","VN")).format(salary) + " VNĐ"
    }
}
