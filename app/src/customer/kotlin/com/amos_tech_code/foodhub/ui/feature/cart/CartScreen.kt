package com.amos_tech_code.foodhub.ui.feature.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.amos_tech_code.foodhub.R
import com.amos_tech_code.foodhub.data.model.Address
import com.amos_tech_code.foodhub.data.model.response.CartItem
import com.amos_tech_code.foodhub.data.model.response.CheckoutDetails
import com.amos_tech_code.foodhub.ui.presentation.BasicDialog
import com.amos_tech_code.foodhub.ui.presentation.ErrorScreen
import com.amos_tech_code.foodhub.ui.presentation.navigation.AddressList
import com.amos_tech_code.foodhub.ui.presentation.navigation.OrderSuccess
import com.amos_tech_code.foodhub.utils.StringUtils
import com.stripe.android.PaymentConfiguration
import com.stripe.android.paymentsheet.PaymentSheet
import com.stripe.android.paymentsheet.PaymentSheetResult
import com.stripe.android.paymentsheet.rememberPaymentSheet
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    navController: NavController,
    viewModel: CartViewModel
) {

    val uiState = viewModel.uiState.collectAsStateWithLifecycle()
    val showErrorDialog = remember { mutableStateOf(false) }
    val address = navController.currentBackStackEntry?.savedStateHandle?.getStateFlow<Address?>(
        "selectedAddress", null)?.collectAsStateWithLifecycle()

    val paymentSheet = rememberPaymentSheet(paymentResultCallback = {
        if (it is PaymentSheetResult.Completed) {
            viewModel.onPaymentSuccess()
        } else {
            viewModel.onPaymentFailure()
        }
    })

    LaunchedEffect(key1 = address?.value) {
        address?.value?.let {
            viewModel.setSelectedAddress(it)
        }
    }
    LaunchedEffect(key1 = true) {
        viewModel.event.collectLatest {
            when (it) {
                CartViewModel.CartEvent.OnCheckOut -> {

                }
                CartViewModel.CartEvent.OnQuantityUpdateError -> {
                    showErrorDialog.value = true
                }
                CartViewModel.CartEvent.OnRemoveItemError -> {
                    showErrorDialog.value = true
                }
                CartViewModel.CartEvent.ShowErrorDialog -> {}
                CartViewModel.CartEvent.OnAddressClicked -> {
                    navController.navigate(AddressList)
                }

                is CartViewModel.CartEvent.OnInitiatePayment -> {
                    PaymentConfiguration.init(navController.context, it.data.publishableKey)

                    val customer = PaymentSheet.CustomerConfiguration(
                        it.data.customerId,
                        it.data.ephemeralKeySecret
                    )
                    val paymentSheetConfiguration = PaymentSheet.Configuration(
                        merchantDisplayName = "Food Hub",
                        customer = customer,
                        allowsDelayedPaymentMethods = false
                    )
                    //Initiate Payment
                    paymentSheet.presentWithPaymentIntent(
                        it.data.paymentIntentClientSecret,
                        paymentSheetConfiguration
                    )
                }

                is CartViewModel.CartEvent.OrderSuccess -> {
                    navController.navigate(OrderSuccess(it.orderId!!))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "Cart", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.titleLarge)
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.navigateUp() },
                        modifier = Modifier
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            when (uiState.value) {
                is CartViewModel.CartUiState.Error -> {
                    val errorMessage = (uiState.value as CartViewModel.CartUiState.Error).message
                    ErrorScreen(
                        message = errorMessage,
                        onRetry = {
                            viewModel.retry()
                        }
                    )
                }

                is CartViewModel.CartUiState.Loading -> {
                    Spacer(modifier = Modifier.size(16.dp))
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Spacer(modifier = Modifier.size(16.dp))
                        CircularProgressIndicator()
                    }
                }

                is CartViewModel.CartUiState.Nothing -> {
                }

                is CartViewModel.CartUiState.Success -> {
                    val data = (uiState.value as CartViewModel.CartUiState.Success).data
                    val selectedAddress = viewModel.selectedAddress.collectAsStateWithLifecycle()

                    if (data.items.isNotEmpty()) {

                            Box(modifier = Modifier.fillMaxSize()) {
                                // Scrollable content
                                LazyColumn(
                                    modifier = Modifier.fillMaxHeight(),
                                    contentPadding = PaddingValues(
                                        bottom = 300.dp
                                    ),
                                ) {
                                    items(data.items) {
                                        CartItemView(
                                            cartItem = it,
                                            onDecrementClick = { cartItem, count ->
                                                viewModel.decrementQuantity(cartItem)
                                            },
                                            onIncrementClick = { cartItem, count ->
                                                viewModel.incrementQuantity(cartItem)
                                            },
                                            onRemove = { cartItem ->
                                                viewModel.removeItem(cartItem)
                                            }
                                        )
                                    }

                                }

                                Box(
                                    modifier = Modifier
                                        .clip(
                                            shape = RoundedCornerShape(
                                                topStart = 16.dp,
                                                topEnd = 16.dp
                                            )
                                        )
                                        .height(300.dp)
                                        .background(Color.LightGray)
                                        .padding(vertical = 16.dp)
                                        .align(Alignment.BottomCenter)
                                ) {
                                    Column {
                                        CheckoutDetailsView(data.checkoutDetails)
                                        //Spacer(modifier = Modifier.height(8.dp))
                                        AddressCard(
                                            selectedAddress.value,
                                            onAddressClicked = { viewModel.onAddressClicked() }
                                        )
                                        //Spacer(modifier = Modifier.height(8.dp))
                                        Button(
                                            onClick = { viewModel.checkOut() },
                                            enabled = selectedAddress.value != null,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(text = "Check Out")
                                        }
                                    }
                                }

                            }

                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_cart),
                                contentDescription = null
                            )
                            Text(
                                text = "No items in cart",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )
                            Spacer(modifier = Modifier.size(16.dp))
                            Button(
                                onClick = { navController.navigateUp() },
                            ) {
                                Text(text = "Continue Shopping")
                            }
                        }
                    }
                }

            }

        }
    }

    if (showErrorDialog.value) {
        ModalBottomSheet(
            onDismissRequest = { showErrorDialog.value = false}
        ) {
            BasicDialog(
                title = viewModel.errorTitle,
                description = viewModel.errorMessage
            ) {
                showErrorDialog.value = false
            }
        }
    }
}


