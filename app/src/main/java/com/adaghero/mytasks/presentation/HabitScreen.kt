package com.adaghero.mytasks.presentation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.nativeCanvas
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
import androidx.compose.ui.graphics.toArgb


//Main screen for displaying habits
@Composable
fun HabitScreen(viewModel: HabitViewModel) {
    val habits by viewModel.habits.collectAsState()

    //Load habits when screen launches
    LaunchedEffect(Unit) { viewModel.loadHabits() }

    var showDialog by remember { mutableStateOf(false) }
    var newHabitName by remember { mutableStateOf("") }
    var newHabitFrequency by remember { mutableStateOf(HabitFrequency.DAILY) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {



        Text("Habit Planner", style = MaterialTheme.typography.headlineSmall)

        Spacer(Modifier.height(24.dp))

        Button(onClick = { showDialog = true }) { Text("Add Habit") }

        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(habits) { habit ->
                HabitItem(
                    habit = habit,
                    onMarkCompleted = { viewModel.markCompleted(habit.id) }

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
                        label = { Text("Habit Name") }
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
fun HabitItem(habit: Habit, onMarkCompleted: () -> Unit) {
    Card(
        modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(
            containerColor = when (habit.priority) {
                "HIGH" -> HighPriority
                "MEDIUM" -> MediumPriority
                "LOW" -> LowPriority
                else -> Info
            }
        )
        ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(habit.name, style = MaterialTheme.typography.titleMedium)
                    Text("Frequency: ${habit.frequency}")
                }
                Button(onClick = onMarkCompleted) { Text("Mark Today ✔") }
            }

            Spacer(Modifier.height(12.dp))

            WeeklyProgressChart(
                history = habit.history,
                barHeight = 60.dp,
                label = "Last 7 days"
            )
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
        OutlinedButton(onClick = { expanded = true }) {
            Text(
                when (selected) {
                    HabitFrequency.DAILY -> "DAILY"
                    HabitFrequency.WEEKLY -> "WEEKLY"
                }
            )
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            HabitFrequency.values().forEach {
                DropdownMenuItem(
                    text = {
                        Text(
                            when (it) {
                                HabitFrequency.DAILY -> "DAILY"
                                HabitFrequency.WEEKLY -> "WEEKLY"
                            }
                        )
                    },
                    onClick = {
                        onSelect(it)
                        expanded = false
                    }
                )
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

            //Draw baseline (X-axis)
                val daysAgo = 6 - index

                drawRect(
                    color = when {
                        v == 0f -> HighPriority
                        daysAgo <= 2 -> LowPriority
                        else -> MediumPriority
                    },
                    topLeft = androidx.compose.ui.geometry.Offset(left, barTop),
                    size = androidx.compose.ui.geometry.Size(barWidth, heightPx - barTop)
                )

                //Draw day label
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

        Spacer(Modifier.height(8.dp))

        //Legend
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)

        ) {
            Box(
                Modifier.size(12.dp).background(LowPriority, shape = MaterialTheme.shapes.extraSmall)
            )
            Text("Completed Recently", style = MaterialTheme.typography.bodySmall)

            Box(
                Modifier.size(12.dp).background(MediumPriority, shape = MaterialTheme.shapes.extraSmall)
            )
            Text("Completed Earlier", style = MaterialTheme.typography.bodySmall)

            Box(
                Modifier.size(12.dp).background(HighPriority, shape = MaterialTheme.shapes.extraSmall)
            )
            Text("Not Completed", style = MaterialTheme.typography.bodySmall)
        }
    }
}

