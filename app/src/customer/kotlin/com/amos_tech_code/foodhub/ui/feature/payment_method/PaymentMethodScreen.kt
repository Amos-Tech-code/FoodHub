package com.amos_tech_code.foodhub.ui.feature.payment_method

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amos_tech_code.foodhub.R

@Composable
fun PaymentMethodsScreen(
    navController: NavController,
) {
    // Sample data - replace with your actual payment methods
    val paymentCards = remember {
        listOf(
            PaymentCard(
                id = "1",
                last4 = "4242",
                expMonth = 12,
                expYear = 25,
                cardType = CardType.VISA,
                isDefault = true
            ),
            PaymentCard(
                id = "2",
                last4 = "5555",
                expMonth = 6,
                expYear = 25,
                cardType = CardType.MASTERCARD
            ),
            PaymentCard(
                id = "3",
                last4 = "1111",
                expMonth = 3,
                expYear = 25,
                cardType = CardType.AMEX
            ),
            PaymentCard(
                id = "4",
                last4 = "9999",
                expMonth = 9,
                expYear = 25,
                cardType = CardType.DISCOVER
            ),
            PaymentCard(
                id = "5",
                last4 = "8888",
                expMonth = 11,
                expYear = 25,
                cardType = CardType.UNKNOWN
            )
        )
    }

    var selectedCard by remember { mutableStateOf(paymentCards.firstOrNull { it.isDefault }) }

    Scaffold(
        topBar = {
            PaymentMethodsTopBar(onBackClick = {
                navController.navigateUp()
            })
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = {

                },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.payment_add_card)
                    )
                },
                text = {
                    Text(text = stringResource(R.string.payment_add_card))
                },
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Stripe Section Header
            PaymentSectionHeader(
                title = stringResource(R.string.payment_credit_debit_cards),
                icon = Icons.Default.CreditCard
            )

            // Payment Cards List
            LazyColumn {
                items(paymentCards) { card ->
                    PaymentCardItem(
                        card = card,
                        isSelected = card.id == selectedCard?.id,
                        onCardClick = {
                            selectedCard = card
                            //onCardSelected(card)
                        },
                        onEditClick = { /* Handle edit */ },
                        onDeleteClick = { /* Handle delete */ }
                    )
                }
            }

            // Stripe Security Info
            PaymentSecurityInfo()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.payment_title),
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
private fun PaymentSectionHeader(title: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
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
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun PaymentCardItem(
    card: PaymentCard,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        border = if (isSelected) {
            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
        } else {
            null
        }
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onCardClick)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Card Brand Icon (use your own vector assets)
                Image(
                    painter = painterResource(id = card.cardType.iconRes),
                    contentDescription = stringResource(R.string.cd_card_type),
                    modifier = Modifier.size(40.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${card.cardType.displayName} •••• ${card.last4}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Expires ${card.expMonth}/${card.expYear}",
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    if (card.isDefault) {
                        Text(
                            text = stringResource(R.string.payment_default_card),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = stringResource(R.string.cd_selected),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            HorizontalDivider()

            Row {
                TextButton(
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.payment_edit))
                }

                TextButton(
                    onClick = onDeleteClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = stringResource(R.string.payment_remove),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun PaymentSecurityInfo() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Security,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.payment_secure_payments),
                style = MaterialTheme.typography.titleSmall
            )
        }

        Text(
            text = stringResource(R.string.payment_security_description),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )

        // Stripe logo (add your own Stripe badge asset)
        Image(
            painter = painterResource(
                com.stripe.payments.model.R.drawable.stripe_ic_mastercard
            ),
            contentDescription = stringResource(R.string.cd_stripe_badge),
            modifier = Modifier
                .padding(top = 8.dp)
                .height(24.dp)
        )
    }
}

// Data Models
data class PaymentCard(
    val id: String,
    val last4: String,
    val expMonth: Int,
    val expYear: Int,
    val cardType: CardType,
    val isDefault: Boolean = false
)

enum class CardType(
    val displayName: String,
    val iconRes: Int
) {
    VISA("Visa",
        com.stripe.payments.model.R.drawable.stripe_ic_visa
    ),
    MASTERCARD("Mastercard",
        com.stripe.payments.model.R.drawable.stripe_ic_mastercard
    ),
    AMEX("American Express",
        com.stripe.payments.model.R.drawable.stripe_ic_amex
    ),
    DISCOVER("Discover",
        com.stripe.payments.model.R.drawable.stripe_ic_discover
        ),
    UNKNOWN("Card",
        com.stripe.payments.model.R.drawable.stripe_ic_unknown
    )
}