@Composable
fun CartItemView(
    cartItem: CartItem,
    onDecrementClick: (cartItem: CartItem, count: Int) -> Unit,
    onIncrementClick: (cartItem: CartItem, count: Int) -> Unit,
    onRemove : (cartItem: CartItem) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = cartItem.menuItemId.imageUrl,
            contentDescription = null,
            modifier = Modifier
                .size(70.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cartItem.menuItemId.name,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onRemove(cartItem) },
                ) {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Text(
                text = cartItem.menuItemId.description,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$${cartItem.menuItemId.price}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.weight(1f))
                FoodItemCounter(
                    count = cartItem.quantity,
                    onDecrementClick = { onDecrementClick.invoke(cartItem, cartItem.quantity) },
                    onIncrementClick = { onIncrementClick(cartItem, cartItem.quantity) }
                )
            }
        }

    }
}


@Composable
fun CheckoutDetailsView(checkoutDetails: CheckoutDetails) {
    Column(modifier = Modifier
        .height(80.dp)
        .fillMaxWidth()){
        CheckoutDetailsItem(title = "Sub Total", value = checkoutDetails.subTotal, currency = "USD")
        CheckoutDetailsItem(title = "Delivery Fee", value = checkoutDetails.deliveryFee, currency = "USD")
        CheckoutDetailsItem(title = "Tax", value = checkoutDetails.tax, currency = "USD")
        CheckoutDetailsItem(title = "Total", value = checkoutDetails.totalAmount, currency = "USD")
    }
}

@Composable
fun CheckoutDetailsItem(title: String, value: Double, currency: String) {
    Column(modifier = Modifier
        .height(20.dp)
        .fillMaxWidth()){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(text = title, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = StringUtils.formatCurrency(value),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold
            )
            Text(text = currency, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
        }
        VerticalDivider()
    }
}

@Composable
fun FoodItemCounter(
    count : Int,
    onIncrementClick : () -> Unit,
    onDecrementClick : () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_minus),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .clickable { onDecrementClick.invoke() }
        )
        Text(
            text = "$count",
            style = MaterialTheme.typography.titleMedium
        )
        Image(
            painter = painterResource(id = R.drawable.ic_add),
            contentDescription = null,
            modifier = Modifier
                .size(60.dp)
                .clip(CircleShape)
                .clickable { onIncrementClick.invoke() }
        )
    }
}

@Composable
fun AddressCard(
    address: Address?,
    onAddressClicked: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clip(RoundedCornerShape(16.dp))
            .shadow(8.dp)
            .background(Color.White)
            .clickable {
                onAddressClicked()
            }
            .padding(8.dp)

    ) {
        if (address != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = address.addressLine1,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.size(4.dp))
                Text(
                    text = "${address.city}, ${address.state}, ${address.country}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Rounded.AddCircle,
                    contentDescription = "Add Address",
                    tint = MaterialTheme.colorScheme.primary,
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "Add Address",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}