package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvoiceWithDetails
import com.example.ui.components.Formatters
import com.example.ui.components.InvoiceTypeBadge
import com.example.ui.components.PaymentModeBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.components.UpiHelper
import com.example.ui.components.UpiQrCodeView
import com.example.ui.theme.DangerRed
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailPreviewScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val invoiceWithDetails = state.selectedInvoice
    val profile = state.profile

    var selectedFormatTab by remember { mutableIntStateOf(0) } // 0: Tally Bordered Grid, 1: Modern SS Fabricator, 2: POS Thermal
    var showTemplateStudioSheet by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Template customizer state
    var showHsnColumn by remember { mutableStateOf(true) }
    var showTaxBreakup by remember { mutableStateOf(true) }
    var showBankDetails by remember { mutableStateOf(true) }
    var customHeaderTitle by remember { mutableStateOf("TAX INVOICE") }
    var primaryAccentColor by remember { mutableStateOf(Color(0xFF1E3A8A)) } // Tally Blue

    val templateSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    if (invoiceWithDetails == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Invoice not found.")
        }
        return
    }

    val invoice = invoiceWithDetails.invoice
    val items = invoiceWithDetails.items

    val upiPayload = remember(profile, invoice) {
        val upiId = profile?.upiId?.ifBlank { "apexsteel@upi" } ?: "apexsteel@upi"
        val bName = profile?.businessName ?: "Apex Stainless Steel"
        UpiHelper.generateUpiString(upiId, bName, invoice.grandTotal, invoice.invoiceNumber)
    }

    fun shareInvoiceWhatsApp() {
        val message = buildString {
            append("Dear ${invoice.partyName},\n\n")
            append("Here are the details of your Stainless Steel Fabrication Bill ${invoice.invoiceNumber} from ${profile?.businessName ?: "Apex Steel"}:\n")
            append("Date: ${Formatters.formatDate(invoice.date)}\n")
            append("Total Amount: ${Formatters.formatCurrency(invoice.grandTotal)}\n")
            append("Paid: ${Formatters.formatCurrency(invoice.paidAmount)}\n")
            if (invoice.balanceDue > 0) {
                append("Outstanding Balance: ${Formatters.formatCurrency(invoice.balanceDue)}\n\n")
                append("Please pay via UPI to: ${profile?.upiId ?: "apexsteel@upi"}\n")
            } else {
                append("Status: Fully Settled (Thank you!)\n\n")
            }
            append("Thank you for your business with us!")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, message)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(sendIntent, "Share Invoice via"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = invoice.invoiceNumber,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${invoice.partyName} • SS Fabrication",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("preview_invoice_back_btn")
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showTemplateStudioSheet = true },
                        modifier = Modifier.testTag("btn_open_template_studio")
                    ) {
                        Icon(imageVector = Icons.Default.Palette, contentDescription = "Custom Template Studio", tint = PrimaryBlue)
                    }
                    IconButton(
                        onClick = { shareInvoiceWhatsApp() },
                        modifier = Modifier.testTag("preview_share_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = PrimaryBlue)
                    }
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier.testTag("preview_delete_btn")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
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
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { shareInvoiceWhatsApp() },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_share_whatsapp"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share on WhatsApp")
                    }

                    Button(
                        onClick = { shareInvoiceWhatsApp() },
                        colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                        modifier = Modifier
                            .weight(1.2f)
                            .height(48.dp)
                            .testTag("btn_print_pdf"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Print, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Print / Export PDF", fontWeight = FontWeight.Bold)
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
            // Template Format Switcher Tab
            ScrollableTabRow(
                selectedTabIndex = selectedFormatTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedFormatTab == 0,
                    onClick = { selectedFormatTab = 0 },
                    text = { Text("📊 Tally Bordered Grid (Classic)", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedFormatTab == 1,
                    onClick = { selectedFormatTab = 1 },
                    text = { Text("✨ Modern SS Fabricator", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedFormatTab == 2,
                    onClick = { selectedFormatTab = 2 },
                    text = { Text("🧾 Thermal Receipt (80mm)", fontWeight = FontWeight.SemiBold) }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedFormatTab) {
                    0 -> {
                        // Tally-Style Full Bordered Grid Invoice Card
                        item {
                            TallyBorderedGstInvoiceCard(
                                invoice = invoice,
                                items = items,
                                profile = profile,
                                upiPayload = upiPayload,
                                headerTitle = customHeaderTitle,
                                accentColor = primaryAccentColor,
                                showHsn = showHsnColumn,
                                showBank = showBankDetails
                            )
                        }
                    }
                    1 -> {
                        // Modern SS Fabrication Template Card
                        item {
                            ModernSteelInvoiceCard(
                                invoice = invoice,
                                items = items,
                                profile = profile,
                                upiPayload = upiPayload,
                                accentColor = primaryAccentColor
                            )
                        }
                    }
                    2 -> {
                        // Thermal Receipt
                        item {
                            PosThermalReceiptCard(
                                invoice = invoice,
                                items = items,
                                profile = profile,
                                upiPayload = upiPayload
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

    // Custom Template Studio / AI Invoice Converter Sheet
    if (showTemplateStudioSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTemplateStudioSheet = false },
            sheetState = templateSheetState
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = PrimaryBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Custom Invoice Design Studio",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "Customize your invoice format, borders, HSN columns, and brand colors to match your company branding or Tally stationery.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                HorizontalDivider()

                // Header Title Presets
                Text(text = "Header Title:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("TAX INVOICE", "FABRICATION BILL", "DELIVERY CHALLAN", "JOB WORK ESTIMATE").forEach { title ->
                        FilterChip(
                            selected = customHeaderTitle == title,
                            onClick = { customHeaderTitle = title },
                            label = { Text(title, fontSize = 11.sp) }
                        )
                    }
                }

                // Brand Color Theme Palette
                Text(text = "Template Color Accent:", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    listOf(
                        Color(0xFF1E3A8A) to "Tally Navy",
                        Color(0xFF0284C7) to "Steel Blue",
                        Color(0xFF0F766E) to "Teal",
                        Color(0xFFB45309) to "Industrial Amber",
                        Color(0xFF334155) to "Titanium Gray"
                    ).forEach { (color, name) ->
                        FilterChip(
                            selected = primaryAccentColor == color,
                            onClick = { primaryAccentColor = color },
                            label = { Text(name, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = color,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                HorizontalDivider()

                // Column toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Show HSN/SAC Code Column in Grid", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = showHsnColumn, onCheckedChange = { showHsnColumn = it })
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Show Bank Account & UPI QR Code", style = MaterialTheme.typography.bodyMedium)
                    Switch(checked = showBankDetails, onCheckedChange = { showBankDetails = it })
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showTemplateStudioSheet = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp)
                ) {
                    Text("Apply Template Design", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Invoice?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete ${invoice.invoiceNumber}?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteInvoice(invoiceWithDetails)
                        showDeleteDialog = false
                        onNavigateBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DangerRed)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

/**
 * Classic Tally-Style Full Bordered Grid Tax Invoice
 * Features distinct inner and outer grid borders, official boxed fields and HSN table.
 */
@Composable
fun TallyBorderedGstInvoiceCard(
    invoice: com.example.data.model.Invoice,
    items: List<com.example.data.model.InvoiceItem>,
    profile: com.example.data.model.BusinessProfile?,
    upiPayload: String,
    headerTitle: String,
    accentColor: Color,
    showHsn: Boolean,
    showBank: Boolean
) {
    val borderColor = Color(0xFF0F172A)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("tally_grid_invoice_card"),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
                .border(1.5.dp, borderColor)
        ) {
            // Top Title Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(accentColor)
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = headerTitle,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp,
                    letterSpacing = 1.sp
                )
            }

            // Company Header & Invoice Details Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor)
            ) {
                // Left: Company Info
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(8.dp)
                ) {
                    Text(
                        text = profile?.businessName ?: "Apex Stainless Steel & Engineering Works",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color.Black
                    )
                    Text(
                        text = profile?.tagline ?: "SS Fabrication, Laser Cutting & Bending",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "${profile?.address ?: ""}, ${profile?.city ?: ""}, ${profile?.state ?: ""} - ${profile?.pincode ?: ""}",
                        fontSize = 10.sp,
                        color = Color.DarkGray
                    )
                    Text(text = "GSTIN: ${profile?.gstin ?: ""} | State: ${profile?.state ?: ""} (${profile?.stateCode ?: "27"})", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(text = "Phone: ${profile?.phone ?: ""} | Email: ${profile?.email ?: ""}", fontSize = 10.sp, color = Color.DarkGray)
                }

                // Right: Invoice Meta Box
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .border(1.dp, borderColor)
                        .padding(8.dp)
                ) {
                    Text(text = "Invoice No: ${invoice.invoiceNumber}", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                    Text(text = "Dated: ${Formatters.formatShortDate(invoice.date)}", fontSize = 10.sp, color = Color.Black)
                    Text(text = "Due Date: ${Formatters.formatShortDate(invoice.dueDate)}", fontSize = 10.sp, color = Color.Black)
                    if (invoice.eWayBillNo.isNotBlank()) {
                        Text(text = "E-Way Bill: ${invoice.eWayBillNo}", fontSize = 10.sp, color = Color.Black)
                    }
                    if (invoice.vehicleNo.isNotBlank()) {
                        Text(text = "Vehicle No: ${invoice.vehicleNo}", fontSize = 10.sp, color = Color.Black)
                    }
                }
            }

            // Buyer / Party Details Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor)
                    .padding(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Buyer / Consignee (Bill To):", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
                    Text(text = invoice.partyName, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                    if (invoice.partyAddress.isNotBlank()) {
                        Text(text = "Address: ${invoice.partyAddress}", fontSize = 10.sp, color = Color.DarkGray)
                    }
                    if (invoice.partyPhone.isNotBlank()) {
                        Text(text = "Phone: ${invoice.partyPhone}", fontSize = 10.sp, color = Color.DarkGray)
                    }
                    if (invoice.partyGstin.isNotBlank()) {
                        Text(text = "GSTIN / UIN: ${invoice.partyGstin}", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
                    }
                }
            }

            // Table Header with Vertical & Horizontal Grid Borders
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9))
                    .border(1.dp, borderColor),
                verticalAlignment = Alignment.CenterVertically
            ) {
                TableCell(text = "Sl No", weight = 0.4f, isHeader = true)
                TableCell(text = "Description of Goods / SS Fabrication Work", weight = 2.0f, isHeader = true)
                if (showHsn) TableCell(text = "HSN/SAC", weight = 0.8f, isHeader = true)
                TableCell(text = "Qty", weight = 0.6f, isHeader = true)
                TableCell(text = "Rate (₹)", weight = 0.8f, isHeader = true)
                TableCell(text = "GST %", weight = 0.5f, isHeader = true)
                TableCell(text = "Amount (₹)", weight = 1.0f, isHeader = true)
            }

            // Table Body Rows
            items.forEachIndexed { index, item ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(0.5.dp, borderColor),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TableCell(text = "${index + 1}", weight = 0.4f)
                    TableCell(text = item.itemName, weight = 2.0f, isBold = true)
                    if (showHsn) TableCell(text = item.hsnCode, weight = 0.8f)
                    TableCell(text = "${item.quantity.toInt()} ${item.unit}", weight = 0.6f)
                    TableCell(text = Formatters.formatCurrency(item.unitPrice, showDecimals = false), weight = 0.8f)
                    TableCell(text = "${item.taxRate.toInt()}%", weight = 0.5f)
                    TableCell(text = Formatters.formatCurrency(item.totalAmount, showDecimals = false), weight = 1.0f, isBold = true)
                }
            }

            // Calculation & Tax Breakdown Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor)
            ) {
                // Left: Bank, UPI QR Code & Remarks
                Column(
                    modifier = Modifier
                        .weight(1.1f)
                        .padding(8.dp)
                ) {
                    if (showBank) {
                        Text(text = "Bank Details & UPI Scan:", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                        Text(text = "Bank: ${profile?.bankName ?: "State Bank of India"}", fontSize = 9.sp, color = Color.DarkGray)
                        Text(text = "A/C: ${profile?.accountNumber ?: ""} | IFSC: ${profile?.ifscCode ?: ""}", fontSize = 9.sp, color = Color.DarkGray)
                        Text(text = "UPI ID: ${profile?.upiId ?: "apexsteel@upi"}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                        Spacer(modifier = Modifier.height(4.dp))
                        UpiQrCodeView(data = upiPayload, size = 90.dp)
                    }
                }

                // Right: Boxed Grand Total Calculations
                Column(
                    modifier = Modifier
                        .weight(0.9f)
                        .border(1.dp, borderColor)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Taxable Subtotal:", fontSize = 10.sp, color = Color.DarkGray)
                        Text(text = Formatters.formatCurrency(invoice.subTotal), fontSize = 10.sp, color = Color.Black)
                    }
                    if (!invoice.isInterState) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "CGST Output:", fontSize = 10.sp, color = Color.DarkGray)
                            Text(text = Formatters.formatCurrency(invoice.cgstAmount), fontSize = 10.sp, color = Color.Black)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "SGST Output:", fontSize = 10.sp, color = Color.DarkGray)
                            Text(text = Formatters.formatCurrency(invoice.sgstAmount), fontSize = 10.sp, color = Color.Black)
                        }
                    } else {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "IGST Output:", fontSize = 10.sp, color = Color.DarkGray)
                            Text(text = Formatters.formatCurrency(invoice.igstAmount), fontSize = 10.sp, color = Color.Black)
                        }
                    }
                    if (invoice.discountAmount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Discount (-):", fontSize = 10.sp, color = DangerRed)
                            Text(text = Formatters.formatCurrency(invoice.discountAmount), fontSize = 10.sp, color = DangerRed)
                        }
                    }
                    HorizontalDivider(color = borderColor)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "TOTAL (₹):", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                        Text(text = Formatters.formatCurrency(invoice.grandTotal), fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Balance Due:", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = if (invoice.balanceDue > 0) DangerRed else SuccessGreen)
                        Text(text = Formatters.formatCurrency(invoice.balanceDue), fontWeight = FontWeight.Bold, fontSize = 10.sp, color = if (invoice.balanceDue > 0) DangerRed else SuccessGreen)
                    }
                }
            }

            // Amount in words
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor)
                    .padding(6.dp)
            ) {
                Text(
                    text = "Amount Chargeable (in words): ${Formatters.numberToWordsIndian(invoice.grandTotal)}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black
                )
            }

            // Declaration & Authorized Signatory Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, borderColor)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1.2f)
                        .padding(6.dp)
                ) {
                    Text(text = "Declaration:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Text(
                        text = "We declare that this invoice shows the actual price of the stainless steel goods/services described and that all particulars are true and correct.",
                        fontSize = 8.sp,
                        color = Color.DarkGray
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .border(1.dp, borderColor)
                        .padding(6.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "for ${profile?.businessName ?: "Apex Steel"}", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                    Spacer(modifier = Modifier.height(26.dp))
                    Text(text = "Authorised Signatory", fontSize = 8.sp, color = Color.DarkGray)
                }
            }
        }
    }
}

