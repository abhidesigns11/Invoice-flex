package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.InvoiceType
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.PaymentStatus
import com.example.ui.components.AccountProfileSheet
import com.example.ui.components.Formatters
import com.example.ui.components.InvoiceFlexLogo
import com.example.ui.components.SupplierBillScannerDialog
import com.example.ui.theme.*
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: BillingUiState,
    onNavigateToCreateInvoice: (InvoiceType) -> Unit,
    onNavigateToInvoiceDetail: (Long) -> Unit,
    onNavigateToInvoices: () -> Unit,
    onNavigateToParties: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToPos: () -> Unit,
    onNavigateToReports: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToHsnFinder: () -> Unit,
    onAddPartyClick: () -> Unit,
    onAddItemClick: () -> Unit,
    onNavigateToForYou: () -> Unit = {},
    onNavigateToThemeColor: () -> Unit = {},
    onNavigateToSetupWizard: () -> Unit = {},
    onNavigateToEWayIrn: () -> Unit = {},
    viewModel: BillingViewModel? = null,
    onNavigateToPartyDetail: ((Long) -> Unit)? = null
) {
    val context = LocalContext.current
    var showAccountSheet by remember { mutableStateOf(false) }
    var showBillScannerDialog by remember { mutableStateOf(false) }

    // Invoice feed filter: "ALL", "SALES", "PURCHASES", "UNPAID"
    var invoiceFilter by remember { mutableStateOf("ALL") }

    val filteredInvoices = remember(state.allInvoices, invoiceFilter) {
        when (invoiceFilter) {
            "SALES" -> state.allInvoices.filter {
                it.invoice.invoiceType in listOf(InvoiceType.SALE_INVOICE, InvoiceType.POS_BILL)
            }
            "PURCHASES" -> state.allInvoices.filter {
                it.invoice.invoiceType == InvoiceType.PURCHASE_INVOICE
            }
            "UNPAID" -> state.allInvoices.filter {
                it.invoice.balanceDue > 0
            }
            else -> state.allInvoices
        }
    }

    val netWorkingBalance = (state.totalReceivables - state.totalPayables)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        InvoiceFlexLogo(size = 32.dp, showText = true, subtitle = "B2B Billing & FinTech")
                    }
                },
                actions = {
                    // Search Action
                    IconButton(onClick = onNavigateToHsnFinder) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color(0xFF475569))
                    }

                    // Notification Action
                    IconButton(onClick = {
                        Toast.makeText(context, "System Alerts: GST Portal & E-Way NIC Connected", Toast.LENGTH_SHORT).show()
                    }) {
                        Box {
                            Icon(Icons.Outlined.Notifications, contentDescription = "Alerts", tint = Color(0xFF475569))
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color(0xFF0284C7), CircleShape)
                                    .align(Alignment.TopEnd)
                            )
                        }
                    }

                    // Universal Top-Right Account Menu Button (Requested by user across all apps)
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
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToCreateInvoice(InvoiceType.SALE_INVOICE) },
                containerColor = Color(0xFF0A2540),
                contentColor = PureWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_create_sale")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null)
                    Text("New Invoice", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                }
            }
        },
        containerColor = Color(0xFFF8FAFC)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Modern FinTech Hero Balance Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0A2540),
                                        Color(0xFF0F172A),
                                        Color(0xFF1E293B)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // Header of Hero Card
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Box(modifier = Modifier.size(8.dp).background(TertiaryEmerald, CircleShape))
                                    Text(
                                        state.profile?.businessName ?: "Apex Stainless Steel & Engineering",
                                        color = PureWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.5.sp,
                                        maxLines = 1
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = PureWhite.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        "GST: ${state.profile?.gstin ?: "27AAACA9876F1Z4"}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 9.5.sp,
                                        color = Color(0xFF93C5FD),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            // Working Capital / Net Position
                            Column {
                                Text(
                                    "NET WORKING BALANCE",
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF94A3B8)
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        "₹ ${Formatters.formatCurrency(Math.abs(netWorkingBalance))}",
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 28.sp,
                                        color = PureWhite
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (netWorkingBalance >= 0) "(Positive Surplus)" else "(Payable Deficit)",
                                        fontSize = 11.sp,
                                        color = if (netWorkingBalance >= 0) TertiaryEmerald else Color(0xFFF87171),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }
                            }

                            // Dual Balance Containers: To Collect vs To Pay
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // To Collect Container
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToParties() },
                                    shape = RoundedCornerShape(14.dp),
                                    color = PureWhite.copy(alpha = 0.08f),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFF059669).copy(alpha = 0.4f)))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.TrendingUp, contentDescription = null, tint = TertiaryEmerald, modifier = Modifier.size(15.dp))
                                            Text("To Collect", fontSize = 11.sp, color = Color(0xFF6EE7B7), fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "₹ ${Formatters.formatCurrency(state.totalReceivables)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = PureWhite
                                        )
                                    }
                                }

                                // To Pay Container
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onNavigateToParties() },
                                    shape = RoundedCornerShape(14.dp),
                                    color = PureWhite.copy(alpha = 0.08f),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFDC2626).copy(alpha = 0.4f)))
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Icon(Icons.AutoMirrored.Filled.TrendingDown, contentDescription = null, tint = Color(0xFFF87171), modifier = Modifier.size(15.dp))
                                            Text("To Pay (Suppliers)", fontSize = 11.sp, color = Color(0xFFFCA5A5), fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            "₹ ${Formatters.formatCurrency(state.totalPayables)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            color = PureWhite
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 2. FinTech Quick Action Pills (High Utility Grid)
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // + New Sale Bill
                    FintechActionPill(
                        icon = Icons.Default.AddCircle,
                        title = "+ New Sale",
                        subtitle = "GST Bill",
                        color = Color(0xFF0284C7),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigateToCreateInvoice(InvoiceType.SALE_INVOICE) }
                    )

                    // 📷 Scan Supplier Bill (Core User Feature)
                    FintechActionPill(
                        icon = Icons.Default.CameraAlt,
                        title = "Scan Bill",
                        subtitle = "Photo & 1M Remind",
                        color = Color(0xFF4F46E5),
                        modifier = Modifier.weight(1f),
                        onClick = { showBillScannerDialog = true }
                    )

                    // + Add Party
                    FintechActionPill(
                        icon = Icons.Default.PersonAdd,
                        title = "+ Add Party",
                        subtitle = "Ledger Hub",
                        color = Color(0xFF0F172A),
                        modifier = Modifier.weight(1f),
                        onClick = onAddPartyClick
                    )

                    // POS Billing
                    FintechActionPill(
                        icon = Icons.Default.PointOfSale,
                        title = "POS Bill",
                        subtitle = "Fast Counter",
                        color = Color(0xFF059669),
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToPos
                    )
                }
            }

            // 3. Trending Feed / Live Market Pulse Strip
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onNavigateToForYou() },
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFEFF6FF),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFBFDBFE)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(shape = CircleShape, color = Color(0xFF0284C7)) {
                                Text("🔥 TRENDING", color = PureWhite, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                            }
                            Text(
                                "Nickel +4.8% • New CBIC Rule 88D Timelines Enforced",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1E3A8A),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Go to Feed",
                            modifier = Modifier.size(15.dp),
                            tint = Color(0xFF0284C7)
                        )
                    }
                }
            }

            // 4. Clean Modern Invoice Feed Header & Filter Pills
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Recent Invoices & Bills",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A)
                        )

                        TextButton(onClick = onNavigateToInvoices) {
                            Text("View All (${state.allInvoices.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryCobalt)
                        }
                    }

                    // Feed Filter Pills
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FintechFilterChip(
                                label = "All (${state.allInvoices.size})",
                                isSelected = invoiceFilter == "ALL",
                                onClick = { invoiceFilter = "ALL" }
                            )
                        }
                        item {
                            FintechFilterChip(
                                label = "Sales",
                                isSelected = invoiceFilter == "SALES",
                                onClick = { invoiceFilter = "SALES" }
                            )
                        }
                        item {
                            FintechFilterChip(
                                label = "Supplier Bills (Purchases)",
                                isSelected = invoiceFilter == "PURCHASES",
                                onClick = { invoiceFilter = "PURCHASES" }
                            )
                        }
                        item {
                            FintechFilterChip(
                                label = "Unpaid Dues",
                                isSelected = invoiceFilter == "UNPAID",
                                onClick = { invoiceFilter = "UNPAID" }
                            )
                        }
                    }
                }
            }

            // 5. Clean FinTech Invoice Cards List
            items(filteredInvoices.take(8), key = { it.invoice.id }) { invItem ->
                val inv = invItem.invoice
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToInvoiceDetail(inv.id) },
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(1.5.dp),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (inv.invoiceType == InvoiceType.PURCHASE_INVOICE) Color(0xFFFEF3C7) else Color(0xFFEFF6FF),
                                modifier = Modifier.size(44.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        inv.partyName.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        color = if (inv.invoiceType == InvoiceType.PURCHASE_INVOICE) Color(0xFFD97706) else Color(0xFF2563EB),
                                        fontSize = 14.sp
                                    )
                                }
                            }

                            Column {
                                Text(
                                    inv.partyName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.5.sp,
                                    color = Color(0xFF0F172A),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        inv.invoiceNumber,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text("•", color = Color(0xFFCBD5E1))
                                    Text(
                                        Formatters.formatDate(inv.date),
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }

                                if (inv.billPhotoUri != null || inv.hasPaymentReminder) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        if (inv.billPhotoUri != null) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFEEF2FF)) {
                                                Row(
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                                ) {
                                                    Icon(Icons.Default.PhotoCamera, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(10.dp))
                                                    Text("Photo Bill", fontSize = 8.5.sp, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                                                }
                                            }
                                        }
                                        if (inv.hasPaymentReminder) {
                                            Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFFFFFBEB)) {
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
                                }
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                "₹ ${Formatters.formatCurrency(inv.grandTotal)}",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 14.5.sp,
                                color = if (inv.invoiceType == InvoiceType.PURCHASE_INVOICE) Color(0xFF0F172A) else Color(0xFF059669)
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when (inv.paymentStatus) {
                                    PaymentStatus.PAID -> Color(0xFFDCFCE7)
                                    PaymentStatus.PARTIAL -> Color(0xFFFEF3C7)
                                    PaymentStatus.UNPAID -> Color(0xFFFEE2E2)
                                    PaymentStatus.OVERDUE -> Color(0xFFFEE2E2)
                                }
                            ) {
                                Text(
                                    inv.paymentStatus.label,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (inv.paymentStatus) {
                                        PaymentStatus.PAID -> Color(0xFF15803D)
                                        PaymentStatus.PARTIAL -> Color(0xFFB45309)
                                        PaymentStatus.UNPAID, PaymentStatus.OVERDUE -> Color(0xFFB91C1C)
                                    },
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            if (filteredInvoices.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite)
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No invoices match this filter.", color = Color(0xFF64748B), fontSize = 12.sp)
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }

    // Supplier Bill Photo Scanner Sheet
    if (showBillScannerDialog && viewModel != null) {
        SupplierBillScannerDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showBillScannerDialog = false },
            onBillSaved = { partyId ->
                if (onNavigateToPartyDetail != null) {
                    onNavigateToPartyDetail(partyId)
                } else {
                    onNavigateToParties()
                }
            }
        )
    }

    // Top-Right Account Profile Modal
    if (showAccountSheet) {
        AccountProfileSheet(
            state = state,
            onDismiss = { showAccountSheet = false },
            onNavigateToSettings = onNavigateToSettings,
            onNavigateToThemeColor = onNavigateToThemeColor,
            onNavigateToReports = onNavigateToReports,
            onNavigateToParties = onNavigateToParties
        )
    }
}

@Composable
fun FintechActionPill(
    icon: ImageVector,
    title: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(1.5.dp),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFE2E8F0)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.12f),
                modifier = Modifier.size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
                }
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF0F172A), maxLines = 1)
            Text(subtitle, fontSize = 9.sp, color = Color(0xFF64748B), maxLines = 1)
        }
    }
}

@Composable
fun FintechFilterChip(
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
            fontSize = 11.5.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) PureWhite else Color(0xFF475569),
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
