package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.ui.BillViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomesScreen(viewModel: BillViewModel, navController: NavController) {
    val homes by viewModel.allHomes.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var homeName by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Homes") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showDialog = true }) {
                        Icon(Icons.Default.Add, "Add Home")
                    }
                }
            )
        }
    ) { padding ->
        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Create Home") },
                text = {
                    OutlinedTextField(
                        value = homeName,
                        onValueChange = { homeName = it },
                        label = { Text("Home Name") }
                    )
                },
                confirmButton = {
                    Button(onClick = {
                        viewModel.createHome(homeName)
                        showDialog = false
                    }) { Text("Create") }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                }
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(homes) { home ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    ListItem(
                        headlineContent = { Text(home.name) },
                        supportingContent = { Text("Collaborative spending history") }
                    )
                }
            }
        }
    }
}