@Composable
fun TableCell(
    text: String,
    weight: Float,
    isHeader: Boolean = false,
    isBold: Boolean = false
) {
    Text(
        text = text,
        modifier = Modifier
            .border(0.5.dp, Color(0xFFCBD5E1))
            .padding(4.dp),
        fontWeight = if (isHeader || isBold) FontWeight.Bold else FontWeight.Normal,
        fontSize = if (isHeader) 10.sp else 9.sp,
        color = Color.Black,
        textAlign = TextAlign.Start,
        maxLines = 2
    )
}

@Composable
fun ModernSteelInvoiceCard(
    invoice: com.example.data.model.Invoice,
    items: List<com.example.data.model.InvoiceItem>,
    profile: com.example.data.model.BusinessProfile?,
    upiPayload: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = profile?.businessName ?: "Apex Stainless Steel",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    )
                    Text(text = "Stainless Steel Fabrication & Laser Cutting", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    Text(text = "GSTIN: ${profile?.gstin ?: ""}", fontWeight = FontWeight.Bold, color = PrimaryBlue, fontSize = 11.sp)
                }
                Surface(color = accentColor, shape = RoundedCornerShape(8.dp)) {
                    Text(
                        text = "TAX INVOICE",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))

            // Bill To
            Text(text = "Client / Project:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = invoice.partyName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            if (invoice.partyGstin.isNotBlank()) {
                Text(text = "GSTIN: ${invoice.partyGstin}", fontSize = 11.sp, color = PrimaryBlue, fontWeight = FontWeight.Bold)
            }
            Text(text = "Bill #: ${invoice.invoiceNumber} • Date: ${Formatters.formatShortDate(invoice.date)}", fontSize = 11.sp, color = Color.DarkGray)

            Spacer(modifier = Modifier.height(12.dp))

            // Items List
            items.forEachIndexed { idx, itm ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "${idx + 1}. ${itm.itemName}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "HSN: ${itm.hsnCode} • ${itm.quantity.toInt()} ${itm.unit} @ ${Formatters.formatCurrency(itm.unitPrice)} (GST ${itm.taxRate.toInt()}%)", fontSize = 10.sp, color = Color.DarkGray)
                    }
                    Text(text = Formatters.formatCurrency(itm.totalAmount), fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                HorizontalDivider(color = Color(0xFFF1F5F9))
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                UpiQrCodeView(data = upiPayload, size = 90.dp)
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "Grand Total: ${Formatters.formatCurrency(invoice.grandTotal)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = accentColor)
                    if (invoice.balanceDue > 0) {
                        Text(text = "Balance Due: ${Formatters.formatCurrency(invoice.balanceDue)}", color = DangerRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    } else {
                        Text(text = "Fully Paid", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun PosThermalReceiptCard(
    invoice: com.example.data.model.Invoice,
    items: List<com.example.data.model.InvoiceItem>,
    profile: com.example.data.model.BusinessProfile?,
    upiPayload: String
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pos_thermal_receipt_card"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = profile?.businessName ?: "Apex Stainless Steel",
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = Color.Black
            )
            Text(
                text = profile?.address ?: "",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = Color.DarkGray
            )
            Text(
                text = "Ph: ${profile?.phone ?: ""} • GSTIN: ${profile?.gstin ?: ""}",
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                color = Color.DarkGray
            )

            Text(text = "------------------------------------------", fontFamily = FontFamily.Monospace, color = Color.Gray)
            Text(text = "SS FABRICATION JOB SLIP", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color.Black)
            Text(text = "Bill: ${invoice.invoiceNumber}  Date: ${Formatters.formatDateTime(invoice.date)}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color.DarkGray)
            Text(text = "Client: ${invoice.partyName}", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = Color.DarkGray)
            Text(text = "------------------------------------------", fontFamily = FontFamily.Monospace, color = Color.Gray)

            // Monospaced Items List
            items.forEach { item ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${item.quantity.toInt()}x ${item.itemName}",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color.Black,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = Formatters.formatCurrency(item.totalAmount, showDecimals = false),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }

            Text(text = "------------------------------------------", fontFamily = FontFamily.Monospace, color = Color.Gray)

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "TAXABLE SUBTOTAL:", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.DarkGray)
                Text(text = Formatters.formatCurrency(invoice.subTotal), fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Black)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "TOTAL GST (18%):", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.DarkGray)
                Text(text = Formatters.formatCurrency(invoice.totalTax), fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Black)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "NET GRAND TOTAL:", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
                Text(text = Formatters.formatCurrency(invoice.grandTotal), fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.Black)
            }
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "PAID (${invoice.paymentMode.label}):", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.DarkGray)
                Text(text = Formatters.formatCurrency(invoice.paidAmount), fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color.Black)
            }

            Text(text = "------------------------------------------", fontFamily = FontFamily.Monospace, color = Color.Gray)

            UpiQrCodeView(data = upiPayload, size = 100.dp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Thank you for your business!", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
        }
    }
}
