package br.com.icvilar.marketflow.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.icvilar.marketflow.presentation.exchange_list.ExchangeListScreen
import br.com.icvilar.marketflow.presentation.exchange_list.ExchangeListViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = "exchange_list"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable("exchange_list") {
            val viewModel: ExchangeListViewModel = hiltViewModel()
            ExchangeListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { exchangeId ->
                    navController.navigate("exchange_detail/$exchangeId")
                }
            )
        }
        
        composable("exchange_detail/{exchangeId}") { backStackEntry ->
            val exchangeId = backStackEntry.arguments?.getString("exchangeId") ?: ""
            // Placeholder detail screen
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Detail Placeholder for Exchange ID: $exchangeId")
            }
        }
    }
}
