package com.amos_tech_code.foodhub.ui.feature.help_faqs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LiveHelp
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.ui.presentation.navigation.ContactUs

@Composable
fun HelpSupportScreen(
    navController: NavController
) {
    val scrollState = rememberScrollState()
    val expandedFAQItems = remember { mutableStateMapOf<String, Boolean>() }
    val faqItems = remember { getFAQItems() }

    Scaffold(
        topBar = {
            HelpSupportTopBar(onBackClick = {
                navController.navigateUp()
            })
        },
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding)
        ) {
            // Quick Help Section
            HelpSection(title = stringResource(R.string.help_quick_help)) {
                HelpActionCard(
                    icon = Icons.Default.LiveHelp,
                    title = stringResource(R.string.help_live_chat),
                    subtitle = stringResource(R.string.help_live_chat_subtitle),
                    onClick = {

                    }
                )

                HelpActionCard(
                    icon = Icons.Default.Phone,
                    title = stringResource(R.string.help_call_support),
                    subtitle = stringResource(R.string.help_call_subtitle),
                    onClick = {

                    }
                )

                HelpActionCard(
                    icon = Icons.Default.Email,
                    title = stringResource(R.string.help_email_support),
                    subtitle = stringResource(R.string.help_email_subtitle),
                    onClick = {

                    }
                )
            }

            // FAQ Section
            HelpSection(title = stringResource(R.string.help_faq)) {
                faqItems.forEach { faq ->
                    FAQItem(
                        question = faq.question,
                        answer = faq.answer,
                        isExpanded = expandedFAQItems[faq.id] ?: false,
                        onClick = {
                            expandedFAQItems[faq.id] = !(expandedFAQItems[faq.id] ?: false)
                        }
                    )
                }
            }

            // Help Articles Section
            HelpSection(title = stringResource(R.string.help_articles)) {
                HelpArticleCard(
                    title = stringResource(R.string.help_article_order_issues),
                    onClick = { /* Navigate to order help */ }
                )
                HelpArticleCard(
                    title = stringResource(R.string.help_article_payment_issues),
                    onClick = { /* Navigate to payment help */ }
                )
                HelpArticleCard(
                    title = stringResource(R.string.help_article_delivery_issues),
                    onClick = { /* Navigate to delivery help */ }
                )
            }

            // Contact Button
            Button(
                onClick = {
                    navController.navigate(ContactUs)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            ) {
                Text(
                    text = stringResource(R.string.help_contact_support),
                )
            }
        }
    }
}

// Helper Components
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HelpSupportTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.help_title),
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
        },

    )
}

@Composable
private fun HelpSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
private fun HelpActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
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
}

@Composable
private fun FAQItem(
    question: String,
    answer: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = question,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (isExpanded) "Collapse" else "Expand"
            )
        }

        if (isExpanded) {
            Text(
                text = answer,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun HelpArticleCard(
    title: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = stringResource(R.string.cd_navigate),
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
            )
        }
    }
}

// Data Models and Sample Data
private data class FAQItem(
    val id: String,
    val question: String,
    val answer: String
)

private fun getFAQItems(): List<FAQItem> {
    return listOf(
        FAQItem(
            id = "1",
            question = "How do I track my order?",
            answer = "You can track your order in real-time through the 'Orders' section in the app. Once your order is out for delivery, you'll see a map with your delivery person's location."
        ),
        FAQItem(
            id = "2",
            question = "What payment methods do you accept?",
            answer = "We accept stripe payments and cash on delivery. Some restaurants may have specific payment options."
        ),
        FAQItem(
            id = "3",
            question = "How do I change my delivery address?",
            answer = "You can change your address before placing the order. For existing orders, contact our support team immediately."
        )
    )
}