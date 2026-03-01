package br.com.icvilar.marketflow.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import br.com.icvilar.marketflow.presentation.exchange_detail.ExchangeDetailScreen
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
            ExchangeDetailScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
