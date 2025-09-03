package com.adaghero.mytasks.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.adaghero.mytasks.model.Habit
import com.adaghero.mytasks.model.HabitFrequency
import com.adaghero.mytasks.ui.theme.*
import com.adaghero.mytasks.viewmodel.HabitViewModel
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import java.time.temporal.WeekFields
import androidx.compose.animation.animateColorAsState


//Main screen for displaying habits
@Composable
fun HabitScreen(viewModel: HabitViewModel) {
    val habits by viewModel.habits.collectAsState()
    var showDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    var newHabitFrequency by remember { mutableStateOf(HabitFrequency.DAILY) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text("Habit Planner", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(16.dp))

        Button(
            onClick = { showDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
        ) { Text("Add Habit", color = Color.White) }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(habits) { habit ->
                HabitItem(
                    habit = habit,
                    onMarkCompleted = { viewModel.markCompleted(habit.id) },
                    onDelete = { viewModel.deleteHabit(habit.id) }
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("New Habit") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newHabitName,
                        onValueChange = { newHabitName = it },
                        placeholder = { Text("Habit Name") }
                    )
                    Spacer(Modifier.height(8.dp))
                    DropdownMenuHabitFrequency(
                        selected = newHabitFrequency,
                        onSelect = { newHabitFrequency = it }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newHabitName.isNotBlank()) {
                        viewModel.addHabit(
                            Habit(
                                id = UUID.randomUUID().toString(),
                                name = newHabitName,
                                frequency = newHabitFrequency
                            )
                        )
                    }
                    showDialog = false
                    newHabitName = ""
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) { Text("Cancel") }
            }
        )
    }
}

//Single habit item with progress chart
@Composable
fun HabitItem(habit: Habit, onMarkCompleted: () -> Unit, onDelete: () -> Unit) {

    val completed = when (habit.frequency) {
        HabitFrequency.DAILY -> habit.isCompletedToday()
        HabitFrequency.WEEKLY -> habit.isCompletedThisWeek()
    }

    val buttonColor by animateColorAsState(
        if (completed) Color(0xFF10B981) else Color(0xFF3B82F6)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(Modifier.weight(1f)) {
                    Text(habit.name, style = MaterialTheme.typography.titleMedium)
                    Text("Frequency: ${habit.frequency}")
                }

                Row {
                    IconButton(onClick = onDelete) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete habit",
                            tint = Color.Red
                        )
                    }

                    Button(
                        onClick = onMarkCompleted,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3B82F6))
                    ) {
                        Text(
                            if (completed) "Done ✅" else when (habit.frequency) {
                                HabitFrequency.DAILY -> "Mark Today"
                                HabitFrequency.WEEKLY -> "Mark This Week"
                            },
                            color = Color.White
                        )
                    }
                }
            }


            Spacer(Modifier.height(12.dp))

            when (habit.frequency) {
                HabitFrequency.DAILY -> WeeklyProgressChart(
                    history = habit.history,
                    barHeight = 80.dp,
                    label = "This Week"
                )

                HabitFrequency.WEEKLY -> WeeklySummaryChart(
                    history = habit.history,
                    barHeight = 80.dp,
                    label = "Last Weeks")
            }
        }
    }
}

//Dropdown for selecting habit frequency
@Composable
fun DropdownMenuHabitFrequency(
    selected: HabitFrequency,
    onSelect: (HabitFrequency) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selected.name)
            Icon(Icons.Default.ArrowDropDown, null)
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            HabitFrequency.values().forEach {
                DropdownMenuItem(text = { Text(it.name) }, onClick = {
                    onSelect(it); expanded = false
                })
            }
        }
    }
}

//Weekly progress chart using Canvas
@Composable
fun WeeklyProgressChart(
    history: List<String>,
    barHeight: Dp,
    label: String
) {
    val today = LocalDate.now()
    val last7 = (6 downTo 0).map { today.minusDays(it.toLong()) }
    val doneSet = history.toSet()
    val values = last7.map { date -> if (doneSet.contains(date.toString())) 1f else 0f }
    val dayLabels = last7.map { date ->
        date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault())
    }

    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        val heightPx = with(LocalDensity.current) { barHeight.toPx() }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight + 24.dp) //space for labels
        ) {
            val barCount = values.size
            val spacing = size.width * 0.04f
            val totalSpacing = spacing * (barCount + 1)
            val barWidth = (size.width - totalSpacing) / barCount

            values.forEachIndexed { index, v ->
                val left = spacing + index * (barWidth + spacing)
                val barTop = heightPx * (1f - v)

                drawRect(
                    color = if (v == 1f) Color(0xFF10B981) else Color.LightGray,
                    topLeft = androidx.compose.ui.geometry.Offset(left, barTop),
                    size = androidx.compose.ui.geometry.Size(barWidth, heightPx - barTop)
                )

                drawContext.canvas.nativeCanvas.apply {
                    val text = dayLabels[index].uppercase(Locale.getDefault())
                    val textPaint = android.graphics.Paint().apply {
                        color = onSurfaceColor.toArgb()
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 10.sp.toPx()
                        isAntiAlias = true
                    }
                    drawText(text, left + barWidth / 2f, heightPx + 14.dp.toPx(), textPaint)
                }
            }
        }
    }
}

//Weekly summary chart for weekly habits
@Composable
fun WeeklySummaryChart(
    history: List<String>,
    barHeight: Dp,
    label: String
) {
    val today = LocalDate.now()
    val weekField = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear()
    val last6Weeks = (5 downTo 0).map { today.minusWeeks(it.toLong()) }
    val doneWeeks = history.map { LocalDate.parse(it) }
        .map { it.get(weekField) to it.year }
        .toSet()

    val values = last6Weeks.map { date ->
        val week = date.get(weekField)
        val year = date.year
        if (doneWeeks.contains(week to year)) 1f else 0f
    }

    val weekLabels = last6Weeks.map { "W${it.get(weekField)}" }
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))

        val heightPx = with(LocalDensity.current) { barHeight.toPx() }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(barHeight + 24.dp)
        ) {
            val barCount = values.size
            val spacing = size.width * 0.04f
            val totalSpacing = spacing * (barCount + 1)
            val barWidth = (size.width - totalSpacing) / barCount

            values.forEachIndexed { index, v ->
                val left = spacing + index * (barWidth + spacing)
                val barTop = heightPx * (1f - v)

                drawRect(
                    color = if (v == 1f) Color(0xFF10B981) else Color.LightGray,
                    topLeft = androidx.compose.ui.geometry.Offset(left, barTop),
                    size = androidx.compose.ui.geometry.Size(barWidth, heightPx - barTop)
                )

                drawContext.canvas.nativeCanvas.apply {
                    val text = weekLabels[index]
                    val textPaint = android.graphics.Paint().apply {
                        color = onSurfaceColor.toArgb()
                        textAlign = android.graphics.Paint.Align.CENTER
                        textSize = 10.sp.toPx()
                        isAntiAlias = true
                    }
                    drawText(text, left + barWidth / 2f, heightPx + 14.dp.toPx(), textPaint)
                }
            }
        }
    }
}
