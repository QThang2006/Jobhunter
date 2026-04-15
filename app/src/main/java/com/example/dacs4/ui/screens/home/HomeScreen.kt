package com.example.dacs4.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.dacs4.ui.components.CompanyCard
import com.example.dacs4.ui.components.JobCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onLogout: () -> Unit,
    onJobClick: (String) -> Unit = {},
    viewModel: HomeViewModel = hiltViewModel()
) {
    val BgColor = Color(0xFF1E2022)
    val AccentColor = Color(0xFF58AAAB)

    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "JobHunter",
                        color = AccentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF252829)),
                actions = {
                    IconButton(onClick = {
                        viewModel.logout()
                        onLogout()
                    }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        containerColor = BgColor
    ) { padding ->
        when (val state = uiState) {
            is HomeUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = AccentColor)
                }
            }

            is HomeUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "😕 ${state.message}",
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.fetchHomeData() },
                            colors = ButtonDefaults.buttonColors(containerColor = AccentColor)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("Thử lại")
                        }
                    }
                }
            }

            is HomeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                ) {
                    // --- Section: Top Công ty ---
                    item {
                        Text(
                            text = "🏢 Công ty nổi bật",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                        LazyRow {
                            items(state.companies) { company ->
                                CompanyCard(company = company, onClick = {})
                            }
                        }
                    }

                    // --- Section: Danh sách Việc làm ---
                    item {
                        Text(
                            text = "💼 Việc làm mới nhất",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
                        )
                    }

                    items(state.jobs) { job ->
                        JobCard(
                            job = job,
                            onClick = { onJobClick(job.id) }
                        )
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}
