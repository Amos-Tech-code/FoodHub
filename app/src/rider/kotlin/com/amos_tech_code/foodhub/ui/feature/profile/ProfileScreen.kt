package com.amos_tech_code.foodhub.ui.feature.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.ui.presentation.FoodHubTextField
import com.amos_tech_code.foodhub.ui.presentation.navigation.AuthScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.Home
import com.amos_tech_code.foodhub.ui.theme.BrightYellow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {

    LaunchedEffect(key1 = true) {
        viewModel.navigationEvents.collectLatest { event ->
            when (event) {
                is ProfileViewModel.ProfileNavigationEvents.NavigateToLogin -> {
                    navController.navigate(AuthScreen) {
                        popUpTo(Home) {
                            inclusive = true
                        }
                    }
                }
                else -> Unit
            }
        }
    }
    val scrollState = rememberScrollState()

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_profile_bg),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillBounds
            )
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .align(Center)
            ) {
                ProfileHeaderSection(
                    user = User(
                        id = "1",
                        name = "John Doe",
                        email = "rider1@example.com",
                        profileImage = null,
                    ),
                    onEditClick = {
                    }
                )
                ProfileSection(
                    fullName = "John Doe",
                    phoneNumber = "+254712345678",
                    email = "rider1@example.com",
                )
            }

            Box(
                modifier = Modifier
                    .padding(16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .align(Alignment.TopEnd)
                    .clickable {
                    }
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Open settings",
                    modifier = Modifier.size(36.dp)
                )
            }

            //Save Changes Button
            Button(
                onClick = { /*TODO*/ },
                enabled = false,
                modifier = Modifier
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = (-100).dp)
            ) {
                Text(text = "Save Changes")
            }
        }
    }
}


@Composable
private fun ProfileHeaderSection(
    user: User?,
    onEditClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.BottomEnd,
            modifier = Modifier
                .background(Color.White, CircleShape)
                .padding(8.dp)
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = BrightYellow,
                        shape = CircleShape
                    )
                    .padding(8.dp),
                contentAlignment = Center
            ) {
                AsyncImage(
                    model = user?.profileImage ?: Image(
                        painter = painterResource(id = R.drawable.ic_profile),
                        contentDescription = stringResource(R.string.cd_profile_picture),
                        colorFilter = ColorFilter.tint(Color.White),
                        modifier = Modifier.size(60.dp)
                    ),
                    contentDescription = stringResource(R.string.cd_profile_picture),
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .align(Center),
                    contentScale = ContentScale.Crop
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = CircleShape
                    )
                    .size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = stringResource(R.string.cd_edit_profile),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = user?.name ?: stringResource(R.string.profile_guest_user),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold
        )

        if (!user?.email.isNullOrEmpty()) {
            Text(
                text = user?.email ?: "",
                style = MaterialTheme.typography.bodyMedium,
            )
        }

    }
}

@Composable
private fun ProfileSection(
    fullName: String?,
    phoneNumber: String?,
    email: String?,
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Column {
            Text(
                text = "Full Name",
            )
            FoodHubTextField(
                value = fullName ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column {
            Text(
                text = "Email",
            )
            FoodHubTextField(
                value = email ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Column {
            Text(
                text = "Phone Number",
            )
            FoodHubTextField(
                value = phoneNumber ?: "",
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth()
            )
        }

    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LogOut(onLogOutClick: () -> Unit) {

    var showDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(25.dp))
            .background(MaterialTheme.colorScheme.primary)
            .clickable { showDialog = true }
            .padding(vertical = 8.dp, horizontal = 24.dp),
        verticalAlignment = CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.background)
                .size(28.dp)
                .padding(8.dp),
            contentAlignment = Center
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_logout),
                contentDescription = "logout"
            )
        }

        Text(
            text = "LogOut",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.padding(start = 8.dp)
        )
    }

    if (showDialog) {
        BasicAlertDialog(
            onDismissRequest = { showDialog = false},
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        ) {
            Column(
                horizontalAlignment = CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp)
            ) {
                Text(text = "Are you sure you want to log out?")
                Row(
                    verticalAlignment = CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Button(onClick = { showDialog = false }) {
                        Text(text = "Cancel")
                    }
                    TextButton(onClick = { onLogOutClick() }) {
                        Text(text = "Log out")
                    }
                }
            }
        }
    }
}


// Data Model
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profileImage: String? = null,
    val phoneNumber: String? = null
)