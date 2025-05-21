package com.amos_tech_code.foodhub.ui.feature.menu.add

import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.LightGray
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.ui.presentation.FoodHubTextField
import com.amos_tech_code.foodhub.ui.presentation.navigation.ImagePicker
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMenuItemScreen(
    navController: NavController,
    viewModel: AddMenuItemViewModel = hiltViewModel()
) {

    val name = viewModel.name.collectAsStateWithLifecycle()
    val description = viewModel.description.collectAsStateWithLifecycle()
    val price = viewModel.price.collectAsStateWithLifecycle()
    val uiState = viewModel.addMenuItemState.collectAsStateWithLifecycle()
    val selectedImage = viewModel.imageUrl.collectAsStateWithLifecycle()

    val imageUri =
        navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Uri?>("imageUri", null)
            ?.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = imageUri?.value) {
        imageUri?.value?.let {
            viewModel.onImageUrlChange(it)
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.addMenuItemEvent.collectLatest {
            when (it) {
                is AddMenuItemViewModel.AddMenuItemEvent.GoBack -> {
                    Toast.makeText(
                        navController.context, "Item added Successfully", Toast.LENGTH_SHORT
                    ).show()
                    navController.previousBackStackEntry?.savedStateHandle?.set("added", true)
                    navController.popBackStack()

                }

                is AddMenuItemViewModel.AddMenuItemEvent.AddNewImage -> {
                    navController.navigate(ImagePicker)
                }

                is AddMenuItemViewModel.AddMenuItemEvent.ShowErrorMessage -> {
                    Toast.makeText(navController.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Add Menu Item",
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                AsyncImage(
                    model = selectedImage.value,
                    contentDescription = "Food Image",
                    modifier = Modifier
                        .size(150.dp)
                        .clip(shape = RoundedCornerShape(8.dp))
                        .background(LightGray)
                        .clickable {
                            viewModel.onImageClicked()
                        },
                    contentScale = ContentScale.FillBounds
                )
                Icon(
                    imageVector = Icons.Default.AddAPhoto,
                    contentDescription = "Add food image",
                    modifier = Modifier.size(32.dp).align(Alignment.Center)
                )
            }
            FoodHubTextField(value = name.value, onValueChange = {
                viewModel.onNameChange(it)
            }, modifier = Modifier.fillMaxWidth(), label = { Text(text = "Name") },
                singleLine = true
            )
            FoodHubTextField(
                value = description.value, onValueChange = {
                    viewModel.onDescriptionChange(it)
                },
                modifier = Modifier.fillMaxWidth(), label = { Text(text = "Description") })
            FoodHubTextField(value = price.value, onValueChange = {
                viewModel.onPriceChange(it)
            }, modifier = Modifier.fillMaxWidth(), label = { Text(text = "Price") },
                singleLine = true
            )
            if (uiState.value is AddMenuItemViewModel.AddMenuItemState.Loading) {
                Button(onClick = { }, enabled = false) {
                    Text(text = "Adding Menu Item...")
                }
            } else {
                if (uiState.value is AddMenuItemViewModel.AddMenuItemState.Error) {
                    Text(
                        text = (uiState.value as AddMenuItemViewModel.AddMenuItemState.Error).message,
                        color = Red
                    )
                }
                Button(
                    onClick = { viewModel.addMenuItem() }) {
                    Text(text = "Add Menu Item")
                }
            }
        }
    }
}