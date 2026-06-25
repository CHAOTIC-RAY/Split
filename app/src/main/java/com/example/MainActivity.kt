package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.ai.LocalLLMService
import com.example.ai.OCRService
import com.example.data.AppDatabase
import com.example.data.BillRepository
import com.example.ui.BillViewModel
import com.example.ui.BillViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val db = AppDatabase.getDatabase(applicationContext)
        val repository = BillRepository(db.billDao())
        val ocrService = OCRService()
        val localLLMService = LocalLLMService(applicationContext)
        
        setContent {
            MyApplicationTheme {
                val viewModel: BillViewModel = viewModel(factory = BillViewModelFactory(repository, ocrService, localLLMService))
                AppNavigation(viewModel)
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: BillViewModel) {
    val navController = rememberNavController()
    
    NavHost(navController = navController, startDestination = "dashboard") {
        composable("dashboard") { DashboardScreen(viewModel, navController) }
        composable("scanner") { ScannerScreen(viewModel, navController) }
        composable("bill_detail") { BillDetailScreen(viewModel, navController) }
        composable("history") { HistoryScreen(viewModel, navController) }
        composable("homes") { HomesScreen(viewModel, navController) }
        composable("bank_settings") { BankSettingsScreen(viewModel, navController) }
    }
}
