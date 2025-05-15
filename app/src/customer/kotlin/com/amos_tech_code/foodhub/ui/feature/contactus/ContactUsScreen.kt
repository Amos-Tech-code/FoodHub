package com.amos_tech_code.foodhub.ui.feature.contactus

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.MapsUgc
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Textsms
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R


@Composable
fun ContactUsScreen(
    navController: NavController
) {
    val phoneNumber = "+254743217122"
    val email = "amosk5132@gmail.com"
    val context = LocalContext.current

    Scaffold(
        topBar = {
            ContactTopBar(onBackClick = {
                navController.navigateUp()
            })
        },

    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .imePadding()
        ) {
            ContactScreen(
                onPhoneClick = {
                    val intent = Intent(Intent.ACTION_DIAL).apply {
                        data = Uri.parse("tel:${phoneNumber}")
                    }
                    startActivity(context, intent, null)
                },
                onEmailClick = {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:${email}")
                    }
                    startActivity(context, intent, null)
                },
                onSocialMediaClick = { platform ->
                    // Handle social media links
                },
                onSubmitFeedback = { message ->
                    //viewModel.submitFeedback(message)
                }
            )
        }
    }

}



@Composable
fun ContactScreen(
    onPhoneClick: () -> Unit,
    onEmailClick: () -> Unit,
    onSocialMediaClick: (platform: String) -> Unit,
    onSubmitFeedback: (message: String) -> Unit
) {
    var feedbackMessage by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Customer Support Section
        ContactSection(title = stringResource(R.string.contact_support_title)) {
            ContactMethodItem(
                icon = Icons.Default.Phone,
                title = stringResource(R.string.contact_call_us),
                subtitle = stringResource(R.string.contact_24_hours),
                onClick = onPhoneClick
            )

            ContactMethodItem(
                icon = Icons.Default.Email,
                title = stringResource(R.string.contact_email_us),
                subtitle = stringResource(R.string.contact_response_time),
                onClick = onEmailClick
            )
        }

        // Social Media Section
        ContactSection(title = stringResource(R.string.contact_social_title)) {
            SocialMediaButton(
                icon = Icons.Default.Facebook,
                platform = "Facebook",
                onClick = onSocialMediaClick
            )

            SocialMediaButton(
                icon = Icons.Default.MapsUgc,
                platform = "Instagram",
                onClick = onSocialMediaClick
            )

            SocialMediaButton(
                icon = Icons.Default.Textsms,
                platform = "Twitter",
                onClick = onSocialMediaClick
            )
        }

        // Feedback Form Section
        ContactSection(title = stringResource(R.string.contact_feedback_title)) {
            TextField(
                value = feedbackMessage,
                onValueChange = { feedbackMessage = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .border(width = 1.dp , shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primary)
                    .height(120.dp),
                label = { Text(stringResource(R.string.contact_feedback_hint)) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                )
            )

            Button (
                onClick = {
                    feedbackMessage = ""
                    onSubmitFeedback(feedbackMessage)
                },
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                enabled = feedbackMessage.isNotEmpty()
            ) {
                Text(stringResource(R.string.contact_submit_feedback))
            }
        }

        // FAQ Section
        TextButton(
            onClick = { /* Handle FAQ click */ },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = stringResource(R.string.contact_view_faqs),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.contact_title),
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = stringResource(R.string.cd_back)
                )
            }
        }
    )
}

@Composable
private fun ContactSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(
                start = 16.dp,
                end = 16.dp,
                top = 16.dp,
                bottom = 8.dp
            )
        )
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                content()
            }
        }
    }
}

@Composable
private fun ContactMethodItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = stringResource(R.string.cd_navigate),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
    }
}

@Composable
private fun SocialMediaButton(
    icon: ImageVector,
    platform: String,
    onClick: (String) -> Unit
) {
    OutlinedButton(
        onClick = { onClick(platform) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Icon(
            imageVector = icon,
            contentDescription = platform,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = platform)
    }
}