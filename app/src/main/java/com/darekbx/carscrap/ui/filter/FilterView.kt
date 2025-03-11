package com.darekbx.carscrap.ui.filter

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.darekbx.carscrap.ui.theme.CarScrapTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import java.util.Locale

@Composable
fun FilterView(filterViewModel: FilterViewModel = koinViewModel()) {

    var name by remember { mutableStateOf("") }

    var carMake by remember { mutableStateOf("") }

    var carModel by remember { mutableStateOf("") }
    var generation by remember { mutableStateOf("") }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text("Create new filter", style = MaterialTheme.typography.titleMedium)

        OutlinedTextField(
            value = name,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            onValueChange = { name = it },
            label = { Text("Filter name") }
        )

        CarMakeSelect(Modifier.padding(top = 4.dp, bottom = 8.dp)) { carMake = it }

        TextField(
            value = carModel,
            onValueChange = { carModel = it },
            label = { "Car model" }
        )

        TextField(
            enabled = false,
            value = generation,
            onValueChange = { generation = it },
            label = { "Generation" }
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = false, enabled = false, onCheckedChange = { })
            Text("Salvaged")
        }


        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                filterViewModel.verify("toyota", "corolla")
            }
        }) {
            Text("Verify filter")
        }
    }
}

@Composable
fun CarMakeSelect(modifier: Modifier, carMakes: CarMakes = koinInject(), onCarMake: (String) -> Unit = { }) {
    var selectedCarMake by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Box {
            OutlinedTextField(
                value = selectedCarMake,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                label = { Text("Car make") },
                trailingIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Dropdown Arrow",
                        modifier = Modifier.clickable { expanded = !expanded }
                    )
                }
            )

            Box(
                modifier = Modifier
                    .matchParentSize()
                    .clickable { expanded = !expanded }
            )

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.height(300.dp)
            ) {
                carMakes.makes.forEach { carMake ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = carMake.capitalize(),
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onClick = {
                            selectedCarMake = carMake.capitalize()
                            onCarMake(carMake)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun FilterViewPreview() {
    CarScrapTheme {
        FilterView()
    }
}