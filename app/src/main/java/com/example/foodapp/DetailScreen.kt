package com.example.foodapp

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.foodapp.data.DetailUiState
import com.example.foodapp.data.DetailViewModel
import com.example.foodapp.ui.DisplayView
import com.example.foodapp.ui.EditView
import com.example.foodapp.utils.ShareUtils
import com.example.foodapp.utils.StorageUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(viewModel: DetailViewModel, onBackClick: () -> Unit) {
    val context = LocalContext.current
    val uiState = viewModel.uiState

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()) { bitmap ->
        bitmap?.let {
            val uri = StorageUtils.saveBitmapToInternal(context, it)
            if (uri != null) {
                viewModel.editImageUri = uri.toString()
            }
        }
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) cameraLauncher.launch(null)
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        uri?.let {
            val internalUri = StorageUtils.copyUriToInternal(context, it)
            if (internalUri != null) {
                viewModel.editImageUri = internalUri.toString()
            } else {
                viewModel.editImageUri = it.toString()
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Meal Detail") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            when (uiState) {
                is DetailUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is DetailUiState.Error -> Text(uiState.message)
                is DetailUiState.Success -> {
                    val meal = uiState.meal
                    if (viewModel.isEditing) {
                        EditView(
                            viewModel = viewModel,
                            onTakePhoto = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    cameraLauncher.launch(null)
                                } else {
                                    permissionLauncher.launch(Manifest.permission.CAMERA)
                                }
                            },
                            onPickPhoto = {
                                imagePickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                            },
                        )
                    } else {
                        DisplayView(
                            meal = meal,
                            onEdit = { viewModel.startEditing(meal) },
                            onShare = { ShareUtils.shareMeal(context, meal) },
                            onFavorite = { viewModel.toggleFavorite() }
                        )
                    }
                }
            }
        }
    }
}
