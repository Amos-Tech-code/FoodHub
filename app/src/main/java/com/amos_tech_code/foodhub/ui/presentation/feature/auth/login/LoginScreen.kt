package com.amos_tech_code.foodhub.ui.presentation.feature.auth.login

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
import androidx.compose.ui.Alignment
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
import com.amos_tech_code.foodhub.ui.presentation.navigation.SignUp
import com.amos_tech_code.foodhub.ui.theme.Primary
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    isCustomer: Boolean = true,
    viewModel: SignInViewModel = hiltViewModel()
) {

    val email = viewModel.email.collectAsStateWithLifecycle()
    val password = viewModel.password.collectAsStateWithLifecycle()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val loading = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var isPasswordVisible by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    val keyBoardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(errorMessage.value) {
        if (errorMessage.value != null)
            scope.launch {
                showDialog = true
            }
    }

    Scaffold { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            val uiState = viewModel.uiState.collectAsStateWithLifecycle()
            when (val state = uiState.value) {

                is SignInViewModel.SignInEvent.Error -> {
                    // show error
                    loading.value = false
                    errorMessage.value = state.error
                }

                is SignInViewModel.SignInEvent.Loading -> {
                    loading.value = true
                    errorMessage.value = null
                }

                else -> {
                    loading.value = false
                    errorMessage.value = null
                }
            }

            LaunchedEffect(true) {
                viewModel.navigationEvent.collectLatest { event ->
                    when (event) {
                        is SignInViewModel.SignInNavigationEvent.NavigateToHome -> {
                            navController.navigate(Home) {
                                popUpTo(AuthScreen) {
                                    inclusive = true
                                }
                            }
                        }

                        is SignInViewModel.SignInNavigationEvent.NavigateToSignUp -> {
                            navController.navigate(SignUp)
                        }
                    }
                }
            }

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
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.weight(1f))
                Text(
                    text = stringResource(id = R.string.sign_in),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.size(20.dp))
                FoodHubTextField(
                    value = email.value,
                    onValueChange = {
                        viewModel.onEmailChanged(it)
                    },
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
                    onValueChange = {
                        viewModel.onPasswordChange(it)
                    },
                    label = {
                        Text(text = stringResource(id = R.string.password), color = Color.Gray)
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = if (!isPasswordVisible) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        if (isPasswordVisible) {
                            IconButton(onClick = { isPasswordVisible = false }) {
                                Icon(
                                    imageVector = Icons.Rounded.VisibilityOff,
                                    contentDescription = stringResource(R.string.hide_password)
                                )
                            }
                        } else {
                            IconButton(onClick = { isPasswordVisible = true }) {
                                Icon(
                                    imageVector = Icons.Rounded.Visibility,
                                    contentDescription = stringResource(R.string.show_password)
                                )
                            }
                        }
                    }
                )
                Spacer(modifier = Modifier.size(16.dp))

                Button(
                    onClick = { viewModel.onSignInClicked()
                              keyBoardController?.hide()
                    },
                    enabled = !loading.value && email.value.isNotBlank() && password.value.isNotBlank(),
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
                                    modifier = Modifier
                                        .padding(horizontal = 32.dp)
                                        .size(24.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.sign_in),
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 32.dp)
                                )
                            }

                        }


                    }
                }
                Spacer(modifier = Modifier.size(16.dp))

                Text(
                    text = stringResource(id = R.string.dont_have_account),
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable(
                            enabled = !loading.value
                        ) {
                            viewModel.onSignUpClicked()
                        }
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                if (isCustomer) {
                    GroupSocialButtons(
                        color = Color.Black,
                        viewModel = viewModel,
                    )
                }
            }
        }
    }

    if (showDialog) {
        ModalBottomSheet(
            onDismissRequest = {
                showDialog = false
            },
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