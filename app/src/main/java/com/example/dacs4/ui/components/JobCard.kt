package com.example.dacs4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.dacs4.data.model.response.JobResponse
import java.text.NumberFormat
import java.util.Locale

@Composable
fun JobCard(
    job: JobResponse,
    onClick: () -> Unit
) {
    val CardBg = Color(0xFF2A2D2F)
    val AccentColor = Color(0xFF58AAAB)
    val TextSecondary = Color(0xFFAAAAAA)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo công ty
            val imageBaseUrl = "http://10.0.2.2:8080/storage/company/"
            AsyncImage(
                model = if (job.company?.logo != null) "$imageBaseUrl${job.company.logo}" else null,
                contentDescription = "Company Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF3A3D3F))
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = job.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = job.company?.name ?: "Công ty chưa cập nhật",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tag địa điểm
                    TagChip(text = job.location, color = AccentColor)
                    // Tag mức lương
                    TagChip(
                        text = formatSalary(job.salary),
                        color = Color(0xFF4CAF50)
                    )
                }
            }
        }
    }
}

@Composable
private fun TagChip(text: String, color: Color) {
    Text(
        text = text,
        color = color,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .background(color.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    )
}

private fun formatSalary(salary: Double): String {
    return if (salary >= 1_000_000) {
        val millions = salary / 1_000_000
        "${NumberFormat.getInstance(Locale("vi","VN")).format(millions)}M VNĐ"
    } else {
        NumberFormat.getInstance(Locale("vi","VN")).format(salary) + " VNĐ"
    }
}
