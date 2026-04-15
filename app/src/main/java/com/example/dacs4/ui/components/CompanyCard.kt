package com.example.dacs4.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.dacs4.data.model.response.CompanyResponse

@Composable
fun CompanyCard(
    company: CompanyResponse,
    onClick: () -> Unit
) {
    val CardBg = Color(0xFF2A2D2F)
    val TextSecondary = Color(0xFFAAAAAA)

    Card(
        modifier = Modifier
            .width(150.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val imageBaseUrl = "http://10.0.2.2:8080/storage/company/"
            AsyncImage(
                model = if (company.logo != null) "$imageBaseUrl${company.logo}" else null,
                contentDescription = "${company.name} Logo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF3A3D3F))
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = company.name,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (!company.address.isNullOrBlank()) {
                Text(
                    text = company.address,
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
