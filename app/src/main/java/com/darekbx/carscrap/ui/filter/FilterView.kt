package com.darekbx.carscrap.ui.filter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
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
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Popup
import com.darekbx.carscrap.repository.remote.scrap.Link

@Composable
fun FilterView(filterViewModel: FilterViewModel = koinViewModel(), onBack: () -> Unit = { }) {

    val generations by filterViewModel.generations
    val inProgress by filterViewModel.inProgress
    val wasVerfied by filterViewModel.wasVerfied

    var carMake by remember { mutableStateOf("") }
    var carModel by remember { mutableStateOf("") }
    var generation by remember { mutableStateOf("") }
    var salvaged by remember { mutableStateOf(false) }
    var hasError by remember { mutableStateOf(false) }

    LaunchedEffect(wasVerfied) {
        if (wasVerfied == false) {
            carModel = ""
            hasError = true
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.Start
    ) {

        Title()

        if (hasError) {
            ErrorText()
        }

        CarMakeSelect(Modifier.padding(top = 4.dp, bottom = 4.dp)) {
            carMake = it
            hasError = false
        }

        OutlinedTextField(
            value = carModel,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
            onValueChange = {
                carModel = it
                hasError = false
            },
            label = { Text("Car model") }
        )

        Button(
            enabled = carMake.isNotBlank() && carModel.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = { filterViewModel.verify(carMake, carModel) }
        ) {
            Text("Verify make and model")
        }

        generations
            ?.takeIf { it.isNotEmpty() }
            ?.let { generationsList ->
                GenerationsSelect(Modifier.padding(top = 4.dp, bottom = 4.dp), generationsList) {
                    generation = it
                }
            }

        Row(modifier = Modifier, verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = salvaged, enabled = false, onCheckedChange = { salvaged = it })
            Text("Salvaged", color = MaterialTheme.colorScheme.onBackground)
        }

        Button(
            enabled = wasVerfied == true && carMake.isNotBlank() && carModel.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            onClick = {
                filterViewModel.saveFilter(carMake, carModel, generation, salvaged) {
                    onBack()
                }
            }
        ) {
            Text("Save filter")
        }
    }

    if (inProgress) {
        ProgressPopup()
    }
}

@Composable
private fun ProgressPopup() {
    Popup {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.4f))
        ) {
            CircularProgressIndicator(
                Modifier
                    .align(Alignment.Center)
                    .size(48.dp)
            )
        }
    }
}

@Composable
private fun Title() {
    Text(
        "Create new filter",
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun ErrorText() {
    Text(
        "Car model not found!",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.error
    )
}

@Composable
fun CarMakeSelect(
    modifier: Modifier,
    carMakes: CarMakes = koinInject(),
    onSelect: (String) -> Unit = { }
) {
    DropdownSelect(modifier, title = "Car make", carMakes.makes, onSelect)
}

@Composable
fun GenerationsSelect(
    modifier: Modifier,
    generations: List<Link>,
    onSelect: (String) -> Unit = { }
) {
    DropdownSelect(modifier, title = "Generations", generations.map { it.title }) { title ->
        val url = generations.find { it.title == title }?.url
        val generation = url?.substringAfterLast("filter_enum_generation%5D=") ?: ""
        onSelect(generation)
    }
}

@Composable
fun DropdownSelect(
    modifier: Modifier,
    title: String,
    items: List<String>,
    onSelect: (String) -> Unit = { }
) {
    var selectedItem by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Box {
            OutlinedTextField(
                value = selectedItem,
                onValueChange = { },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onBackground),
                label = { Text(title) },
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
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        },
                        onClick = {
                            selectedItem = item
                            onSelect(item)
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
