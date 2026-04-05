package com.example.foodapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.foodapp.data.DetailViewModel
import com.example.foodapp.data.MealListViewModel
import com.example.foodapp.ui.theme.FoodAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FoodAppTheme {
                FoodApp()
            }
        }
    }
}

@Composable
fun FoodApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val application = context.applicationContext as FoodApplication

    val mealListViewModel: MealListViewModel = viewModel(
        factory = MealListViewModel.provideFactory(application)
    )

    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ) {
        composable<Screen.Home> {
            HomeScreen(
                mealListViewModel = mealListViewModel,
                onMealClick = { mealId ->
                    navController.navigate(Screen.Detail(mealId))
                }
            )
        }

        composable<Screen.Detail> { backStackEntry ->
            val detailRoute: Screen.Detail = backStackEntry.toRoute()

            val detailViewModel: DetailViewModel = viewModel(
                factory = DetailViewModel.provideFactory(detailRoute.mealId, application)
            )

            DetailScreen(
                viewModel = detailViewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
