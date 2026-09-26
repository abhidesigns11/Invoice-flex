package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.data.model.UserProfile
import com.example.ui.components.InvoiceFlexLogo
import com.example.ui.components.QrCodeGenerator
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingWizardScreen(
    state: BillingUiState = BillingUiState(),
    viewModel: BillingViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    authViewModel: AuthViewModel? = null,
    onFinish: () -> Unit = {}
) {
    val context = LocalContext.current
    var currentStep by remember { mutableIntStateOf(1) } // 1: Personal, 2: Company & GST, 3: Logo & Sign, 4: Bank & QR, 5: Starting Look

    val existing = state.profile ?: BusinessProfile()

    // Step 1: Personal Q&A
    var ownerName by remember { mutableStateOf("Abhishek Panchal") }
    var selectedGender by remember { mutableStateOf("Male") }
    var userPhone by remember { mutableStateOf(existing.phone.ifEmpty { "+91 98220 98765" }) }
    var userEmail by remember { mutableStateOf(existing.email.ifEmpty { "abhishek@apexsteel.in" }) }

    // Step 2: Company & GST
    var companyName by remember { mutableStateOf(existing.businessName) }
    var companyTagline by remember { mutableStateOf(existing.tagline) }
    var industryCategory by remember { mutableStateOf("Stainless Steel Fabrication & Engineering") }
    var isGstRegistered by remember { mutableStateOf(existing.isGstEnabled) }
    var gstin by remember { mutableStateOf(existing.gstin) }
    var address by remember { mutableStateOf(existing.address) }
    var city by remember { mutableStateOf(existing.city) }
    var stateName by remember { mutableStateOf(existing.state) }
    var pincode by remember { mutableStateOf(existing.pincode) }

    // Step 3: Logo & Signature
    var signatureType by remember { mutableStateOf("Text / Digital Font") } // Text / Digital Font, Uploaded Image, Drawn
    var signatureText by remember { mutableStateOf("Authorized Signatory") }
    var hasStamp by remember { mutableStateOf(true) }

    // Step 4: Bank & QR
    var bankName by remember { mutableStateOf(existing.bankName) }
    var accountHolder by remember { mutableStateOf(existing.accountHolderName.ifEmpty { companyName }) }
    var accountNumber by remember { mutableStateOf(existing.accountNumber) }
    var ifscCode by remember { mutableStateOf(existing.ifscCode) }
    var branch by remember { mutableStateOf(existing.branch) }
    var upiId by remember { mutableStateOf(existing.upiId) }

    // Step 5: Starting Look & Theme
    var selectedThemeStyle by remember { mutableStateOf("Advance GST") }
    var selectedColorHex by remember { mutableStateOf(0xFF0066FF) } // Invoice Flex Blue

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InvoiceFlexLogo(size = 32.dp, showText = true, subtitle = "Business Setup Wizard")
                    }
                },
                navigationIcon = {
                    if (currentStep > 1) {
                        IconButton(onClick = { currentStep-- }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        bottomBar = {
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = PureWhite
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(
                            onClick = { currentStep-- },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Previous")
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }

                    Button(
                        onClick = {
                            if (currentStep < 5) {
                                currentStep++
                            } else {
                                // Save profile into ViewModel
                                val updated = existing.copy(
                                    businessName = companyName.ifEmpty { "Apex Stainless Steel & Engineering Works" },
                                    tagline = companyTagline,
                                    phone = userPhone,
                                    email = userEmail,
                                    address = address,
                                    city = city,
                                    state = stateName,
                                    pincode = pincode,
                                    gstin = if (isGstRegistered) gstin else "",
                                    upiId = upiId,
                                    bankName = bankName,
                                    accountHolderName = accountHolder,
                                    accountNumber = accountNumber,
                                    ifscCode = ifscCode,
                                    branch = branch,
                                    isGstEnabled = isGstRegistered
                                )
                                viewModel.saveBusinessProfile(updated)

                                if (authViewModel != null) {
                                    val userProfile = UserProfile(
                                        ownerName = ownerName,
                                        gender = selectedGender,
                                        companyName = companyName.ifEmpty { "Apex Stainless Steel & Engineering Works" },
                                        phone = userPhone,
                                        email = userEmail,
                                        address = address,
                                        city = city,
                                        state = stateName,
                                        pincode = pincode,
                                        gstin = if (isGstRegistered) gstin else "",
                                        isGstEnabled = isGstRegistered,
                                        bankName = bankName,
                                        accountHolderName = accountHolder,
                                        accountNumber = accountNumber,
                                        ifscCode = ifscCode,
                                        branch = branch,
                                        upiId = upiId,
                                        onboardingComplete = true
                                    )
                                    authViewModel.completeOnboarding(userProfile, logoUri = null)
                                }

                                Toast.makeText(context, "Welcome to Invoice Flex! Profile configured successfully.", Toast.LENGTH_LONG).show()
                                onFinish()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        modifier = Modifier.weight(1f)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(if (currentStep == 5) "Launch Workspace" else "Next Step")
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                if (currentStep == 5) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Progress Bar
            LinearProgressIndicator(
                progress = { currentStep / 5f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp),
                color = PrimaryCobalt,
                trackColor = Color(0xFFE2E8F0)
            )

            // Step Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when (currentStep) {
                        1 -> "Step 1 of 5: Personal Profile"
                        2 -> "Step 2 of 5: Company & GST"
                        3 -> "Step 3 of 5: Logo & Signature"
                        4 -> "Step 4 of 5: Bank Details & QR"
                        else -> "Step 5 of 5: Select Invoice Look"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = PrimaryCobalt,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${(currentStep * 20)}% Complete",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary
                )
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    when (currentStep) {
                        1 -> Step1Personal(
                            ownerName = ownerName,
                            onOwnerNameChange = { ownerName = it },
                            selectedGender = selectedGender,
                            onGenderChange = { selectedGender = it },
                            userPhone = userPhone,
                            onUserPhoneChange = { userPhone = it },
                            userEmail = userEmail,
                            onUserEmailChange = { userEmail = it }
                        )

                        2 -> Step2CompanyGst(
                            companyName = companyName,
                            onCompanyNameChange = { companyName = it },
                            tagline = companyTagline,
                            onTaglineChange = { companyTagline = it },
                            category = industryCategory,
                            onCategoryChange = { industryCategory = it },
                            isGst = isGstRegistered,
                            onGstToggle = { isGstRegistered = it },
                            gstin = gstin,
                            onGstinChange = { gstin = it },
                            address = address,
                            onAddressChange = { address = it },
                            city = city,
                            onCityChange = { city = it },
                            state = stateName,
                            onStateChange = { stateName = it },
                            pincode = pincode,
                            onPincodeChange = { pincode = it }
                        )

                        3 -> Step3LogoSignature(
                            signatureType = signatureType,
                            onSignatureTypeChange = { signatureType = it },
                            signatureText = signatureText,
                            onSignatureTextChange = { signatureText = it },
                            hasStamp = hasStamp,
                            onStampToggle = { hasStamp = it }
                        )

                        4 -> Step4BankQr(
                            bankName = bankName,
                            onBankNameChange = { bankName = it },
                            accountHolder = accountHolder,
                            onAccountHolderChange = { accountHolder = it },
                            accountNumber = accountNumber,
                            onAccountNumberChange = { accountNumber = it },
                            ifsc = ifscCode,
                            onIfscChange = { ifscCode = it },
                            branch = branch,
                            onBranchChange = { branch = it },
                            upiId = upiId,
                            onUpiIdChange = { upiId = it }
                        )

                        5 -> Step5InvoiceLook(
                            selectedTheme = selectedThemeStyle,
                            onThemeSelect = { selectedThemeStyle = it },
                            selectedColor = selectedColorHex,
                            onColorSelect = { selectedColorHex = it },
                            companyName = companyName,
                            upiId = upiId
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
private fun Step1Personal(
    ownerName: String,
    onOwnerNameChange: (String) -> Unit,
    selectedGender: String,
    onGenderChange: (String) -> Unit,
    userPhone: String,
    onUserPhoneChange: (String) -> Unit,
    userEmail: String,
    onUserEmailChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryCobalt.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryCobalt)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Owner & Account Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Enter who is managing this Invoice Flex workspace", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = ownerName,
                onValueChange = onOwnerNameChange,
                label = { Text("Your Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Gender", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf("Male", "Female", "Other").forEach { g ->
                    FilterChip(
                        selected = selectedGender == g,
                        onClick = { onGenderChange(g) },
                        label = { Text(g) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCobalt,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = userPhone,
                onValueChange = onUserPhoneChange,
                label = { Text("Primary Phone / WhatsApp") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = userEmail,
                onValueChange = onUserEmailChange,
                label = { Text("Business Email Address") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
        }
    }
}

@Composable
private fun Step2CompanyGst(
    companyName: String,
    onCompanyNameChange: (String) -> Unit,
    tagline: String,
    onTaglineChange: (String) -> Unit,
    category: String,
    onCategoryChange: (String) -> Unit,
    isGst: Boolean,
    onGstToggle: (Boolean) -> Unit,
    gstin: String,
    onGstinChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    state: String,
    onStateChange: (String) -> Unit,
    pincode: String,
    onPincodeChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryCobalt.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Business, contentDescription = null, tint = PrimaryCobalt)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Business & GST Configuration", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("This appears on printed invoices and legal reports", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = companyName,
                onValueChange = onCompanyNameChange,
                label = { Text("Company / Trade Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = tagline,
                onValueChange = onTaglineChange,
                label = { Text("Business Subtitle / Tagline") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Industry Segment", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.SemiBold)
            val segments = listOf(
                "Stainless Steel Fabrication & Engineering",
                "Wholesale & Distribution",
                "Manufacturing & Industrial",
                "Retail & Supermarket"
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(top = 4.dp)) {
                segments.forEach { seg ->
                    FilterChip(
                        selected = category == seg,
                        onClick = { onCategoryChange(seg) },
                        label = { Text(seg, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCobalt,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF1F5F9), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("My Business is GST Registered", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Enables CGST, SGST & IGST tax columns", color = TextSecondary, fontSize = 11.sp)
                }
                Switch(
                    checked = isGst,
                    onCheckedChange = onGstToggle,
                    colors = SwitchDefaults.colors(checkedThumbColor = PureWhite, checkedTrackColor = PrimaryCobalt)
                )
            }

            AnimatedVisibility(visible = isGst) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    OutlinedTextField(
                        value = gstin,
                        onValueChange = { onGstinChange(it.uppercase()) },
                        label = { Text("15-Digit GSTIN (e.g. 27AAACA9876F1Z4)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                label = { Text("Works / Factory Address") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = city,
                    onValueChange = onCityChange,
                    label = { Text("City") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = state,
                    onValueChange = onStateChange,
                    label = { Text("State") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                OutlinedTextField(
                    value = pincode,
                    onValueChange = onPincodeChange,
                    label = { Text("Pincode") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }
    }
}

@Composable
private fun Step3LogoSignature(
    signatureType: String,
    onSignatureTypeChange: (String) -> Unit,
    signatureText: String,
    onSignatureTextChange: (String) -> Unit,
    hasStamp: Boolean,
    onStampToggle: (Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryCobalt.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Draw, contentDescription = null, tint = PrimaryCobalt)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Logo & Authorized Signature", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Add visual authority to all generated Tax Invoices", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Invoice Flex Official Logo Badge", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, Color(0xFFE2E8F0), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InvoiceFlexLogo(size = 48.dp, showText = true, subtitle = "Verified Digital Invoicing")
                Spacer(modifier = Modifier.weight(1f))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFF10B981).copy(alpha = 0.15f)
                ) {
                    Text("ACTIVE", color = Color(0xFF059669), fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("Signature Style", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                listOf("Text / Digital Font", "Draw Sign Pad").forEach { st ->
                    FilterChip(
                        selected = signatureType == st,
                        onClick = { onSignatureTypeChange(st) },
                        label = { Text(st) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCobalt,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = signatureText,
                onValueChange = onSignatureTextChange,
                label = { Text("Signatory Designation") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Live Signature Preview Box
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                shape = RoundedCornerShape(12.dp),
                color = Color(0xFFFAF5FF),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE9D5FF))
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Abhishek Panchal",
                            fontFamily = FontFamily.Cursive,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            color = Color(0xFF6B21A8)
                        )
                        Text(
                            text = signatureText,
                            fontSize = 11.sp,
                            color = Color(0xFF7E22CE),
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Print Authorized Company Stamp", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                Switch(checked = hasStamp, onCheckedChange = onStampToggle)
            }
        }
    }
}

@Composable
private fun Step4BankQr(
    bankName: String,
    onBankNameChange: (String) -> Unit,
    accountHolder: String,
    onAccountHolderChange: (String) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    ifsc: String,
    onIfscChange: (String) -> Unit,
    branch: String,
    onBranchChange: (String) -> Unit,
    upiId: String,
    onUpiIdChange: (String) -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryCobalt.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AccountBalance, contentDescription = null, tint = PrimaryCobalt)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Bank Account & UPI Payment QR", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("Customers scan to pay directly on invoice delivery", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = bankName,
                onValueChange = onBankNameChange,
                label = { Text("Bank Name (e.g. State Bank of India)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = accountHolder,
                onValueChange = onAccountHolderChange,
                label = { Text("Account Holder Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = onAccountNumberChange,
                    label = { Text("Account Number") },
                    modifier = Modifier.weight(1.3f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = ifsc,
                    onValueChange = { onIfscChange(it.uppercase()) },
                    label = { Text("IFSC Code") },
                    modifier = Modifier.weight(0.9f),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = branch,
                onValueChange = onBranchChange,
                label = { Text("Branch Location") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = upiId,
                onValueChange = onUpiIdChange,
                label = { Text("UPI ID (e.g. apexsteel@upi)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Live UPI QR Preview Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFEFF6FF), RoundedCornerShape(12.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                QrCodeGenerator(
                    data = "upi://pay?pa=$upiId&pn=$accountHolder&cu=INR",
                    modifier = Modifier.size(72.dp)
                )
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text("Instant Scan & Pay QR", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PrimaryCobalt)
                    Text("Generated automatically for $upiId", fontSize = 11.sp, color = TextSecondary)
                    Text("Works with Google Pay, PhonePe & Paytm", fontSize = 10.sp, color = Color(0xFF64748B))
                }
            }
        }
    }
}

@Composable
private fun Step5InvoiceLook(
    selectedTheme: String,
    onThemeSelect: (String) -> Unit,
    selectedColor: Long,
    onColorSelect: (Long) -> Unit,
    companyName: String,
    upiId: String
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(PrimaryCobalt.copy(alpha = 0.1f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ColorLens, contentDescription = null, tint = PrimaryCobalt)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Choose Starting Invoice Look", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text("You can change or design your own anytime in Settings", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Presets from reference
            val presets = listOf("Advance GST", "Luxury", "Stylish", "Simple", "Modern")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.forEach { pr ->
                    FilterChip(
                        selected = selectedTheme == pr,
                        onClick = { onThemeSelect(pr) },
                        label = { Text(pr, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryCobalt,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Color Theme Palette", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            val colors = listOf(
                0xFF0066FF to "Invoice Flex Blue",
                0xFF1E293B to "Charcoal Black",
                0xFF15803D to "Forest Green",
                0xFF7E22CE to "Royal Purple",
                0xFFDC2626 to "Crimson Red",
                0xFFD97706 to "Mustard Gold"
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                colors.forEach { (cHex, name) ->
                    val color = Color(cHex)
                    val isSelected = selectedColor == cHex
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(color, CircleShape)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) PrimaryCobalt else Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable { onColorSelect(cHex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = name, tint = PureWhite, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Paper Preview Representation
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                shape = RoundedCornerShape(8.dp),
                color = Color.White,
                shadowElevation = 3.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCBD5E1))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    // Top Bar with chosen color
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(selectedColor).copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                            .padding(6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            InvoiceFlexLogo(size = 20.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(companyName.ifEmpty { "Apex Stainless Steel" }, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color(selectedColor))
                        }
                        Text("TAX INVOICE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(selectedColor))
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Bill To
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("BILL TO: Kirloskar Brothers Ltd", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                            Text("GSTIN: 27AAACK1122D1Z8", fontSize = 8.sp, color = TextSecondary)
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("INV-2026-089", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            Text("Date: 26/09/2026", fontSize = 8.sp, color = TextSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Table Mock
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(selectedColor))
                            .padding(horizontal = 6.dp, vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("ITEM DESCRIPTION", fontSize = 8.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                        Text("QTY", fontSize = 8.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                        Text("RATE", fontSize = 8.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                        Text("AMOUNT", fontSize = 8.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(3.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("S.S. 304 Staff Locker (12 Door)", fontSize = 8.sp)
                        Text("2 Pcs", fontSize = 8.sp)
                        Text("₹24,500", fontSize = 8.sp)
                        Text("₹49,000", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Total
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, Color(selectedColor).copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Total Amount (Incl. 18% GST)", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text("₹57,820.00", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = Color(selectedColor))
                    }
                }
            }
        }
    }
}
