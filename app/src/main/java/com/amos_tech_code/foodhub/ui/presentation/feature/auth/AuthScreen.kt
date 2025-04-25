package com.amos_tech_code.foodhub.ui.presentation.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.ui.presentation.BasicDialog
import com.amos_tech_code.foodhub.ui.presentation.GroupSocialButtons
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.ui.presentation.navigation.Login
import com.amos_tech_code.foodhub.ui.presentation.navigation.SignUp
import com.amos_tech_code.foodhub.ui.theme.DarkBlue
import com.amos_tech_code.foodhub.ui.theme.Primary
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    isCustomer: Boolean = true,
    viewModel: AuthScreenViewModel = hiltViewModel()
) {

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showDialog by remember { mutableStateOf(false) }

    val imageSize = remember {
        mutableStateOf(IntSize.Zero)
    }
    val brush = Brush.verticalGradient(
        colors = listOf(
            Color.Transparent,
            DarkBlue
        ),
        startY = imageSize.value.height.toFloat() / 3
    )

    LaunchedEffect(true) {
        viewModel.navigationEvent.collectLatest { event ->
            when (event) {
                is AuthScreenViewModel.AuthNavigationEvent.NavigateToHome -> {
                    navController.navigate(Home) {
                        popUpTo(AuthScreen) {
                            inclusive = true
                        }
                    }
                }

                is AuthScreenViewModel.AuthNavigationEvent.NavigateToSignUp -> {
                    navController.navigate(SignUp)
                }

                is AuthScreenViewModel.AuthNavigationEvent.ShowErrorDialog -> {
                    showDialog = true
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val uiState = viewModel.uiState.collectAsState()
        val loading = remember { mutableStateOf(false) }

        when(uiState.value) {
            is AuthScreenViewModel.AuthEvent.Loading -> {
                loading.value = true
            }
            else -> {
                loading.value = false
            }
        }
        Image(
            painter = painterResource(R.drawable.background_img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                    imageSize.value = it.size
                }
                .alpha(0.6f),
            contentScale = ContentScale.FillBounds
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(brush = brush)
        )

        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Text(
                text = stringResource(R.string.skip),
                color = Primary
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 110.dp)
                .padding(16.dp)
        ) {
            Text(
                text = stringResource(id = R.string.welcome),
                color = Color.Black,
                modifier = Modifier,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.food_hub),
                color = Primary,
                modifier = Modifier,
                fontSize = 50.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(id = R.string.food_hub_desc),
                color = Color.Black,
                fontSize = 20.sp,
                modifier = Modifier.padding(vertical = 16.dp)
            )

        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isCustomer) {
                GroupSocialButtons(
                    viewModel = viewModel
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.navigate(SignUp)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray.copy(alpha = 0.2f)),
                    shape = RoundedCornerShape(32.dp),
                    border = BorderStroke(1.dp, Color.White)
                ) {
                    Text(text = stringResource(id = R.string.sign_with_email), color = Color.White)
                }
            }

            TextButton(
                onClick = {
                    navController.navigate(Login)
                }
            ) {
                Text(text = stringResource(id = R.string.already_have_account), color = Color.White)
            }

        }

        if (loading.value) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(shape = RoundedCornerShape(8.dp), color = Color.White)
                        .size(80.dp)
                ) {
                    CircularProgressIndicator(color = Primary)
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
