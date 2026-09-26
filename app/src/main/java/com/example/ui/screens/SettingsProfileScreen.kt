package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.ui.components.Formatters
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SuccessGreen
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsProfileScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToHsnFinder: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToPos: () -> Unit = {}
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Business Profile, 1: GST Calculator, 2: Bank & UPI

    val profile = state.profile ?: BusinessProfile()

    var businessName by remember(profile) { mutableStateOf(profile.businessName) }
    var tagline by remember(profile) { mutableStateOf(profile.tagline) }
    var phone by remember(profile) { mutableStateOf(profile.phone) }
    var email by remember(profile) { mutableStateOf(profile.email) }
    var address by remember(profile) { mutableStateOf(profile.address) }
    var city by remember(profile) { mutableStateOf(profile.city) }
    var gstState by remember(profile) { mutableStateOf(profile.state) }
    var stateCode by remember(profile) { mutableStateOf(profile.stateCode) }
    var pincode by remember(profile) { mutableStateOf(profile.pincode) }
    var gstin by remember(profile) { mutableStateOf(profile.gstin) }

    var upiId by remember(profile) { mutableStateOf(profile.upiId) }
    var bankName by remember(profile) { mutableStateOf(profile.bankName) }
    var accNumber by remember(profile) { mutableStateOf(profile.accountNumber) }
    var ifscCode by remember(profile) { mutableStateOf(profile.ifscCode) }
    var terms by remember(profile) { mutableStateOf(profile.termsAndConditions) }

    fun saveChanges() {
        val updated = profile.copy(
            businessName = businessName,
            tagline = tagline,
            phone = phone,
            email = email,
            address = address,
            city = city,
            state = gstState,
            stateCode = stateCode,
            pincode = pincode,
            gstin = gstin,
            upiId = upiId,
            bankName = bankName,
            accountNumber = accNumber,
            ifscCode = ifscCode,
            termsAndConditions = terms
        )
        viewModel.saveBusinessProfile(updated)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business Settings & Tools", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("settings_back_btn")) {
                        Icon(imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to Home")
                    }
                },
                actions = {
                    if (selectedTab != 1) {
                        Button(
                            onClick = { saveChanges() },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .testTag("btn_save_business_profile")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save", fontWeight = FontWeight.Bold)
                        }
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
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = PrimaryBlue,
                edgePadding = 16.dp
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Business Info & GST", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("GST Tax Calculator", fontWeight = FontWeight.SemiBold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Bank, UPI & Terms", fontWeight = FontWeight.SemiBold) }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // Quick Tools Shortcuts Card
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "Quick Tools & Engineering Utilities",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = PrimaryNavy
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Button(
                                            onClick = onNavigateToHsnFinder,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF), contentColor = PrimaryBlue),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("HSN Finder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = onNavigateToReports,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0FDF4), contentColor = SuccessGreen),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Reports", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Button(
                                            onClick = onNavigateToPos,
                                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFAF5FF), contentColor = Color(0xFF7E22CE)),
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.weight(1f).height(44.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.QrCode, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("Quick POS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }

                        // Business Info Form
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
                                    Text(text = "Shop / Company Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                    OutlinedTextField(
                                        value = businessName,
                                        onValueChange = { businessName = it },
                                        label = { Text("Business Name *") },
                                        modifier = Modifier.fillMaxWidth().testTag("input_biz_name"),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = tagline,
                                        onValueChange = { tagline = it },
                                        label = { Text("Tagline / Subtitle") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = phone,
                                            onValueChange = { phone = it },
                                            label = { Text("Phone Number") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = email,
                                            onValueChange = { email = it },
                                            label = { Text("Email") },
                                            modifier = Modifier.weight(1f),
                                            singleLine = true
                                        )
                                    }

                                    OutlinedTextField(
                                        value = gstin,
                                        onValueChange = { gstin = it },
                                        label = { Text("GSTIN (GST Identification Number)") },
                                        modifier = Modifier.fillMaxWidth().testTag("input_biz_gstin"),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = address,
                                        onValueChange = { address = it },
                                        label = { Text("Street Address") },
                                        modifier = Modifier.fillMaxWidth()
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = city,
                                            onValueChange = { city = it },
                                            label = { Text("City") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = gstState,
                                            onValueChange = { gstState = it },
                                            label = { Text("State") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = stateCode,
                                            onValueChange = { stateCode = it },
                                            label = { Text("State Code (e.g. 27)") },
                                            modifier = Modifier.weight(1f)
                                        )
                                        OutlinedTextField(
                                            value = pincode,
                                            onValueChange = { pincode = it },
                                            label = { Text("Pincode") },
                                            modifier = Modifier.weight(1f)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    1 -> {
                        // Smart GST Calculator Utility
                        item {
                            SmartGstCalculatorCard()
                        }
                    }

                    2 -> {
                        // Bank & UPI Settings
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
                                    Text(text = "Bank & UPI Payment QR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                                    OutlinedTextField(
                                        value = upiId,
                                        onValueChange = { upiId = it },
                                        label = { Text("UPI ID (for Invoice QR Code) *") },
                                        modifier = Modifier.fillMaxWidth().testTag("input_biz_upi"),
                                        singleLine = true
                                    )

                                    OutlinedTextField(
                                        value = bankName,
                                        onValueChange = { bankName = it },
                                        label = { Text("Bank Name") },
                                        modifier = Modifier.fillMaxWidth(),
                                        singleLine = true
                                    )

                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        OutlinedTextField(
                                            value = accNumber,
                                            onValueChange = { accNumber = it },
                                            label = { Text("Bank A/C Number") },
                                            modifier = Modifier.weight(1.2f),
                                            singleLine = true
                                        )
                                        OutlinedTextField(
                                            value = ifscCode,
                                            onValueChange = { ifscCode = it },
                                            label = { Text("IFSC Code") },
                                            modifier = Modifier.weight(0.8f),
                                            singleLine = true
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(6.dp))

                                    Text(text = "Default Invoice Terms & Conditions", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                    OutlinedTextField(
                                        value = terms,
                                        onValueChange = { terms = it },
                                        label = { Text("Terms & Conditions") },
                                        modifier = Modifier.fillMaxWidth(),
                                        maxLines = 4
                                    )
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
}

@Composable
fun SmartGstCalculatorCard() {
    var amountInput by remember { mutableStateOf("10000") }
    var selectedGstRate by remember { mutableDoubleStateOf(18.0) }
    var isInclusive by remember { mutableStateOf(false) } // false = Exclusive (+ GST), true = Inclusive (Extract GST)

    val amount = amountInput.toDoubleOrNull() ?: 0.0

    val (taxableValue, taxAmount, totalAmount) = remember(amount, selectedGstRate, isInclusive) {
        if (!isInclusive) {
            val tax = amount * (selectedGstRate / 100.0)
            Triple(amount, tax, amount + tax)
        } else {
            val taxable = amount / (1.0 + (selectedGstRate / 100.0))
            val tax = amount - taxable
            Triple(taxable, tax, amount)
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth().testTag("smart_gst_calculator_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Calculate, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Smart GST Tax Calculator", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            OutlinedTextField(
                value = amountInput,
                onValueChange = { amountInput = it },
                label = { Text("Enter Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("input_calc_amount")
            )

            // Exclusive vs Inclusive Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isInclusive) "Inclusive of GST (Reverse Tax)" else "Exclusive of GST (Add Tax)",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = if (isInclusive) "Calculates base price from MRP" else "Adds tax onto the base price",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = isInclusive,
                    onCheckedChange = { isInclusive = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = PrimaryBlue)
                )
            }

            // GST Rate Buttons
            Text(text = "Select GST Slab Rate:", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(0.0, 5.0, 12.0, 18.0, 28.0).forEach { rate ->
                    FilterChip(
                        selected = selectedGstRate == rate,
                        onClick = { selectedGstRate = rate },
                        label = { Text("${rate.toInt()}%") },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

            // Results Card
            Surface(
                color = PrimaryBlueContainer.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Taxable Base Value:", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = Formatters.formatCurrency(taxableValue), fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "CGST (${selectedGstRate / 2}%):", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = Formatters.formatCurrency(taxAmount / 2.0), fontWeight = FontWeight.Medium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "SGST (${selectedGstRate / 2}%):", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = Formatters.formatCurrency(taxAmount / 2.0), fontWeight = FontWeight.Medium)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Total GST Tax Amount:", fontWeight = FontWeight.Bold)
                        Text(text = Formatters.formatCurrency(taxAmount), fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }
                    HorizontalDivider(color = PrimaryBlue.copy(alpha = 0.2f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Final Total (with GST):", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(text = Formatters.formatCurrency(totalAmount), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = PrimaryNavy)
                    }
                }
            }
        }
    }
}
