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
import com.adaghero.mytasks.model.Task
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.graphics.Color



@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val tasks by taskViewModel.tasks.collectAsState()

    var newTask by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var menuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)
    ) {
        //Title
        Text(
            text = "Tasks",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        //Form for adding tasks
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newTask,
                onValueChange = { newTask = it },
                label = { Text("New task") },
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))

            // Priority selector
            Box {
                FilledTonalButton(onClick = { menuExpanded = true }) {
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
                Text("Add")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        //Task list
        LazyColumn {
            items(tasks, key = { it.id }) { task ->
                TaskItem(task = task, onToggle = { checked ->
                    taskViewModel.toggleTaskDone(task.id, checked)
                }, onDelete = {
                    taskViewModel.deleteTask(task.id)
                })
            }
        }
    }
}

@Composable
fun TaskItem(task: Task, onToggle: (Boolean) -> Unit, onDelete: ()-> Unit){
    val bgColor = when (task.priority) {
        Priority.HIGH -> Color(0xFFFF8A80)
        Priority.MEDIUM -> Color(0xFFFFF59D)
        Priority.LOW -> Color(0xFFA5D6A7)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = bgColor)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            Checkbox(
                checked = task.isDone,
                onCheckedChange = onToggle
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(task.title, style = MaterialTheme.typography.titleMedium)
                Text(
                    text = task.priority.name.lowercase().replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall
                )
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}
