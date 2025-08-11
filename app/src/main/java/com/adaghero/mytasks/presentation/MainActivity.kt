package com.adaghero.mytasks.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adaghero.mytasks.ui.theme.MyTasksTheme
import com.adaghero.mytasks.viewmodel.TaskViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTasksTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Surface (
                        modifier = Modifier.padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ){
                        val taskViewModel: TaskViewModel = viewModel()
                        TaskScreen(taskViewModel = taskViewModel)
                    }
                }
            }
        }
    }
}
