package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation.NavController
import com.example.ui.BillViewModel
import com.example.ui.UiState
import com.example.data.*
import com.example.ui.components.AmbientBackground
import com.example.ui.components.GlassCard
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BillDetailScreen(viewModel: BillViewModel, navController: NavController) {
    val extractionState by viewModel.extractionState.collectAsState()
    val context = LocalContext.current
    
    if (extractionState !is UiState.Success) {
        navController.popBackStack()
        return
    }
    
    val billData = (extractionState as UiState.Success).data
    val allHomes by viewModel.allHomes.collectAsState()
    var selectedHome by remember { mutableStateOf<Home?>(null) }
    var shopName by remember { mutableStateOf(billData.bill.name) }
    var category by remember { mutableStateOf(billData.bill.category) }
    val items = remember { mutableStateListOf(*billData.items.toTypedArray()) }
    var splitCount by remember { mutableIntStateOf(1) }

    val total = items.sumOf { it.price * it.quantity }
    val splitAmount = if (splitCount > 0) total / splitCount else total

    Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        AmbientBackground()

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Review Bill", fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    },
                    actions = {
                        IconButton(onClick = { /* Share */ }) {
                            Icon(Icons.Default.Share, "Share")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
                )
            },
            bottomBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).navigationBarsPadding(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Total Amount", style = MaterialTheme.typography.labelSmall)
                            Text("$${String.format("%.2f", total)}", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                        }
                        Button(
                            onClick = {
                                val bill = billData.bill.copy(
                                    name = shopName,
                                    totalAmount = total,
                                    category = category,
                                    splitCount = splitCount,
                                    homeId = selectedHome?.id,
                                    isPersonal = selectedHome == null
                                )
                                viewModel.saveBill(bill, items)
                                navController.navigate("dashboard") {
                                    popUpTo("dashboard") { inclusive = true }
                                }
                            },
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text("Confirm & Save", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    GlassCard {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = shopName,
                                onValueChange = { shopName = it },
                                label = { Text("Shop Name") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                            )

                            // Home Selection
                            var expanded by remember { mutableStateOf(false) }
                            ExposedDropdownMenuBox(
                                expanded = expanded,
                                onExpandedChange = { expanded = it }
                            ) {
                                OutlinedTextField(
                                    value = selectedHome?.name ?: "Personal (No Home)",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text("Assign to Household") },
                                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
                                )
                                ExposedDropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false }
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Personal (No Home)") },
                                        onClick = {
                                            selectedHome = null
                                            expanded = false
                                        }
                                    )
                                    allHomes.forEach { home ->
                                        DropdownMenuItem(
                                            text = { Text(home.name) },
                                            onClick = {
                                                selectedHome = home
                                                expanded = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                item {
                    Text("Items Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                }

                itemsIndexed(items) { index, item ->
                    GlassCard {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Column(Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = item.name,
                                    onValueChange = { items[index] = item.copy(name = it) },
                                    label = { Text("Item Name") },
                                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedTextField(
                                        value = item.price.toString(),
                                        onValueChange = { items[index] = item.copy(price = it.toDoubleOrNull() ?: 0.0) },
                                        label = { Text("Price") },
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(1f)
                                    )
                                    OutlinedTextField(
                                        value = item.quantity.toString(),
                                        onValueChange = { items[index] = item.copy(quantity = it.toIntOrNull() ?: 1) },
                                        label = { Text("Qty") },
                                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                                        modifier = Modifier.weight(0.6f)
                                    )
                                }
                            }
                            IconButton(onClick = { items.removeAt(index) }) {
                                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }

                item {
                    Text("Split Options", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                    Spacer(Modifier.height(8.dp))
                    GlassCard {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("Split between", modifier = Modifier.weight(1f))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { if (splitCount > 1) splitCount-- }) { Text("-", style = MaterialTheme.typography.headlineMedium) }
                                    Text(splitCount.toString(), modifier = Modifier.padding(horizontal = 12.dp), style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                    IconButton(onClick = { splitCount++ }) { Text("+", style = MaterialTheme.typography.headlineMedium) }
                                }
                            }
                            Spacer(Modifier.height(16.dp))
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(Modifier.padding(16.dp)) {
                                    Text("Per Person", style = MaterialTheme.typography.labelSmall)
                                    Text("$${String.format("%.2f", splitAmount)}", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(Modifier.height(80.dp)) // Buffer for bottom bar
                }
            }
        }
    }
}
