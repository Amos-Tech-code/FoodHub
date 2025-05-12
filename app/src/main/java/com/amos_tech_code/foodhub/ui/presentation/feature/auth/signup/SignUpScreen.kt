package com.amos_tech_code.foodhub.ui.presentation.feature.auth.signup

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.ui.presentation.BasicDialog
import com.amos_tech_code.foodhub.ui.presentation.FoodHubTextField
import com.amos_tech_code.foodhub.ui.presentation.GroupSocialButtons
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.ui.presentation.navigation.Login
import com.amos_tech_code.foodhub.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    navController: NavController,
    isCustomer: Boolean = true,
    viewModel: SignUpViewModel = hiltViewModel()
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }
    val name = viewModel.name.collectAsStateWithLifecycle()
    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }
    val isPasswordVisible = remember { mutableStateOf(false) }
    val keyBoardController = LocalSoftwareKeyboardController.current

    val uiState = viewModel.uiState.collectAsState()

    when (val state = uiState.value) {
        is SignUpEvent.Error -> {
            loading.value = false
            errorMessage.value = state.error
        }

        is SignUpEvent.Loading -> {
            errorMessage.value = null
            loading.value = true
        }

        else -> {
            loading.value = false
            errorMessage.value = null
        }
    }

    LaunchedEffect(true) {

        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is SignUpNavigationEvent.NavigateToLogin -> {
                    navController.navigate(Login)
                }

                is SignUpNavigationEvent.NavigateToHome -> {
                    navController.navigate(Home) {
                        popUpTo(AuthScreen) {
                            inclusive = true
                        }
                    }
                }

                is SignUpNavigationEvent.ShowErrorMessage -> {
                    showDialog = true
                }
            }
        }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(innerPadding)
        ) {

            Image(
                painter = painterResource(id = R.drawable.ic_auth_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .imePadding()
                    .padding(16.dp),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.sign_up),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(20.dp))
                FoodHubTextField(
                    value = name.value,
                    onValueChange = { viewModel.onNameChange(it) },
                    label = {
                        Text(text = stringResource(id = R.string.full_name), color = Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                FoodHubTextField(
                    value = email.value,
                    onValueChange = { viewModel.onEmailChanged(it) },
                    label = {
                        Text(text = stringResource(id = R.string.email), color = Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                FoodHubTextField(
                    value = password.value,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    label = {
                        Text(text = stringResource(id = R.string.password), color = Color.Gray)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (!isPasswordVisible.value) PasswordVisualTransformation() else VisualTransformation.None,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    trailingIcon = {
                        if (isPasswordVisible.value) {
                            IconButton(onClick = { isPasswordVisible.value = false }) {
                                Icon(
                                    imageVector = Icons.Rounded.VisibilityOff,
                                    contentDescription = "Hide Password"
                                )
                            }
                        } else {
                            IconButton(onClick = { isPasswordVisible.value = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Visibility,
                                    contentDescription = "Show Password"
                                )
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))
                Button(
                    onClick = {
                        viewModel.onSignUpClicked()
                        keyBoardController?.hide()
                    },
                    enabled = name.value.isNotEmpty() && email.value.isNotEmpty() && password.value.isNotEmpty(),
                    modifier = Modifier.height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Box {

                        AnimatedContent(
                            targetState = loading.value,
                            transitionSpec = {
                                fadeIn(animationSpec = tween(300)) + scaleIn(initialScale = 0.8f) togetherWith
                                        fadeOut(animationSpec = tween(300)) + scaleOut(targetScale = 0.8f)
                            }
                        ) { target ->
                            if (target) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 32.dp).size(24.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.sign_up),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.size(16.dp))
                Text(
                    text = stringResource(id = R.string.already_have_account),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            viewModel.onLoginClicked()
                        }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                if (isCustomer) {
                    GroupSocialButtons(
                        color = Color.Black,
                        viewModel = viewModel
                    )
                }
            }
        }
}

    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = { showDialog = false },
            sheetState = sheetState
        ) {
            BasicDialog(
                title = viewModel.error,
                description = viewModel.errorDescription,
                onClick = {
                    scope.launch {
                        sheetState.hide()
                        showDialog = false
                    }
                }
            )
        }
    }

}