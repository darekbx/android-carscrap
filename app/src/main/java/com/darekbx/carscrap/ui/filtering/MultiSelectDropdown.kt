package com.darekbx.carscrap.ui.filtering

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp

data class AnnotatedItem(val text: String, val label: AnnotatedString) {
    constructor(text: String) : this(text, AnnotatedString(text))
}

@Composable
fun MultiSelectDropdown(
    items: List<AnnotatedItem>,
    label: String = "Select Items",
    selected: List<String> = emptyList(),
    onSelectionChanged: (List<String>) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedItems by remember { mutableStateOf(selected) }

    val displayText = when {
        selectedItems.isEmpty() -> label
        selectedItems.size == 1 -> selectedItems.first()
        selectedItems.size == 2 -> selectedItems.joinToString(", ")
        else -> "${selectedItems.size} items selected"
    }

    Column {
        Row(
            Modifier
                .padding(8.dp)
                .fillMaxWidth()
                .border(
                    1.dp, MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                    RoundedCornerShape(4.dp)
                )
                .clickable { expanded = !expanded },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = displayText ?: label,
                modifier = Modifier.padding(8.dp)
            )
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                contentDescription = "Dropdown Arrow",
                modifier = Modifier.padding(8.dp)
            )
        }

        DropdownMenu(
            expanded = expanded,
            offset = DpOffset(8.dp, 48.dp),
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .heightIn(max = 280.dp)
        ) {
            items.forEach { item ->
                val isSelected = selectedItems.contains(item.text)
                DropdownMenuItem(
                    text = {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = { checked ->
                                    selectedItems = if (checked) {
                                        selectedItems + item.text
                                    } else {
                                        selectedItems - item.text
                                    }
                                    onSelectionChanged(selectedItems.distinct())
                                }
                            )
                            Text(
                                text = item.label,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                    },
                    onClick = {
                        selectedItems = if (isSelected) {
                            selectedItems - item.text
                        } else {
                            selectedItems + item.text
                        }
                        onSelectionChanged(selectedItems.distinct())
                    }
                )

            }
        }
    }
}