package com.adaghero.mytasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adaghero.mytasks.viewmodel.TaskViewModel


@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.tasks.collectAsState()

    var newTask by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newTask,
                onValueChange = { newTask = it },
                label = { Text("New task") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (newTask.isNotBlank()) {
                    taskViewModel.addTask(newTask)
                    newTask = ""
                }
            }) {
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(tasks, key = { it.id }) { task ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    Checkbox(
                        checked = task.isDone,
                        onCheckedChange = {
                            taskViewModel.toggleTaskDone(task.id, it)
                        }
                    )
                    Text(task.title, modifier = Modifier.weight(1f))
                    IconButton(onClick = {
                        taskViewModel.deleteTask(task.id)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
        }
    }
}
