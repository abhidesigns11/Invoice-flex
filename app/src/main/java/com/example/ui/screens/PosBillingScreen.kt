package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Money
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Item
import com.example.data.model.PaymentMode
import com.example.ui.components.Formatters
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SecondaryTeal
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel
import com.example.ui.viewmodel.PosCartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PosBillingScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit,
    onCheckoutSuccess: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var showCartSheet by remember { mutableStateOf(false) }
    val cartSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val cart = state.posCart
    val totalCartItems = cart.sumOf { it.quantity.toInt() }
    val cartTotal = cart.sumOf { it.totalAmount }

    val filteredItems = remember(state.allItems, searchQuery, selectedCategory) {
        state.allItems.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.itemCode.contains(searchQuery, ignoreCase = true) ||
                    item.barcode.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            matchesSearch && matchesCategory
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.PointOfSale, contentDescription = null, tint = PrimaryBlue)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("POS Quick Counter", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("pos_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (cart.isNotEmpty()) {
                        IconButton(onClick = { viewModel.clearPosCart() }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear Cart", tint = DangerRed)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
                Surface(
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp,
                    shadowElevation = 8.dp,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.clickable { showCartSheet = true }
                        ) {
                            Text(
                                text = "$totalCartItems item(s) in Cart",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatCurrency(cartTotal),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }

                        Button(
                            onClick = { showCartSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .height(48.dp)
                                .testTag("btn_pos_view_cart")
                        ) {
                            Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Review & Pay", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("pos_search_input"),
                placeholder = { Text("Search item, barcode or SKU...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null)
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryBlue,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedCategory == "All",
                        onClick = { selectedCategory = "All" },
                        label = { Text("All (${state.allItems.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlueContainer,
                            selectedLabelColor = PrimaryBlue
                        )
                    )
                }
                state.categories.forEach { cat ->
                    item {
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlueContainer,
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // POS Items Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredItems, key = { it.id }) { item ->
                    val cartItem = cart.find { it.item.id == item.id }
                    PosItemGridCard(
                        item = item,
                        quantityInCart = cartItem?.quantity ?: 0.0,
                        onAdd = { viewModel.addPosCartItem(item) },
                        onMinus = { viewModel.updatePosCartItemQty(item.id, (cartItem?.quantity ?: 1.0) - 1.0) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

    // POS Cart & Instant Checkout Bottom Sheet
    if (showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { showCartSheet = false },
            sheetState = cartSheetState
        ) {
            PosCheckoutBottomSheetContent(
                cart = cart,
                totalAmount = cartTotal,
                customerName = state.posCustomerName,
                customerPhone = state.posCustomerPhone,
                tenderedCash = state.posTenderedCash,
                onCustomerChange = { name, phone -> viewModel.setPosCustomer(name, phone) },
                onTenderedCashChange = { viewModel.setPosTenderedCash(it) },
                onQtyChange = { itemId, qty -> viewModel.updatePosCartItemQty(itemId, qty) },
                onCheckout = { mode ->
                    viewModel.checkoutPosBill(mode) { newId ->
                        showCartSheet = false
                        onCheckoutSuccess(newId)
                    }
                }
            )
        }
    }
}

@Composable
fun PosItemGridCard(
    item: Item,
    quantityInCart: Double,
    onAdd: () -> Unit,
    onMinus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAdd() }
            .testTag("pos_item_${item.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (quantityInCart > 0) PrimaryBlueContainer.copy(alpha = 0.4f) else MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = item.category,
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (quantityInCart > 0) {
                    Surface(
                        color = PrimaryBlue,
                        shape = CircleShape
                    ) {
                        Text(
                            text = "${quantityInCart.toInt()}",
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                modifier = Modifier.height(40.dp)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Stock: ${item.currentStock.toInt()} ${item.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = if (item.currentStock <= item.minStockAlert) DangerRed else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = Formatters.formatCurrency(item.salePrice, showDecimals = false),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue
                )

                if (quantityInCart > 0) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                            .border(1.dp, PrimaryBlue, RoundedCornerShape(8.dp))
                    ) {
                        IconButton(onClick = onMinus, modifier = Modifier.size(26.dp)) {
                            Text("-", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                        Text(
                            text = "${quantityInCart.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                        IconButton(onClick = onAdd, modifier = Modifier.size(26.dp)) {
                            Text("+", fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PosCheckoutBottomSheetContent(
    cart: List<PosCartItem>,
    totalAmount: Double,
    customerName: String,
    customerPhone: String,
    tenderedCash: Double,
    onCustomerChange: (String, String) -> Unit,
    onTenderedCashChange: (Double) -> Unit,
    onQtyChange: (Long, Double) -> Unit,
    onCheckout: (PaymentMode) -> Unit
) {
    val changeToReturn = (tenderedCash - totalAmount).coerceAtLeast(0.0)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "POS Order Checkout",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(10.dp))

        // Customer Quick Inputs
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = customerName,
                onValueChange = { onCustomerChange(it, customerPhone) },
                label = { Text("Customer Name") },
                modifier = Modifier.weight(1.2f),
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value = customerPhone,
                onValueChange = { onCustomerChange(customerName, it) },
                label = { Text("Mobile #") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                shape = RoundedCornerShape(8.dp)
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Cart Items List
        Text(text = "Cart Items (${cart.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(cart, key = { it.item.id }) { cItem ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = cItem.item.name, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, maxLines = 1)
                        Text(text = "${Formatters.formatCurrency(cItem.customPrice)} x ${cItem.quantity.toInt()}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { onQtyChange(cItem.item.id, cItem.quantity - 1.0) }, modifier = Modifier.size(24.dp)) {
                            Text("-", fontWeight = FontWeight.Bold)
                        }
                        Text(text = "${cItem.quantity.toInt()}", fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp))
                        IconButton(onClick = { onQtyChange(cItem.item.id, cItem.quantity + 1.0) }, modifier = Modifier.size(24.dp)) {
                            Text("+", fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(10.dp))
                        Text(text = Formatters.formatCurrency(cItem.totalAmount), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        // Cash Tendered & Change Return Calculator
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Cash Received (₹):", fontWeight = FontWeight.Medium, fontSize = 12.sp)
                    OutlinedTextField(
                        value = if (tenderedCash == 0.0) "" else tenderedCash.toString(),
                        onValueChange = { onTenderedCashChange(it.toDoubleOrNull() ?: 0.0) },
                        placeholder = { Text("e.g. 2000") },
                        modifier = Modifier
                            .width(120.dp)
                            .height(48.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                if (tenderedCash > 0) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Change to Return:", fontWeight = FontWeight.Bold, color = SuccessGreen)
                        Text(text = Formatters.formatCurrency(changeToReturn), fontWeight = FontWeight.Bold, color = SuccessGreen, fontSize = 14.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Total & Payment Mode Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Net Payable:", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Text(text = Formatters.formatCurrency(totalAmount), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = PrimaryBlue)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { onCheckout(PaymentMode.CASH) },
                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pos_pay_cash")
            ) {
                Icon(imageVector = Icons.Default.Money, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Cash", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onCheckout(PaymentMode.UPI) },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pos_pay_upi")
            ) {
                Icon(imageVector = Icons.Default.QrCodeScanner, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("UPI QR", fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = { onCheckout(PaymentMode.CREDIT) },
                colors = ButtonDefaults.buttonColors(containerColor = SecondaryTeal),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("pos_pay_credit")
            ) {
                Text("Udhaar", fontWeight = FontWeight.Bold)
            }
        }
    }
}
