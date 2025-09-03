package com.adaghero.mytasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.adaghero.mytasks.model.Priority
import com.adaghero.mytasks.model.Task
import com.adaghero.mytasks.viewmodel.TaskViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.lazy.items

@Composable
fun TaskScreen(taskViewModel: TaskViewModel = viewModel()) {
    val groupedTasks by taskViewModel.groupedTasks.collectAsState()
    val filter by taskViewModel.filter.collectAsState()

    var newTask by remember { mutableStateOf("") }
    var selectedPriority by remember { mutableStateOf(Priority.MEDIUM) }
    var menuExpanded by remember { mutableStateOf(false) }
    var filterMenuExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        //Filter up
        Box {
            OutlinedButton(onClick = { filterMenuExpanded = true }) {
                Text(filter?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "All")
                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(
                expanded = filterMenuExpanded,
                onDismissRequest = { filterMenuExpanded = false }
            ) {
                DropdownMenuItem(
                    text = { Text("All") },
                    onClick = {
                        taskViewModel.setFilter(null)
                        filterMenuExpanded = false
                    }
                )
                Priority.values().forEach { pr ->
                    DropdownMenuItem(
                        text = { Text(pr.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        onClick = {
                            taskViewModel.setFilter(pr)
                            filterMenuExpanded = false
                        }
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Form card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Tasks", style = MaterialTheme.typography.headlineSmall)

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = newTask,
                    onValueChange = { newTask = it },
                    placeholder = { Text("New task") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                //Priority selector
                Box {
                    OutlinedButton(
                        onClick = { menuExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(selectedPriority.name.lowercase().replaceFirstChar { it.uppercase() })
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        Priority.values().forEach { pr ->
                            DropdownMenuItem(
                                text = { Text(pr.name.lowercase().replaceFirstChar { it.uppercase() }) },
                                onClick = {
                                    selectedPriority = pr
                                    menuExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(8.dp))

                Button(
                    onClick = {
                        if (newTask.isNotBlank()) {
                            taskViewModel.addTask(newTask.trim(), selectedPriority)
                            newTask = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add", color = Color.White)
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        //Task list grouped by priority
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            Priority.values().forEach { pr ->
                val tasksList = groupedTasks[pr] ?: emptyList()
                if (tasksList.isNotEmpty()) {
                    item {
                        SectionHeader(priority = pr)
                    }
                    items(
                        items = tasksList,
                        key = {  task: Task -> task.id }
                    ) { task ->
                        TaskItem(
                            task = task,
                            onToggle = { checked ->
                                taskViewModel.toggleTaskDone(task.id, checked)
                            },
                            onDelete = { taskViewModel.deleteTask(task.id) }
                        )
                    }
                }
            }
        }
    }
}

/**Section heading*/
@Composable
fun SectionHeader(priority: Priority) {
    val color = when (priority) {
        Priority.HIGH -> Color(0xFFE53935)
        Priority.MEDIUM -> Color(0xFFFFA726)
        Priority.LOW -> Color(0xFF43A047)
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "${priority.name.lowercase().replaceFirstChar { it.uppercase() }} Priority",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )
    }
}

/** list item (top-level)*/
@Composable
fun TaskItem(
    task: Task,
    onToggle: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    val priorityColor = when (task.priority) {
        Priority.HIGH -> Color(0xFFE53935)
        Priority.MEDIUM -> Color(0xFFFFA726)
        Priority.LOW -> Color(0xFF43A047)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(if (task.isDone) 0.dp else 2.dp)
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
                Text(
                    task.title, style = MaterialTheme.typography.titleMedium,
                    textDecoration = if (task.isDone) TextDecoration.LineThrough else null
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(priorityColor)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(task.priority.name.lowercase().replaceFirstChar { it.uppercase() })
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                }
            }
        }
    }
}



