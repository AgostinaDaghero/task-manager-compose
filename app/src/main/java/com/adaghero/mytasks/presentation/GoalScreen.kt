package com.adaghero.mytasks.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.adaghero.mytasks.model.Goal
import com.adaghero.mytasks.model.Subtask
import androidx.compose.foundation.shape.RoundedCornerShape
import com.adaghero.mytasks.viewmodel.GoalViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.ui.text.input.ImeAction



@Composable
fun GoalScreen(viewModel: GoalViewModel) {
    val goals by viewModel.goals.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "My Goals",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Goal title") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addGoal(Goal(title = title, description = description))
                    title = ""
                    description = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Add Goal", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(goals) { goal ->
                GoalItem(goal, viewModel)
            }
        }
    }
}

@Composable
fun GoalItem(goal: Goal, viewModel: GoalViewModel) {
    var expanded by remember { mutableStateOf(false) }

    val rotation by animateFloatAsState(
        targetValue = if (expanded) 180f else 0f,
        label = "ArrowRotation"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            //Goal header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.weight(1f)) {
                    Text(text = goal.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = goal.description, style = MaterialTheme.typography.bodyMedium)
                }
                Row {
                    IconButton(onClick = { expanded = !expanded }) {
                        Icon(
                            imageVector = Icons.Default.ExpandMore,
                            contentDescription = "Expand subtasks",
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                    IconButton(onClick = { viewModel.deleteGoal(goal.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Goal")
                    }
                }
            }
            //Progress
            LinearProgressIndicator(
                progress = goal.progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFF3B82F6)
            )
            Text("${(goal.progress * 100).toInt()}% completed")

            //Subtasks (expandable)
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(modifier = Modifier.height(8.dp))

                    //List of subtasks
                    goal.subtasks.forEach { sub ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = sub.completed,
                                onCheckedChange = { viewModel.toggleSubtask(goal.id, sub.id) },
                            )
                            Text(
                                sub.title,
                                style = if (sub.completed) {
                                    MaterialTheme.typography.bodyMedium.copy(
                                        color = Color.Gray,
                                        textDecoration = TextDecoration.LineThrough
                                    )
                                } else {
                                    MaterialTheme.typography.bodyMedium
                                }
                            )
                            Spacer(Modifier.weight(1f))
                            IconButton(onClick = { viewModel.deleteSubtask(goal.id, sub.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete Subtask")
                            }
                        }
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    var subtaskTitle by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = subtaskTitle,
                        onValueChange = { subtaskTitle = it },
                        label = { Text("New subtask") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
                        keyboardActions = KeyboardActions(
                            onDone = {
                                if (subtaskTitle.isNotBlank()) {
                                    viewModel.addSubtask(goal.id, Subtask(title = subtaskTitle))
                                    subtaskTitle = ""
                                }
                            }
                        )
                    )
                    Spacer(Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (subtaskTitle.isNotBlank()) {
                                viewModel.addSubtask(goal.id, Subtask(title = subtaskTitle))
                                subtaskTitle = ""
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Text("+")
                    }
                }
            }
        }
    }
}