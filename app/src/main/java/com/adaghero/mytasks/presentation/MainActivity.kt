package com.adaghero.mytasks.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.adaghero.mytasks.R
import com.adaghero.mytasks.ui.theme.MyTasksTheme
import com.adaghero.mytasks.viewmodel.HabitViewModel
import com.adaghero.mytasks.viewmodel.TaskViewModel
import com.adaghero.mytasks.viewmodel.GoalViewModel
import com.adaghero.mytasks.viewmodel.GoalViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTasksTheme {
               MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val taskViewModel: TaskViewModel = viewModel()
    val habitViewModel: HabitViewModel = viewModel()
    val goalViewModel: GoalViewModel = viewModel(
        factory = GoalViewModelFactory(context)
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tasks",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("tasks") {
                TaskScreen(taskViewModel = taskViewModel)
            }
            composable("habits") {
                HabitScreen(context = context, viewModel = habitViewModel)
            }
            composable("goals") {
                GoalScreen(viewModel = goalViewModel)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    val items = listOf(
        BottomNavItem("Tasks", "tasks", R.drawable.ic_task),
        BottomNavItem("Habits", "habits", R.drawable.ic_habit),
        BottomNavItem( "Goals", "goals", R.drawable.ic_goal)
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        painter = painterResource(id = item.icon),
                        contentDescription = item.title
                    )
                },
                label = { Text(item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

data class BottomNavItem(val title: String, val route: String, val icon: Int)
