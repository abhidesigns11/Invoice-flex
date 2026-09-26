package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.MoneyOff
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpenseCategory
import com.example.data.model.InvoiceType
import com.example.data.model.PaymentMode
import com.example.data.model.PaymentTransaction
import com.example.ui.components.Formatters
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.PurpleBadge
import com.example.ui.theme.PurpleBadgeLight
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    state: BillingUiState,
    viewModel: BillingViewModel
) {
    val context = LocalContext.current
    var selectedReportTab by remember { mutableIntStateOf(0) } // 0: P&L, 1: GSTR-1, 2: Daybook, 3: Expenses
    var showAddExpenseDialog by remember { mutableStateOf(false) }

    // Computations for P&L
    val salesInvoices = state.allInvoices.filter { it.invoice.invoiceType == InvoiceType.SALE_INVOICE || it.invoice.invoiceType == InvoiceType.POS_BILL }
    val totalRevenue = salesInvoices.sumOf { it.invoice.grandTotal }
    val totalTaxableRevenue = salesInvoices.sumOf { it.invoice.subTotal }
    val totalGstCollected = salesInvoices.sumOf { it.invoice.totalTax }

    val purchaseInvoices = state.allInvoices.filter { it.invoice.invoiceType == InvoiceType.PURCHASE_INVOICE }
    val totalCostOfPurchases = purchaseInvoices.sumOf { it.invoice.grandTotal }

    val totalExpenses = state.expenses.sumOf { it.amount }
    val grossProfit = (totalRevenue - totalCostOfPurchases).coerceAtLeast(0.0)
    val netProfit = totalRevenue - totalCostOfPurchases - totalExpenses

    fun shareReportText() {
        val reportText = buildString {
            append("=== ${state.profile?.businessName ?: "BillNova"} Financial Report ===\n\n")
            append("Total Sales Revenue: ${Formatters.formatCurrency(totalRevenue)}\n")
            append("Total Purchases: ${Formatters.formatCurrency(totalCostOfPurchases)}\n")
            append("Total Operating Expenses: ${Formatters.formatCurrency(totalExpenses)}\n")
            append("Net Profit: ${Formatters.formatCurrency(netProfit)}\n")
            append("Outstanding Receivables: ${Formatters.formatCurrency(state.totalReceivables)}\n")
            append("Outstanding Payables: ${Formatters.formatCurrency(state.totalPayables)}\n\n")
            append("Generated by BillNova App on ${Formatters.formatDate(System.currentTimeMillis())}")
        }
        val intent = Intent(Intent.ACTION_SEND).apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, reportText)
            type = "text/plain"
        }
        context.startActivity(Intent.createChooser(intent, "Share Report via"))
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & GST Accounting", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = { shareReportText() }, modifier = Modifier.testTag("btn_share_report")) {
                        Icon(imageVector = Icons.Default.Share, contentDescription = "Share Report", tint = PrimaryBlue)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            ScrollableTabRow(
                selectedTabIndex = selectedReportTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedReportTab == 0,
                    onClick = { selectedReportTab = 0 },
                    text = { Text("Profit & Loss (P&L)", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedReportTab == 1,
                    onClick = { selectedReportTab = 1 },
                    text = { Text("GSTR-1 Tax Summary", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedReportTab == 2,
                    onClick = { selectedReportTab = 2 },
                    text = { Text("Daybook Cashflow", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedReportTab == 3,
                    onClick = { selectedReportTab = 3 },
                    text = { Text("Expenses (${state.expenses.size})", fontWeight = FontWeight.SemiBold) }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                when (selectedReportTab) {
                    0 -> {
                        // Profit & Loss Report View
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().testTag("pnl_summary_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = "Profit & Loss Summary", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                    ReportRow(label = "Total Sales Revenue (+)", amount = totalRevenue, isPositive = true)
                                    ReportRow(label = "Cost of Purchases (-)", amount = totalCostOfPurchases, isPositive = false)

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    ReportRow(label = "Gross Profit", amount = grossProfit, isBold = true, isPositive = true)
                                    ReportRow(label = "Total Operating Expenses (-)", amount = totalExpenses, isPositive = false)

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = "Net Business Profit", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                                        Text(
                                            text = Formatters.formatCurrency(netProfit),
                                            style = MaterialTheme.typography.titleLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = if (netProfit >= 0) SuccessGreen else DangerRed
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // GSTR-1 Tax Summary View
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth().testTag("gstr1_card"),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = "GSTR-1 GST Sales Tax Return", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(
                                        text = "GSTIN: ${state.profile?.gstin ?: "Registered"} • State Code: ${state.profile?.stateCode ?: "27"}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    ReportRow(label = "Total Taxable Turnover", amount = totalTaxableRevenue, isBold = true)
                                    ReportRow(label = "Central GST (CGST)", amount = salesInvoices.sumOf { it.invoice.cgstAmount })
                                    ReportRow(label = "State GST (SGST)", amount = salesInvoices.sumOf { it.invoice.sgstAmount })
                                    ReportRow(label = "Integrated GST (IGST)", amount = salesInvoices.sumOf { it.invoice.igstAmount })

                                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                                    ReportRow(label = "Total GST Tax Output", amount = totalGstCollected, isBold = true, isPositive = true)
                                }
                            }
                        }
                    }

                    2 -> {
                        // Daybook Cashflow View
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(text = "Payment Mode Breakdown", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                    val cashIn = state.allInvoices.filter { it.invoice.paymentMode == PaymentMode.CASH }.sumOf { it.invoice.paidAmount }
                                    val upiIn = state.allInvoices.filter { it.invoice.paymentMode == PaymentMode.UPI }.sumOf { it.invoice.paidAmount }
                                    val bankIn = state.allInvoices.filter { it.invoice.paymentMode == PaymentMode.BANK_TRANSFER }.sumOf { it.invoice.paidAmount }

                                    ReportRow(label = "Cash Inflow", amount = cashIn)
                                    ReportRow(label = "UPI / QR Inflow", amount = upiIn)
                                    ReportRow(label = "Bank Transfer Inflow", amount = bankIn)
                                    ReportRow(label = "Credit / Pending (Udhaar)", amount = state.totalReceivables)
                                }
                            }
                        }
                    }

                    3 -> {
                        // Expenses Tab
                        item {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Operating Expenses (${Formatters.formatCurrency(totalExpenses)})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )

                                Button(
                                    onClick = { showAddExpenseDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_add_expense")
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("+ Record Expense", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        if (state.expenses.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                    Text(text = "No expenses recorded yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else {
                            items(state.expenses, key = { it.id }) { exp ->
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(text = exp.partyName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                                            Text(
                                                text = "${exp.expenseCategory?.label ?: "Expense"} • ${Formatters.formatDate(exp.date)}",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            if (exp.notes.isNotBlank()) {
                                                Text(text = exp.notes, style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 11.sp)
                                            }
                                        }

                                        Text(
                                            text = "- ${Formatters.formatCurrency(exp.amount)}",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = DangerRed
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(70.dp))
                }
            }
        }
    }

    if (showAddExpenseDialog) {
        AddExpenseDialog(
            onDismiss = { showAddExpenseDialog = false },
            onSave = { title, amt, cat, mode, ref, notes ->
                viewModel.recordExpense(title, amt, cat, mode, ref, notes)
                showAddExpenseDialog = false
            }
        )
    }
}

@Composable
fun ReportRow(
    label: String,
    amount: Double,
    isBold: Boolean = false,
    isPositive: Boolean? = null
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = Formatters.formatCurrency(amount),
            style = if (isBold) MaterialTheme.typography.bodyLarge else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = when (isPositive) {
                true -> SuccessGreen
                false -> DangerRed
                null -> MaterialTheme.colorScheme.onSurface
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    onDismiss: () -> Unit,
    onSave: (title: String, amount: Double, category: ExpenseCategory, mode: PaymentMode, ref: String, notes: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(ExpenseCategory.RENT) }
    var selectedMode by remember { mutableStateOf(PaymentMode.UPI) }
    var refNo by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Business Expense", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Expense Title / Payee *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount (₹) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                // Category selector
                ExposedDropdownMenuBox(
                    expanded = categoryDropdownExpanded,
                    onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                ) {
                    OutlinedTextField(
                        value = selectedCategory.label,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        ExpenseCategory.values().forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.label) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Receipt Reference") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amount.toDoubleOrNull()
                    if (title.isNotBlank() && amt != null && amt > 0) {
                        onSave(title, amt, selectedCategory, selectedMode, refNo, notes)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue)
            ) {
                Text("Save Expense")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
