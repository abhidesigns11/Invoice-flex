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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.data.model.InvoiceType
import com.example.data.model.Item
import com.example.data.model.Party
import com.example.data.model.PartyType
import com.example.data.model.PaymentMode
import com.example.ui.components.Formatters
import com.example.ui.components.InvoiceTypeBadge
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel
import com.example.ui.viewmodel.InvoiceDraftItem
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateEditInvoiceScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit,
    onInvoiceSaved: (Long) -> Unit
) {
    val draft = state.invoiceDraft

    var showPartyPickerSheet by remember { mutableStateOf(false) }
    var showItemPickerSheet by remember { mutableStateOf(false) }
    var showCustomItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<InvoiceDraftItem?>(null) }

    val partySheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val itemSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Create ${draft.invoiceType.label}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = draft.invoiceNumber,
                            style = MaterialTheme.typography.bodySmall,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("create_invoice_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
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
                    Column {
                        Text(
                            text = "Grand Total",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = Formatters.formatCurrency(draft.calculatedGrandTotal),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryBlue
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.saveDraftInvoice { newId ->
                                onInvoiceSaved(newId)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .height(48.dp)
                            .testTag("save_and_preview_invoice_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Save & Generate", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Customer / Party Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("party_selection_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (draft.invoiceType == InvoiceType.PURCHASE_INVOICE) "Supplier Details" else "Customer Details",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                            OutlinedButton(
                                onClick = { showPartyPickerSheet = true },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_select_party")
                            ) {
                                Text(if (draft.partyName.isBlank()) "Select Party" else "Change", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (draft.partyName.isNotBlank()) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryBlueContainer),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = PrimaryBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = draft.partyName,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (draft.partyPhone.isNotBlank()) {
                                        Text(
                                            text = "Phone: ${draft.partyPhone}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    if (draft.partyGstin.isNotBlank()) {
                                        Text(
                                            text = "GSTIN: ${draft.partyGstin}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = PrimaryBlue,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        } else {
                            // Quick inputs if not selected from party catalog
                            OutlinedTextField(
                                value = draft.partyName,
                                onValueChange = { viewModel.setDraftPartyCustom(it, draft.partyPhone, draft.partyGstin, draft.partyAddress) },
                                label = { Text("Customer / Party Name *") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_customer_name"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = draft.partyPhone,
                                onValueChange = { viewModel.setDraftPartyCustom(draft.partyName, it, draft.partyGstin, draft.partyAddress) },
                                label = { Text("Mobile Number (Optional)") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_customer_phone"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            }

            // Invoice Settings (Number, Interstate IGST toggle)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Invoice Settings",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = draft.invoiceNumber,
                                onValueChange = { viewModel.updateDraftDetails(invoiceNumber = it) },
                                label = { Text("Bill Number") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_invoice_number"),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = Formatters.formatShortDate(draft.date),
                                onValueChange = {},
                                label = { Text("Bill Date") },
                                readOnly = true,
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Interstate GST Toggle
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Inter-State Supply (IGST)",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (draft.isInterState) "Applies 100% IGST" else "Applies 50% CGST + 50% SGST",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                            Switch(
                                checked = draft.isInterState,
                                onCheckedChange = { viewModel.updateDraftDetails(isInterState = it) },
                                colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
                            )
                        }
                    }
                }
            }

            // Items Section
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Items & Products (${draft.items.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { showItemPickerSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlueContainer, contentColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_add_item_catalog")
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("From Catalog", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                editingItem = null
                                showCustomItemDialog = true
                            },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_add_custom_item")
                        ) {
                            Text("+ Custom Item", fontSize = 12.sp)
                        }
                    }
                }
            }

            // Line Items List
            if (draft.items.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No items added yet",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Add products from catalog or enter custom line items",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                items(draft.items, key = { it.tempId }) { draftItem ->
                    DraftItemCard(
                        item = draftItem,
                        onEdit = {
                            editingItem = draftItem
                            showCustomItemDialog = true
                        },
                        onDelete = { viewModel.removeDraftItem(draftItem.tempId) },
                        onQtyChange = { newQty ->
                            if (newQty > 0) {
                                viewModel.updateDraftItem(draftItem.tempId, draftItem.copy(quantity = newQty))
                            }
                        }
                    )
                }
            }

            // Calculation Breakdown Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Bill Summary",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Taxable Subtotal", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = Formatters.formatCurrency(draft.subTotal), fontWeight = FontWeight.Medium)
                        }

                        if (!draft.isInterState) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "CGST", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = Formatters.formatCurrency(draft.cgstAmount), fontWeight = FontWeight.Medium)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "SGST", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = Formatters.formatCurrency(draft.sgstAmount), fontWeight = FontWeight.Medium)
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "IGST (Inter-State)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = Formatters.formatCurrency(draft.igstAmount), fontWeight = FontWeight.Medium)
                            }
                        }

                        // Additional Discount Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "Special Discount (₹)", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            OutlinedTextField(
                                value = if (draft.discountAmount == 0.0) "" else draft.discountAmount.toString(),
                                onValueChange = {
                                    val amount = it.toDoubleOrNull() ?: 0.0
                                    viewModel.updateDraftDetails(discountAmount = amount)
                                },
                                placeholder = { Text("0.0") },
                                modifier = Modifier
                                    .width(110.dp)
                                    .height(52.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Grand Total",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = Formatters.formatCurrency(draft.calculatedGrandTotal),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue
                            )
                        }
                    }
                }
            }

            // Payment Settlement Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Payment Settlement",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick buttons: "Full Paid", "Unpaid / Credit", "Partial"
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    viewModel.updateDraftDetails(paidAmount = draft.calculatedGrandTotal)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = if (draft.balanceDue <= 0.01) ButtonDefaults.outlinedButtonColors(containerColor = SuccessGreenLight) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("Full Paid", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    viewModel.updateDraftDetails(paidAmount = 0.0)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                colors = if (draft.paidAmount == 0.0) ButtonDefaults.outlinedButtonColors(containerColor = DangerRedLight) else ButtonDefaults.outlinedButtonColors()
                            ) {
                                Text("Unpaid (Udhaar)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = if (draft.paidAmount == 0.0) "0" else draft.paidAmount.toString(),
                                onValueChange = {
                                    val paid = it.toDoubleOrNull() ?: 0.0
                                    viewModel.updateDraftDetails(paidAmount = paid)
                                },
                                label = { Text("Amount Paid (₹)") },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_paid_amount"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                shape = RoundedCornerShape(10.dp)
                            )

                            // Payment Mode Selector
                            PaymentModeSelectorDropdown(
                                selected = draft.paymentMode,
                                onSelect = { viewModel.updateDraftDetails(paymentMode = it) },
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Balance Due",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = Formatters.formatCurrency(draft.balanceDue),
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (draft.balanceDue > 0) DangerRed else SuccessGreen
                            )
                        }
                    }
                }
            }

            // Optional Transport & Notes
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Additional Details (Optional)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            OutlinedTextField(
                                value = draft.eWayBillNo,
                                onValueChange = { viewModel.updateDraftDetails(eWayBillNo = it) },
                                label = { Text("E-Way Bill #") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )

                            OutlinedTextField(
                                value = draft.vehicleNo,
                                onValueChange = { viewModel.updateDraftDetails(vehicleNo = it) },
                                label = { Text("Vehicle #") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = draft.notes,
                            onValueChange = { viewModel.updateDraftDetails(notes = it) },
                            label = { Text("Remarks / Notes for customer") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 2,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }

    // Party Picker Modal Bottom Sheet
    if (showPartyPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPartyPickerSheet = false },
            sheetState = partySheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Party",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                val targetType = if (draft.invoiceType == InvoiceType.PURCHASE_INVOICE) PartyType.SUPPLIER else PartyType.CUSTOMER
                val filteredParties = state.allParties.filter { it.partyType == targetType }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredParties) { party ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setDraftParty(party)
                                    showPartyPickerSheet = false
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = party.name, fontWeight = FontWeight.Bold)
                                    Text(text = party.phone, style = MaterialTheme.typography.bodySmall)
                                    if (party.gstin.isNotBlank()) {
                                        Text(text = "GSTIN: ${party.gstin}", style = MaterialTheme.typography.labelSmall, color = PrimaryBlue)
                                    }
                                }
                                Text(
                                    text = Formatters.formatCurrency(party.currentBalance),
                                    fontWeight = FontWeight.Bold,
                                    color = if (party.currentBalance >= 0) SuccessGreen else DangerRed
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Catalog Item Picker Bottom Sheet
    if (showItemPickerSheet) {
        ModalBottomSheet(
            onDismissRequest = { showItemPickerSheet = false },
            sheetState = itemSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Select Product / Service",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(400.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.allItems) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.addDraftItemFromCatalog(item)
                                    showItemPickerSheet = false
                                },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = item.name, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "Stock: ${item.currentStock.toInt()} ${item.unit} • GST ${item.taxRate.toInt()}% • HSN ${item.hsnCode}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = Formatters.formatCurrency(item.salePrice),
                                        fontWeight = FontWeight.Bold,
                                        color = PrimaryBlue
                                    )
                                    Text(
                                        text = "+ Add",
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Custom Item Entry Dialog
    if (showCustomItemDialog) {
        CustomItemEditDialog(
            initial = editingItem,
            onDismiss = { showCustomItemDialog = false },
            onSave = { newItem ->
                if (editingItem != null) {
                    viewModel.updateDraftItem(editingItem!!.tempId, newItem)
                } else {
                    viewModel.addDraftCustomItem(newItem)
                }
                showCustomItemDialog = false
            }
        )
    }
}

@Composable
fun DraftItemCard(
    item: InvoiceDraftItem,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onQtyChange: (Double) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.itemName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )

                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Text(
                text = "Rate: ${Formatters.formatCurrency(item.unitPrice)} • GST ${item.taxRate.toInt()}% • HSN ${item.hsnCode}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Stepper
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                ) {
                    IconButton(
                        onClick = { onQtyChange(item.quantity - 1.0) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("-", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Text(
                        text = "${item.quantity.toInt()} ${item.unit}",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                    IconButton(
                        onClick = { onQtyChange(item.quantity + 1.0) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Text("+", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                Text(
                    text = Formatters.formatCurrency(item.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentModeSelectorDropdown(
    selected: PaymentMode,
    onSelect: (PaymentMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Payment Mode") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor(),
            shape = RoundedCornerShape(10.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            PaymentMode.values().forEach { mode ->
                DropdownMenuItem(
                    text = { Text(mode.label) },
                    onClick = {
                        onSelect(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomItemEditDialog(
    initial: InvoiceDraftItem?,
    onDismiss: () -> Unit,
    onSave: (InvoiceDraftItem) -> Unit
) {
    var name by remember { mutableStateOf(initial?.itemName ?: "") }
    var hsn by remember { mutableStateOf(initial?.hsnCode ?: "8517") }
    var qty by remember { mutableStateOf(initial?.quantity?.toString() ?: "1") }
    var unit by remember { mutableStateOf(initial?.unit ?: "Pcs") }
    var rate by remember { mutableStateOf(initial?.unitPrice?.toString() ?: "") }
    var taxRate by remember { mutableStateOf(initial?.taxRate?.toString() ?: "18") }
    var discount by remember { mutableStateOf(initial?.discountPercent?.toString() ?: "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add Custom Item" else "Edit Item", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Item Name *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = rate,
                        onValueChange = { rate = it },
                        label = { Text("Unit Price (₹) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = qty,
                        onValueChange = { qty = it },
                        label = { Text("Quantity") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = taxRate,
                        onValueChange = { taxRate = it },
                        label = { Text("GST % (0, 5, 12, 18, 28)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = hsn,
                        onValueChange = { hsn = it },
                        label = { Text("HSN Code") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank() && rate.isNotBlank()) {
                        val newItem = InvoiceDraftItem(
                            tempId = initial?.tempId ?: java.util.UUID.randomUUID().toString(),
                            itemId = initial?.itemId,
                            itemName = name,
                            hsnCode = hsn,
                            quantity = qty.toDoubleOrNull() ?: 1.0,
                            unit = unit,
                            unitPrice = rate.toDoubleOrNull() ?: 0.0,
                            taxRate = taxRate.toDoubleOrNull() ?: 18.0,
                            discountPercent = discount.toDoubleOrNull() ?: 0.0
                        )
                        onSave(newItem)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Item")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
