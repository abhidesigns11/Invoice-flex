package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.Party
import com.example.data.model.PartyType
import com.example.data.model.PaymentStatus
import com.example.ui.components.AccountProfileSheet
import com.example.ui.components.Formatters
import com.example.ui.components.SupplierBillScannerDialog
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PureWhite
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartiesScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateToPartyDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToInvoiceDetail: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    // Filter options: "ALL", "CUSTOMERS", "SUPPLIERS", "TO_COLLECT", "TO_PAY"
    var selectedFilter by remember { mutableStateOf("ALL") }
    var showCreatePartyDialog by remember { mutableStateOf(false) }
    var showBillScannerDialog by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var isSearchActive by remember { mutableStateOf(false) }

    val filteredParties = state.allParties.filter { party ->
        val matchesSearch = party.name.contains(searchQuery, ignoreCase = true) ||
                party.phone.contains(searchQuery, ignoreCase = true) ||
                party.gstin.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (selectedFilter) {
            "CUSTOMERS" -> party.partyType == PartyType.CUSTOMER
            "SUPPLIERS" -> party.partyType == PartyType.SUPPLIER
            "TO_COLLECT" -> party.currentBalance > 0
            "TO_PAY" -> party.currentBalance < 0
            else -> true
        }
        matchesSearch && matchesFilter
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearchActive) {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search company, GSTIN or phone...") },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            singleLine = true
                        )
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                "Parties & Companies",
                                fontSize = 19.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A)
                            )
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFFE2E8F0)
                            ) {
                                Text(
                                    "${filteredParties.size}",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF334155)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { isSearchActive = !isSearchActive }) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF475569))
                    }

                    // Scan Supplier Bill Photo Quick Button
                    IconButton(
                        onClick = { showBillScannerDialog = true },
                        modifier = Modifier.testTag("topbar_scan_supplier_bill_btn")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Scan Bill", tint = PrimaryCobalt, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    // Top-right Account Profile Avatar (Universal in all apps)
                    IconButton(
                        onClick = { showAccountSheet = true },
                        modifier = Modifier.testTag("top_right_account_btn")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0A2540),
                            modifier = Modifier.size(34.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    state.profile?.businessName?.take(1)?.uppercase() ?: "A",
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                color = PureWhite,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Scan Supplier Bill (Photo) - Core User Feature
                    Button(
                        onClick = { showBillScannerDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(46.dp)
                            .testTag("bottom_scan_supplier_bill_btn")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.CameraAlt, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Supplier Bill", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        }
                    }

                    // + Create Party
                    Button(
                        onClick = { showCreatePartyDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(46.dp)
                            .testTag("create_party_button")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = PureWhite, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Add Party", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Filter Pills: All, Customers, Suppliers, To Collect, To Pay
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(PureWhite)
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item {
                        FilterChipPill(
                            label = "All Parties",
                            isSelected = selectedFilter == "ALL",
                            onClick = { selectedFilter = "ALL" }
                        )
                    }
                    item {
                        FilterChipPill(
                            label = "Customers",
                            isSelected = selectedFilter == "CUSTOMERS",
                            onClick = { selectedFilter = if (selectedFilter == "CUSTOMERS") "ALL" else "CUSTOMERS" }
                        )
                    }
                    item {
                        FilterChipPill(
                            label = "Suppliers 🏭",
                            isSelected = selectedFilter == "SUPPLIERS",
                            onClick = { selectedFilter = if (selectedFilter == "SUPPLIERS") "ALL" else "SUPPLIERS" }
                        )
                    }
                    item {
                        FilterChipPill(
                            label = "To Collect",
                            isSelected = selectedFilter == "TO_COLLECT",
                            onClick = { selectedFilter = if (selectedFilter == "TO_COLLECT") "ALL" else "TO_COLLECT" }
                        )
                    }
                    item {
                        FilterChipPill(
                            label = "To Pay",
                            isSelected = selectedFilter == "TO_PAY",
                            onClick = { selectedFilter = if (selectedFilter == "TO_PAY") "ALL" else "TO_PAY" }
                        )
                    }
                }
            }

            // Quick Banner: Scan Supplier Bill (Photo) Feature Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clickable { showBillScannerDialog = true },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0A2540))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(16.dp))
                                Text(
                                    "Scan Supplier Bill with Photo",
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                "Auto-detects supplier name & groups all bills into individual company invoices with 1-month payment reminder.",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                lineHeight = 15.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0284C7),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.CameraAlt, contentDescription = "Scan", tint = PureWhite, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }

            // List of Parties & Grouped Invoices
            items(filteredParties) { party ->
                // Filter all invoices/bills belonging to this party
                val partyInvoices = state.allInvoices.filter { it.invoice.partyId == party.id }

                PartyListItemCard(
                    party = party,
                    invoices = partyInvoices,
                    onClick = {
                        viewModel.selectPartyById(party.id)
                        onNavigateToPartyDetail(party.id)
                    },
                    onInvoiceClick = { invId ->
                        viewModel.selectInvoiceById(invId)
                        onNavigateToInvoiceDetail(invId)
                    }
                )
            }

            if (filteredParties.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PeopleOutline, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(48.dp))
                            Text("No parties found matching criteria", color = Color(0xFF64748B), fontWeight = FontWeight.Medium)
                            OutlinedButton(onClick = { selectedFilter = "ALL"; searchQuery = "" }) {
                                Text("Reset Filters")
                            }
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(90.dp)) }
        }
    }

    // Add Party Dialog
    if (showCreatePartyDialog) {
        AddEditPartyDialog(
            defaultType = if (selectedFilter == "SUPPLIERS") PartyType.SUPPLIER else PartyType.CUSTOMER,
            onDismiss = { showCreatePartyDialog = false },
            onSave = { newParty ->
                viewModel.saveParty(newParty)
                showCreatePartyDialog = false
            }
        )
    }

    // Supplier Bill Scanner Dialog (Photo OCR & 1-Month Reminder)
    if (showBillScannerDialog) {
        SupplierBillScannerDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showBillScannerDialog = false },
            onBillSaved = { partyId ->
                viewModel.selectPartyById(partyId)
                onNavigateToPartyDetail(partyId)
            }
        )
    }

    // Top-Right Corner Account Profile Sheet
    if (showAccountSheet) {
        AccountProfileSheet(
            state = state,
            onDismiss = { showAccountSheet = false },
            onNavigateToSettings = onNavigateToSettings
        )
    }
}

