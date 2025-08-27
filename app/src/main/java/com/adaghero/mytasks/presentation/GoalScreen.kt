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
import com.adaghero.mytasks.ui.theme.HighPriority
import com.adaghero.mytasks.ui.theme.MediumPriority
import com.adaghero.mytasks.ui.theme.LowPriority
import com.adaghero.mytasks.viewmodel.GoalViewModel

@Composable
fun GoalScreen(viewModel: GoalViewModel) {
    val goals by viewModel.goals.collectAsState()

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {

        Text(
            text = "My Goals",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        Spacer(Modifier.height(24.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Goal title") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )
        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addGoal(Goal(title = title, description = description))
                    title = ""
                    description = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add Goal")
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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = goal.title, style = MaterialTheme.typography.titleMedium)
            Text(text = goal.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            goal.subtasks.forEach { subtask ->
                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                    Checkbox(
                        checked = subtask.completed,
                        onCheckedChange = { viewModel.toggleSubtask(goal.id, subtask.id) },
                        colors = CheckboxDefaults.colors(
                            checkedColor = LowPriority,
                            uncheckedColor = MediumPriority
                        )
                    )
                    Text(subtask.title)
                }
            }

            var subtaskTitle by remember { mutableStateOf("") }
            OutlinedTextField(
                value = subtaskTitle,
                onValueChange = { subtaskTitle = it },
                label = { Text("New subtask") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(onClick = {
                if (subtaskTitle.isNotBlank()) {
                    viewModel.addSubtask(goal.id, Subtask(title = subtaskTitle))
                    subtaskTitle = ""
                }
            },
                colors = ButtonDefaults.buttonColors(containerColor = HighPriority)
                ) {
                Text("Add subtask")
            }
        }
    }
}