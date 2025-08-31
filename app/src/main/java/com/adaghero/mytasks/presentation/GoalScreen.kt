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
import androidx.compose.foundation.shape.RoundedCornerShape
import com.adaghero.mytasks.viewmodel.GoalViewModel
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment

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
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = goal.title, style = MaterialTheme.typography.titleMedium)
            Text(text = goal.description, style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))

            goal.subtasks.forEach { sub ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = sub.completed,
                        onCheckedChange = { viewModel.toggleSubtask(goal.id, sub.id) },
                    )
                    Text(sub.title)
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