@Composable
fun FilterChipPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) Color(0xFF0F172A) else Color(0xFFF1F5F9),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PureWhite else Color(0xFF475569),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
        )
    }
}

@Composable
fun PartyListItemCard(
    party: Party,
    invoices: List<InvoiceWithDetails> = emptyList(),
    onClick: () -> Unit,
    onInvoiceClick: ((Long) -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 5.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(1.5.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Surface(
                        color = if (party.partyType == PartyType.CUSTOMER) Color(0xFFEFF6FF) else Color(0xFFFEF3C7),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.size(42.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                party.name.take(2).uppercase(),
                                fontWeight = FontWeight.Bold,
                                color = if (party.partyType == PartyType.CUSTOMER) Color(0xFF2563EB) else Color(0xFFD97706),
                                fontSize = 14.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                party.name,
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0F172A),
                                maxLines = 1
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (party.partyType == PartyType.CUSTOMER) Color(0xFFEFF6FF) else Color(0xFFFEF3C7)
                            ) {
                                Text(
                                    party.partyType.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (party.partyType == PartyType.CUSTOMER) Color(0xFF2563EB) else Color(0xFFD97706),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            "${party.city.ifBlank { "Maharashtra" }} • ${party.phone}",
                            fontSize = 11.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val bal = party.currentBalance
                    Text(
                        Formatters.formatCurrency(kotlin.math.abs(bal)),
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            bal > 0 -> Color(0xFF059669) // Green (To Collect)
                            bal < 0 -> Color(0xFFDC2626) // Red (To Pay)
                            else -> Color(0xFF64748B)
                        }
                    )
                    Text(
                        when {
                            bal > 0 -> "To Collect"
                            bal < 0 -> "To Pay"
                            else -> "Settled"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when {
                            bal > 0 -> Color(0xFF059669)
                            bal < 0 -> Color(0xFFDC2626)
                            else -> Color(0xFF64748B)
                        }
                    )
                }
            }

            // Invoices Grouping Preview Section
            // "if I make invoice of one same company so they show one name of company/party but inside them show two diff/individual invoice"
            if (invoices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = Color(0xFFF1F5F9))
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = Color(0xFF64748B), modifier = Modifier.size(13.dp))
                        Text(
                            "${invoices.size} Individual ${if (party.partyType == PartyType.SUPPLIER) "Bills" else "Invoices"} Grouped Inside",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                    }
                    Text(
                        "Manage Khata ➜",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryCobalt
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Show up to 2 individual invoices inside this company
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    invoices.take(2).forEach { invItem ->
                        val inv = invItem.invoice
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFF8FAFC),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    if (onInvoiceClick != null) onInvoiceClick(inv.id) else onClick()
                                }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        inv.invoiceNumber,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E293B)
                                    )
                                    if (inv.billPhotoUri != null) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFEEF2FF)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(10.dp))
                                                Text("Bill Photo", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                                            }
                                        }
                                    }
                                    if (inv.hasPaymentReminder) {
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = Color(0xFFFFFBEB)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                                            ) {
                                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(10.dp))
                                                Text("1M Remind", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                            }
                                        }
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        "₹${Formatters.formatCurrency(inv.grandTotal)}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0F172A)
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = when (inv.paymentStatus) {
                                            PaymentStatus.PAID -> Color(0xFFDCFCE7)
                                            PaymentStatus.PARTIAL -> Color(0xFFFEF3C7)
                                            PaymentStatus.UNPAID, PaymentStatus.OVERDUE -> Color(0xFFFEE2E2)
                                        }
                                    ) {
                                        Text(
                                            inv.paymentStatus.label,
                                            fontSize = 8.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = when (inv.paymentStatus) {
                                                PaymentStatus.PAID -> Color(0xFF15803D)
                                                PaymentStatus.PARTIAL -> Color(0xFFB45309)
                                                PaymentStatus.UNPAID, PaymentStatus.OVERDUE -> Color(0xFFB91C1C)
                                            },
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AddEditPartyDialog(
    initialParty: Party? = null,
    defaultType: PartyType = PartyType.CUSTOMER,
    onDismiss: () -> Unit,
    onSave: (Party) -> Unit
) {
    var name by remember { mutableStateOf(initialParty?.name ?: "") }
    var phone by remember { mutableStateOf(initialParty?.phone ?: "") }
    var gstin by remember { mutableStateOf(initialParty?.gstin ?: "") }
    var address by remember { mutableStateOf(initialParty?.address ?: "") }
    var city by remember { mutableStateOf(initialParty?.city ?: "Pune") }
    var partyType by remember { mutableStateOf(initialParty?.partyType ?: defaultType) }
    var openingBalance by remember { mutableStateOf(if (initialParty != null) initialParty.openingBalance.toString() else "0") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                if (initialParty == null) "Add New Company / Party" else "Edit Party",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { partyType = PartyType.CUSTOMER }
                    ) {
                        RadioButton(
                            selected = partyType == PartyType.CUSTOMER,
                            onClick = { partyType = PartyType.CUSTOMER }
                        )
                        Text("Customer", fontSize = 13.sp)
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { partyType = PartyType.SUPPLIER }
                    ) {
                        RadioButton(
                            selected = partyType == PartyType.SUPPLIER,
                            onClick = { partyType = PartyType.SUPPLIER }
                        )
                        Text("Supplier 🏭", fontSize = 13.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Company / Party Name *") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone / WhatsApp *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = gstin,
                    onValueChange = { gstin = it },
                    label = { Text("GSTIN (Optional)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Billing Address") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val ob = openingBalance.toDoubleOrNull() ?: 0.0
                        val p = initialParty?.copy(
                            name = name.trim(),
                            phone = phone.trim(),
                            gstin = gstin.trim(),
                            address = address.trim(),
                            city = city.trim(),
                            partyType = partyType,
                            openingBalance = ob
                        ) ?: Party(
                            name = name.trim(),
                            phone = phone.trim(),
                            gstin = gstin.trim(),
                            address = address.trim(),
                            city = city.trim(),
                            partyType = partyType,
                            openingBalance = ob,
                            currentBalance = ob
                        )
                        onSave(p)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
            ) {
                Text("Save Company")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
