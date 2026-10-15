package com.zenith26.fest

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.zenith26.fest.ui.admin.AdminDashboardScreen
import com.zenith26.fest.ui.admin.AdminDashboardViewModel
import com.zenith26.fest.ui.navigation.Screen
import com.zenith26.fest.ui.portal.StudentPortalScreen
import com.zenith26.fest.ui.portal.StudentPortalViewModel
import com.zenith26.fest.ui.theme.Zenith26Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Zenith26Theme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = Screen.StudentPortal.route
                ) {
                    composable(Screen.StudentPortal.route) {
                        val portalViewModel: StudentPortalViewModel = viewModel()
                        StudentPortalScreen(
                            viewModel = portalViewModel,
                            onNavigateToAdmin = {
                                navController.navigate(Screen.AdminDashboard.route)
                            }
                        )
                    }

                    composable(Screen.AdminDashboard.route) {
                        val adminViewModel: AdminDashboardViewModel = viewModel()
                        AdminDashboardScreen(
                            viewModel = adminViewModel,
                            onNavigateToPortal = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
