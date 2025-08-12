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
import com.adaghero.mytasks.model.Priority
import androidx.compose.material.icons.filled.ArrowDropDown


@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.tasks.collectAsState()

    var newTask by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = newTask,
                onValueChange = { newTask = it },
                label = { Text("Nueva tarea") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Priority selector
            Box {
                Button(onClick = { menuExpanded = true }) {
                    Text(text = selectedPriority.name.lowercase().replaceFirstChar { it.uppercase() })
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select priority")
                }
                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(text = { Text("High") }, onClick = {
                        selectedPriority = Priority.HIGH
                        menuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Medium") }, onClick = {
                        selectedPriority = Priority.MEDIUM
                        menuExpanded = false
                    })
                    DropdownMenuItem(text = { Text("Low") }, onClick = {
                        selectedPriority = Priority.LOW
                        menuExpanded = false
                    })
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (newTask.isNotBlank()) {
                    taskViewModel.addTask(newTask.trim(), selectedPriority)
                    newTask = ""
                }
            }) {
                Text("Agregar")
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
                        onCheckedChange = { checked ->
                            taskViewModel.toggleTaskDone(task.id, checked)
                        }
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(task.title)
                        // Show priority in small
                        Text(
                            text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                    IconButton(onClick = {
                        taskViewModel.deleteTask(task.id)
                    }) {
                        Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                    }
                }
            }
        }
    }
}
