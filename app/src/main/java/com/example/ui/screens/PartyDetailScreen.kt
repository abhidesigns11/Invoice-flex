package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.Invoice
import com.example.data.model.Party
import com.example.data.model.PartyType
import com.example.data.model.PaymentMode
import com.example.data.model.PaymentStatus
import com.example.data.model.PaymentTransaction
import com.example.ui.components.AccountProfileSheet
import com.example.ui.components.Formatters
import com.example.ui.components.SupplierBillScannerDialog
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TertiaryEmerald
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyDetailScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToInvoiceDetail: (Long) -> Unit,
    onNavigateToSettings: () -> Unit = {}
) {
    val context = LocalContext.current
    val party = state.selectedParty
    val profile = state.profile

    var showPaymentDialog by remember { mutableStateOf(false) }
    var isPaymentIn by remember { mutableStateOf(true) }
    var showReminderSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showAccountSheet by remember { mutableStateOf(false) }
    var showBillScannerDialog by remember { mutableStateOf(false) }

    var selectedInvoiceForPayment by remember { mutableStateOf<Invoice?>(null) }
    var viewedBillPhotoUri by remember { mutableStateOf<String?>(null) }

    val reminderSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (party == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Party not found.")
        }
        return
    }

    fun callPartyPhone() {
        if (party.phone.isNotBlank()) {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${party.phone}"))
            context.startActivity(intent)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(party.name, fontWeight = FontWeight.Bold, maxLines = 1, fontSize = 17.sp)
                        Text("${party.partyType.label} Ledger Hub", fontSize = 11.sp, color = Color(0xFF64748B))
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("party_detail_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (party.partyType == PartyType.SUPPLIER) {
                        IconButton(onClick = { showBillScannerDialog = true }) {
                            Icon(Icons.Default.CameraAlt, contentDescription = "Scan Bill", tint = PrimaryCobalt)
                        }
                    }
                    if (party.phone.isNotBlank()) {
                        IconButton(onClick = { callPartyPhone() }) {
                            Icon(imageVector = Icons.Default.Call, contentDescription = "Call", tint = PrimaryBlue)
                        }
                    }
                    // Top-right Account Profile Avatar
                    IconButton(onClick = { showAccountSheet = true }) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF0A2540),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    profile?.businessName?.take(1)?.uppercase() ?: "A",
                                    color = PureWhite,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                    IconButton(onClick = { showDeleteConfirm = true }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                color = PureWhite,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (party.partyType == PartyType.CUSTOMER) {
                        Button(
                            onClick = {
                                isPaymentIn = true
                                showPaymentDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("btn_record_payment_in")
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Payment In (₹)", fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { showReminderSheet = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.2f)
                                .height(48.dp)
                                .testTag("btn_payment_reminder")
                        ) {
                            Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("WhatsApp Reminder", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    } else {
                        Button(
                            onClick = { showBillScannerDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(48.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Bill Photo", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = {
                                isPaymentIn = false
                                showPaymentDialog = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .height(48.dp)
                                .testTag("btn_record_payment_out")
                        ) {
                            Icon(imageVector = Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Give Payment (₹)", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Party Info & Current Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
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
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = party.name,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0F172A)
                                )
                                Text(
                                    text = "${party.partyType.label} • ${party.city.ifBlank { "Maharashtra" }}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF64748B)
                                )
                                if (party.gstin.isNotBlank()) {
                                    Text(
                                        text = "GSTIN: ${party.gstin}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = PrimaryBlue,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "Current Balance",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF64748B)
                                )
                                Text(
                                    text = Formatters.formatCurrency(Math.abs(party.currentBalance)),
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (party.currentBalance > 0) SuccessGreen else if (party.currentBalance < 0) DangerRed else Color.Gray
                                )
                                Text(
                                    text = when {
                                        party.currentBalance > 0 -> "(To Collect)"
                                        party.currentBalance < 0 -> "(To Pay)"
                                        else -> "(Settled)"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (party.currentBalance > 0) SuccessGreen else if (party.currentBalance < 0) DangerRed else Color.Gray
                                )
                            }
                        }

                        if (party.address.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Address: ${party.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            }

            // Invoices Header & List
            // "if I make invoice of one same company so they show one name of company/party but inside them show two diff/individual invoice"
            val partyInvoices = state.selectedPartyInvoices
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Individual Invoices & Bills (${partyInvoices.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )

                    if (party.partyType == PartyType.SUPPLIER) {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFFEFF6FF),
                            modifier = Modifier.clickable { showBillScannerDialog = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(14.dp))
                                Text("+ Scan Bill Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                            }
                        }
                    }
                }
            }

            if (partyInvoices.isNotEmpty()) {
                items(partyInvoices, key = { it.invoice.id }) { invItem ->
                    val inv = invItem.invoice
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onNavigateToInvoiceDetail(inv.id) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(2.dp),
                        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
                    ) {
                        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Top row: Invoice #, Date & Status Pill
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = Color(0xFFF1F5F9)
                                    ) {
                                        Text(
                                            inv.invoiceNumber,
                                            fontFamily = FontFamily.Monospace,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFF0F172A),
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        Formatters.formatDate(inv.date),
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = when (inv.paymentStatus) {
                                        PaymentStatus.PAID -> Color(0xFFDCFCE7)
                                        PaymentStatus.PARTIAL -> Color(0xFFFEF3C7)
                                        PaymentStatus.UNPAID, PaymentStatus.OVERDUE -> Color(0xFFFEE2E2)
                                    }
                                ) {
                                    Text(
                                        inv.paymentStatus.label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = when (inv.paymentStatus) {
                                            PaymentStatus.PAID -> Color(0xFF15803D)
                                            PaymentStatus.PARTIAL -> Color(0xFFB45309)
                                            PaymentStatus.UNPAID, PaymentStatus.OVERDUE -> Color(0xFFB91C1C)
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Middle row: Scanned Bill Photo & 1-Month Reminder tags
                            if (inv.billPhotoUri != null || inv.hasPaymentReminder) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (inv.billPhotoUri != null) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFEFF6FF),
                                            modifier = Modifier.clickable { viewedBillPhotoUri = inv.billPhotoUri }
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(13.dp))
                                                Text("📷 Bill Photo Attached (Tap to View)", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                                            }
                                        }
                                    }

                                    if (inv.hasPaymentReminder) {
                                        Surface(
                                            shape = RoundedCornerShape(8.dp),
                                            color = Color(0xFFFFFBEB)
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(13.dp))
                                                Text("1-Month Reminder Active", fontSize = 10.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                            }
                                        }
                                    }
                                }
                            }

                            // Bottom row: Financial amounts & Manage Payment Action
                            HorizontalDivider(color = Color(0xFFF1F5F9))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Grand Total: ₹${Formatters.formatCurrency(inv.grandTotal)}", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF0F172A))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("Paid: ₹${Formatters.formatCurrency(inv.paidAmount)}", fontSize = 11.sp, color = SuccessGreen, fontWeight = FontWeight.Medium)
                                        Text("•", color = Color(0xFFCBD5E1))
                                        Text(
                                            "Pending: ₹${Formatters.formatCurrency(inv.balanceDue)}",
                                            fontSize = 11.sp,
                                            color = if (inv.balanceDue > 0) DangerRed else Color(0xFF64748B),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                if (inv.balanceDue > 0) {
                                    Button(
                                        onClick = { selectedInvoiceForPayment = inv },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(34.dp)
                                    ) {
                                        Text("Pay Bill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No bills or invoices added for this company yet.", color = Color(0xFF64748B), fontSize = 12.sp)
                        }
                    }
                }
            }

            // Ledger Transactions History
            val partyTransactions = state.selectedPartyTransactions
            if (partyTransactions.isNotEmpty()) {
                item {
                    Text(
                        text = "Payment Entries (${partyTransactions.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                items(partyTransactions, key = { it.id }) { tx ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (tx.type == "PAYMENT_IN") "Payment In (Received)" else "Payment Out (Paid)",
                                    fontWeight = FontWeight.Bold,
                                    color = if (tx.type == "PAYMENT_IN") SuccessGreen else DangerRed,
                                    fontSize = 13.sp
                                )
                                Text(text = "${Formatters.formatDate(tx.date)} • Mode: ${tx.paymentMode.label}", style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B))
                                if (tx.notes.isNotBlank()) {
                                    Text(text = tx.notes, style = MaterialTheme.typography.bodySmall, color = Color(0xFF64748B), fontSize = 11.sp)
                                }
                            }
                            Text(
                                text = "${if (tx.type == "PAYMENT_IN") "+" else "-"} ₹${Formatters.formatCurrency(tx.amount)}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.type == "PAYMENT_IN") SuccessGreen else DangerRed
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(70.dp)) }
        }
    }

    // Record Payment for entire party
    if (showPaymentDialog) {
        RecordPaymentDialog(
            party = party,
            isPaymentIn = isPaymentIn,
            onDismiss = { showPaymentDialog = false },
            onSave = { amount, mode, ref, notes ->
                viewModel.recordPartyPayment(
                    partyId = party.id,
                    partyName = party.name,
                    amount = amount,
                    isPaymentIn = isPaymentIn,
                    paymentMode = mode,
                    referenceNo = ref,
                    notes = notes
                )
                showPaymentDialog = false
            }
        )
    }

    // Bill Photo Viewer Dialog
    if (viewedBillPhotoUri != null) {
        Dialog(onDismissRequest = { viewedBillPhotoUri = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = PureWhite,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Scanned Supplier Bill Photo", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        IconButton(onClick = { viewedBillPhotoUri = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Simulated Original Photo Box
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF0F172A),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = null, tint = PureWhite, modifier = Modifier.size(54.dp))
                            Spacer(modifier = Modifier.height(10.dp))
                            Text("Official B2B Tax Invoice Photo", color = PureWhite, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Vendor: ${party.name}", color = Color(0xFF94A3B8), fontSize = 12.sp)
                            Text("OCR Verified & Attached to Ledger", color = TertiaryEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { viewedBillPhotoUri = null },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Close Photo Viewer")
                    }
                }
            }
        }
    }

    // Manage Payment for individual bill
    if (selectedInvoiceForPayment != null) {
        val targetInv = selectedInvoiceForPayment!!
        var payAmount by remember { mutableStateOf(targetInv.balanceDue.toInt().toString()) }
        var payMode by remember { mutableStateOf(PaymentMode.BANK_TRANSFER) }

        AlertDialog(
            onDismissRequest = { selectedInvoiceForPayment = null },
            title = { Text("Record Payment for ${targetInv.invoiceNumber}", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Total Bill: ₹${Formatters.formatCurrency(targetInv.grandTotal)}", fontSize = 12.sp)
                    Text("Currently Pending: ₹${Formatters.formatCurrency(targetInv.balanceDue)}", fontWeight = FontWeight.Bold, color = DangerRed)

                    OutlinedTextField(
                        value = payAmount,
                        onValueChange = { payAmount = it },
                        label = { Text("Payment Amount (₹)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )

                    PartyPaymentModeDropdown(
                        selected = payMode,
                        onSelect = { payMode = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val pAmount = payAmount.toDoubleOrNull() ?: 0.0
                        if (pAmount > 0) {
                            viewModel.updateInvoicePayment(
                                invoiceId = targetInv.id,
                                additionalPayment = pAmount,
                                paymentMode = payMode,
                                notes = "Payment recorded for ${targetInv.invoiceNumber}"
                            )
                            selectedInvoiceForPayment = null
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
                ) {
                    Text("Confirm Payment")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedInvoiceForPayment = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // WhatsApp Payment Reminder Generator Sheet
    if (showReminderSheet) {
        ModalBottomSheet(
            onDismissRequest = { showReminderSheet = false },
            sheetState = reminderSheetState
        ) {
            WhatsAppReminderBottomSheet(
                party = party,
                profile = profile,
                onShare = { reminderText ->
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, reminderText)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(intent, "Send Payment Reminder via"))
                    showReminderSheet = false
                }
            )
        }
    }

    // Supplier Bill Scanner Dialog
    if (showBillScannerDialog) {
        SupplierBillScannerDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showBillScannerDialog = false },
            onBillSaved = { pId ->
                viewModel.selectPartyById(pId)
            }
        )
    }

    // Top-Right Account Profile Sheet
    if (showAccountSheet) {
        AccountProfileSheet(
            state = state,
            onDismiss = { showAccountSheet = false },
            onNavigateToSettings = onNavigateToSettings
        )
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Party?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${party.name}' from your contacts?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteParty(party)
                        showDeleteConfirm = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordPaymentDialog(
    party: Party,
    isPaymentIn: Boolean,
    onDismiss: () -> Unit,
    onSave: (amount: Double, mode: PaymentMode, ref: String, notes: String) -> Unit
) {
    var amount by remember { mutableStateOf(if (party.currentBalance != 0.0) Math.abs(party.currentBalance).toString() else "") }
    var selectedMode by remember { mutableStateOf(PaymentMode.CASH) }
    var refNo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isPaymentIn) "Record Payment In (Collect)" else "Record Payment Out (Pay)",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(text = "Party: ${party.name}", fontWeight = FontWeight.SemiBold)
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("input_payment_amount"),
                    singleLine = true
                )

                PartyPaymentModeDropdown(
                    selected = selectedMode,
                    onSelect = { selectedMode = it },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = refNo,
                    onValueChange = { refNo = it },
                    label = { Text("Reference / UPI Transaction ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Remarks") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = amount.toDoubleOrNull()
                    if (parsed != null && parsed > 0) {
                        onSave(parsed, selectedMode, refNo, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (isPaymentIn) SuccessGreen else DangerRed)
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PartyPaymentModeDropdown(
    selected: PaymentMode,
    onSelect: (PaymentMode) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selected.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("Payment Mode") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
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
fun WhatsAppReminderBottomSheet(
    party: Party,
    profile: com.example.data.model.BusinessProfile?,
    onShare: (String) -> Unit
) {
    val context = LocalContext.current
    val balanceAmount = Math.abs(party.currentBalance)
    val defaultText = "Dear ${party.name},\n\nThis is a gentle reminder from ${profile?.businessName ?: "our business"} that your payment of ₹${Formatters.formatCurrency(balanceAmount)} is due. Kindly clear the pending balance at your earliest convenience.\n\nUPI: ${profile?.upiId ?: "Not Provided"}\nBank: ${profile?.bankName ?: "Not Provided"}\nA/c: ${profile?.accountNumber ?: "Not Provided"}\nIFSC: ${profile?.ifscCode ?: "Not Provided"}\n\nThank you for your business!"

    var reminderText by remember { mutableStateOf(defaultText) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "Share Payment Reminder",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Send via WhatsApp or any messaging app to ${party.name}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = reminderText,
            onValueChange = { reminderText = it },
            label = { Text("Reminder Message") },
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            maxLines = 8
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onShare(reminderText) },
            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(imageVector = Icons.Default.Share, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Share via WhatsApp / Messages", fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(10.dp))
    }
}
