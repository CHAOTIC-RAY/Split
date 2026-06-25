package com.example.ui.screens

import com.example.ui.components.AmbientBackground
import com.example.ui.components.GlassCard
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.R
import com.example.ui.BillViewModel
import com.example.ui.UiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: BillViewModel, navController: NavController) {
    val bills by viewModel.allBills.collectAsState()
    val modelDownloadState by viewModel.modelDownloadState.collectAsState()
    val bankSenders by viewModel.bankSenders.collectAsState()
    
    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        // Ambient background blobs
        AmbientBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Spilt", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                            Text("The Grand Apt", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
                        }
                    },
                    actions = {
                        IconButton(onClick = { navController.navigate("homes") }) {
                            Icon(Icons.Default.Home, "Homes")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { navController.navigate("scanner") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.size(64.dp)
                ) {
                    Icon(Icons.Default.Add, null, modifier = Modifier.size(32.dp))
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    // Model Download Card if not ready
                    if (modelDownloadState !is UiState.Success) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(24.dp))
                                .background(MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f))
                                .border(1.dp, MaterialTheme.colorScheme.error.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
                                .padding(16.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Local AI Model Required", fontWeight = FontWeight.Bold)
                                    Text("Download the model to enable local processing.", style = MaterialTheme.typography.labelSmall)
                                }
                                Button(
                                    onClick = { viewModel.downloadModel() },
                                    enabled = modelDownloadState !is UiState.Loading
                                ) {
                                    if (modelDownloadState is UiState.Loading) {
                                        CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                                    } else {
                                        Text("Download")
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    // Glass Spend Card
                    GlassCard(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Total Spending", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f))
                                Surface(
                                    color = Color(0xFFE8F5E9).copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("+2.4%", modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = Color(0xFF2E7D32), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text("$${String.format("%.2f", bills.sumOf { it.totalAmount })}", style = MaterialTheme.typography.displayMedium, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onBackground)
                            
                            Spacer(Modifier.height(16.dp))
                            // Simple Progress Bar
                            Row(modifier = Modifier.height(8.dp).fillMaxWidth().clip(CircleShape)) {
                                Box(Modifier.weight(0.6f).fillMaxHeight().background(MaterialTheme.colorScheme.primary))
                                Box(Modifier.weight(0.25f).fillMaxHeight().background(MaterialTheme.colorScheme.secondary))
                                Box(Modifier.weight(0.15f).fillMaxHeight().background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)))
                            }
                        }
                    }
                }

                item {
                    Row(modifier = Modifier.height(110.dp).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        GlassActionButton(
                            modifier = Modifier.weight(1f),
                            icon = "📷",
                            title = "Scan Bill",
                            subtitle = "Local Extraction",
                            color = Color(0xFFD0E4FF).copy(alpha = 0.6f),
                            onClick = { navController.navigate("scanner") }
                        )
                        GlassActionButton(
                            modifier = Modifier.weight(1f),
                            icon = "🏦",
                            title = "Bank SMS",
                            subtitle = "${bankSenders.size} Configured",
                            color = MaterialTheme.colorScheme.surface,
                            onClick = { navController.navigate("bank_settings") }
                        )
                    }
                }

                item {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 4.dp)) {
                        Text("Recent Activity", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                        TextButton(onClick = { navController.navigate("history") }) {
                            Text("View all", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                items(bills.take(5)) { bill ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = { Text(bill.name, fontWeight = FontWeight.Bold) },
                            supportingContent = { Text("${bill.category} • ${bill.shopName}", style = MaterialTheme.typography.labelSmall) },
                            trailingContent = { 
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("$${String.format("%.2f", bill.totalAmount)}", fontWeight = FontWeight.Bold)
                                    Text("SPLIT", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                }
                            },
                            leadingContent = {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                    modifier = Modifier.size(44.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(if (bill.category == "Groceries") "🛒" else "⚡", fontSize = 20.sp)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassActionButton(modifier: Modifier, icon: String, title: String, subtitle: String, color: Color, onClick: () -> Unit) {
    val isDark = androidx.compose.foundation.isSystemInDarkTheme()
    val backgroundColor = if (isDark) {
        Color.White.copy(alpha = 0.08f)
    } else {
        color.copy(alpha = 0.4f)
    }
    
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxHeight(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.15f))
    ) {
        Column(modifier = Modifier.padding(16.dp).fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween) {
            Surface(
                shape = CircleShape, 
                color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.White.copy(alpha = 0.6f), 
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(icon, fontSize = 18.sp)
                }
            }
            Column {
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f))
            }
        }
    }
}
