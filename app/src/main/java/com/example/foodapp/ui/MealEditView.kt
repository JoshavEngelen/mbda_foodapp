package com.example.foodapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
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

    Column(modifier = modifier) {
        MealImageHeader(viewModel.editImageUri)

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
            Button(onClick = {
                scope.launch {
                    viewModel.saveChanges()
                }
            }) {
                Text("Save")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = { viewModel.cancelEditing() }) { Text("Cancel") }
        }
    }
}
