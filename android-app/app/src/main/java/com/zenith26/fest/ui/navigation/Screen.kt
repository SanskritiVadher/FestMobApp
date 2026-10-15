package com.zenith26.fest.ui.navigation

sealed class Screen(val route: String) {
    object StudentPortal : Screen("portal")
    object AdminDashboard : Screen("admin")
}
