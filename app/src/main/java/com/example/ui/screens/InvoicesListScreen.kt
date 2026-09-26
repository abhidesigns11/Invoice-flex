package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvoiceType
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.PaymentStatus
import com.example.ui.components.Formatters
import com.example.ui.components.InvoiceTypeBadge
import com.example.ui.components.PaymentModeBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.BillingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListScreen(
    state: BillingUiState,
    onNavigateToCreateInvoice: (InvoiceType) -> Unit,
    onNavigateToInvoiceDetail: (Long) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTypeFilter by remember { mutableStateOf<InvoiceType?>(null) }
    var selectedStatusFilter by remember { mutableStateOf<PaymentStatus?>(null) }

    val filteredInvoices = remember(state.allInvoices, searchQuery, selectedTypeFilter, selectedStatusFilter) {
        state.allInvoices.filter { item ->
            val inv = item.invoice
            val matchesQuery = searchQuery.isBlank() ||
                    inv.invoiceNumber.contains(searchQuery, ignoreCase = true) ||
                    inv.partyName.contains(searchQuery, ignoreCase = true) ||
                    inv.partyPhone.contains(searchQuery, ignoreCase = true)
            val matchesType = selectedTypeFilter == null || inv.invoiceType == selectedTypeFilter
            val matchesStatus = selectedStatusFilter == null || inv.paymentStatus == selectedStatusFilter
            matchesQuery && matchesType && matchesStatus
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Invoices & Billing",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateInvoice(selectedTypeFilter ?: InvoiceType.SALE_INVOICE) },
                containerColor = PrimaryBlue,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("invoices_fab_add")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "New Bill", fontWeight = FontWeight.Bold)
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
                    .testTag("invoice_search_input"),
                placeholder = { Text("Search by bill #, customer, phone...") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
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
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Invoice Type Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = selectedTypeFilter == null,
                        onClick = { selectedTypeFilter = null },
                        label = { Text("All Bills (${state.allInvoices.size})") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlueContainer,
                            selectedLabelColor = PrimaryBlue
                        )
                    )
                }
                InvoiceType.values().forEach { type ->
                    item {
                        val count = state.allInvoices.count { it.invoice.invoiceType == type }
                        FilterChip(
                            selected = selectedTypeFilter == type,
                            onClick = { selectedTypeFilter = if (selectedTypeFilter == type) null else type },
                            label = { Text("${type.label} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlueContainer,
                                selectedLabelColor = PrimaryBlue
                            )
                        )
                    }
                }
            }

            // Status Filter Row (Paid / Unpaid)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusFilterTag(
                    label = "All Status",
                    isSelected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null }
                )
                StatusFilterTag(
                    label = "Unpaid / Due",
                    isSelected = selectedStatusFilter == PaymentStatus.UNPAID,
                    onClick = {
                        selectedStatusFilter = if (selectedStatusFilter == PaymentStatus.UNPAID) null else PaymentStatus.UNPAID
                    }
                )
                StatusFilterTag(
                    label = "Fully Paid",
                    isSelected = selectedStatusFilter == PaymentStatus.PAID,
                    onClick = {
                        selectedStatusFilter = if (selectedStatusFilter == PaymentStatus.PAID) null else PaymentStatus.PAID
                    }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Invoices List
            if (filteredInvoices.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No matching bills found" else "No bills in this category",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap '+ New Bill' to create a GST/Non-GST invoice",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredInvoices, key = { it.invoice.id }) { item ->
                        DetailedInvoiceCard(
                            invoiceWithDetails = item,
                            onClick = { onNavigateToInvoiceDetail(item.invoice.id) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(70.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun StatusFilterTag(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant,
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun DetailedInvoiceCard(
    invoiceWithDetails: InvoiceWithDetails,
    onClick: () -> Unit
) {
    val invoice = invoiceWithDetails.invoice
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("detailed_invoice_card_${invoice.id}")
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    InvoiceTypeBadge(type = invoice.invoiceType)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = invoice.invoiceNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                PaymentStatusBadge(status = invoice.paymentStatus)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = invoice.partyName,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    if (invoice.partyPhone.isNotBlank()) {
                        Text(
                            text = "Ph: ${invoice.partyPhone}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                    Text(
                        text = "Date: ${Formatters.formatShortDate(invoice.date)} • ${invoiceWithDetails.items.size} item(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = Formatters.formatCurrency(invoice.grandTotal),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (invoice.balanceDue > 0) {
                        Text(
                            text = "Due: ${Formatters.formatCurrency(invoice.balanceDue)}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = DangerRed
                        )
                    } else {
                        PaymentModeBadge(mode = invoice.paymentMode)
                    }
                }
            }
        }
    }
}
