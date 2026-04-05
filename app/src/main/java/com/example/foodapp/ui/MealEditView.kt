package com.example.foodapp.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.example.foodapp.data.DetailViewModel
import kotlinx.coroutines.launch

@Composable
fun EditView(
    viewModel: DetailViewModel,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    if (isLandscape) {
        Row(modifier = modifier.fillMaxSize()) {
            MealImageHeader(
                uriString = viewModel.editImageUri,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                EditForm(
                    viewModel = viewModel,
                    onTakePhoto = onTakePhoto,
                    onPickPhoto = onPickPhoto,
                    onSave = { scope.launch { viewModel.saveChanges() } },
                    onCancel = { viewModel.cancelEditing() }
                )
            }
        }
    } else {
        Column(modifier = modifier.verticalScroll(rememberScrollState())) {
            MealImageHeader(
                uriString = viewModel.editImageUri,
                modifier = Modifier.fillMaxWidth().height(200.dp)
            )

            EditForm(
                viewModel = viewModel,
                onTakePhoto = onTakePhoto,
                onPickPhoto = onPickPhoto,
                onSave = { scope.launch { viewModel.saveChanges() } },
                onCancel = { viewModel.cancelEditing() }
            )
        }
    }
}

@Composable
private fun EditForm(
    viewModel: DetailViewModel,
    onTakePhoto: () -> Unit,
    onPickPhoto: () -> Unit,
    onSave: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = onTakePhoto,
            modifier = Modifier.weight(1f)
        ) {
            Text("Take Photo")
        }
        OutlinedButton(
            onClick = onPickPhoto,
            modifier = Modifier.weight(1f)
        ) {
            Text("Select Photo")
        }
    }

    OutlinedTextField(
        value = viewModel.editName,
        onValueChange = { viewModel.editName = it },
        label = { Text("Name") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(8.dp))
    OutlinedTextField(
        value = viewModel.editInstructions,
        onValueChange = { viewModel.editInstructions = it },
        label = { Text("Instructions") },
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(Modifier.height(16.dp))
    Row {
        Button(onClick = onSave) {
            Text("Save")
        }
        Spacer(Modifier.width(8.dp))
        OutlinedButton(onClick = onCancel) { Text("Cancel") }
    }
}
