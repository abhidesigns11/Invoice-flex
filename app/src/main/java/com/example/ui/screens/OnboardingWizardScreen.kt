package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.model.UserProfile
import com.example.ui.theme.CanvasBg
import com.example.ui.theme.ErrorCrimson
import com.example.ui.theme.OnSurfaceObsidian
import com.example.ui.theme.OutlineHairline
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PrimaryFixed
import com.example.ui.theme.SurfaceContainerLow
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.SessionStatus

private enum class OnboardingStep(val title: String) {
    PERSONAL("About You"),
    COMPANY("Your Company"),
    CONTACT("Contact & Address"),
    BANK("Bank Details"),
    REVIEW("Review & Finish")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingWizardScreen(
    authViewModel: AuthViewModel,
    onFinish: () -> Unit
) {
    val authState by authViewModel.uiState.collectAsStateWithLifecycle()
    // Snapshot the existing profile once, so re-opening this screen to edit an
    // already-completed setup (from Account Settings) starts pre-filled instead of blank.
    val existingProfile = remember { authState.profile }

    var step by remember { mutableStateOf(OnboardingStep.PERSONAL) }
    var hasSubmitted by remember { mutableStateOf(false) }

    // Step 1
    var ownerName by remember { mutableStateOf(existingProfile?.ownerName ?: "") }
    var gender by remember { mutableStateOf(existingProfile?.gender ?: "") }

    // Step 2
    var companyName by remember { mutableStateOf(existingProfile?.companyName ?: "") }
    var website by remember { mutableStateOf(existingProfile?.website ?: "") }
    var gstin by remember { mutableStateOf(existingProfile?.gstin ?: "") }
    var isGstEnabled by remember { mutableStateOf(existingProfile?.isGstEnabled ?: false) }
    var logoUri by remember { mutableStateOf<Uri?>(null) }
    val existingLogoUrl = existingProfile?.logoUrl ?: ""

    val logoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) logoUri = uri
    }

    // Step 3
    var phone by remember { mutableStateOf(existingProfile?.phone ?: "") }
    var email by remember { mutableStateOf(existingProfile?.email ?: authState.userEmail ?: "") }
    var address by remember { mutableStateOf(existingProfile?.address ?: "") }
    var city by remember { mutableStateOf(existingProfile?.city ?: "") }
    var state by remember { mutableStateOf(existingProfile?.state ?: "") }
    var pincode by remember { mutableStateOf(existingProfile?.pincode ?: "") }

    // Step 4
    var bankName by remember { mutableStateOf(existingProfile?.bankName ?: "") }
    var accountHolderName by remember { mutableStateOf(existingProfile?.accountHolderName ?: "") }
    var accountNumber by remember { mutableStateOf(existingProfile?.accountNumber ?: "") }
    var ifscCode by remember { mutableStateOf(existingProfile?.ifscCode ?: "") }
    var branch by remember { mutableStateOf(existingProfile?.branch ?: "") }
    var upiId by remember { mutableStateOf(existingProfile?.upiId ?: "") }

    // Only auto-navigate forward once *this* submission finishes successfully —
    // never just because the account already happened to be fully set up already.
    LaunchedEffect(authState.sessionStatus, hasSubmitted) {
        if (hasSubmitted && authState.sessionStatus == SessionStatus.READY) {
            onFinish()
        }
    }

    fun buildProfileDraft() = UserProfile(
        ownerName = ownerName,
        gender = gender,
        companyName = companyName,
        logoUrl = existingLogoUrl,
        website = website,
        gstin = gstin,
        isGstEnabled = isGstEnabled,
        phone = phone,
        email = email,
        address = address,
        city = city,
        state = state,
        pincode = pincode,
        bankName = bankName,
        accountHolderName = accountHolderName,
        accountNumber = accountNumber,
        ifscCode = ifscCode,
        branch = branch,
        upiId = upiId
    )

    val stepIndex = OnboardingStep.entries.indexOf(step)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CanvasBg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header with progress
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (stepIndex > 0) {
                        IconButton(onClick = {
                            step = OnboardingStep.entries[stepIndex - 1]
                        }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(48.dp))
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Column {
                        Text(
                            text = "Step ${stepIndex + 1} of ${OnboardingStep.entries.size}",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                        Text(
                            text = step.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceObsidian
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OnboardingStep.entries.forEachIndexed { index, _ ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(if (index <= stepIndex) PrimaryCobalt else OutlineHairline)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                when (step) {
                    OnboardingStep.PERSONAL -> PersonalStep(
                        ownerName = ownerName,
                        onOwnerNameChange = { ownerName = it },
                        gender = gender,
                        onGenderChange = { gender = it }
                    )

                    OnboardingStep.COMPANY -> CompanyStep(
                        companyName = companyName,
                        onCompanyNameChange = { companyName = it },
                        website = website,
                        onWebsiteChange = { website = it },
                        gstin = gstin,
                        onGstinChange = { gstin = it },
                        isGstEnabled = isGstEnabled,
                        onGstEnabledChange = { isGstEnabled = it },
                        logoUri = logoUri,
                        existingLogoUrl = existingLogoUrl,
                        onPickLogo = { logoPicker.launch("image/*") }
                    )

                    OnboardingStep.CONTACT -> ContactStep(
                        phone = phone,
                        onPhoneChange = { phone = it },
                        email = email,
                        onEmailChange = { email = it },
                        address = address,
                        onAddressChange = { address = it },
                        city = city,
                        onCityChange = { city = it },
                        state = state,
                        onStateChange = { state = it },
                        pincode = pincode,
                        onPincodeChange = { pincode = it }
                    )

                    OnboardingStep.BANK -> BankStep(
                        bankName = bankName,
                        onBankNameChange = { bankName = it },
                        accountHolderName = accountHolderName,
                        onAccountHolderNameChange = { accountHolderName = it },
                        accountNumber = accountNumber,
                        onAccountNumberChange = { accountNumber = it },
                        ifscCode = ifscCode,
                        onIfscChange = { ifscCode = it },
                        branch = branch,
                        onBranchChange = { branch = it },
                        upiId = upiId,
                        onUpiChange = { upiId = it }
                    )

                    OnboardingStep.REVIEW -> ReviewStep(
                        ownerName = ownerName,
                        companyName = companyName,
                        phone = phone,
                        email = email,
                        address = listOf(address, city, state, pincode).filter { it.isNotBlank() }.joinToString(", "),
                        bankName = bankName,
                        accountNumber = accountNumber
                    )
                }

                if (authState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = authState.errorMessage ?: "", color = ErrorCrimson, fontSize = 13.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // Bottom action bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (stepIndex < OnboardingStep.entries.size - 1) {
                    Button(
                        onClick = {
                            val canProceed = when (step) {
                                OnboardingStep.PERSONAL -> ownerName.isNotBlank()
                                OnboardingStep.COMPANY -> companyName.isNotBlank()
                                else -> true
                            }
                            if (canProceed) {
                                step = OnboardingStep.entries[stepIndex + 1]
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                    ) {
                        Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = {
                            hasSubmitted = true
                            authViewModel.completeOnboarding(buildProfileDraft(), logoUri)
                        },
                        enabled = !authState.isSubmitting,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt)
                    ) {
                        if (authState.isSubmitting) {
                            CircularProgressIndicator(modifier = Modifier.height(22.dp), color = Color.White)
                        } else {
                            Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Finish Setup", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StepField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp),
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryCobalt)
    )
}

@Composable
private fun PersonalStep(
    ownerName: String,
    onOwnerNameChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text("Let's start with a little about you.", color = TextSecondary, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(16.dp))

    StepField(value = ownerName, onValueChange = onOwnerNameChange, label = "Your full name")

    Text("Gender", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = OnSurfaceObsidian)
    Spacer(modifier = Modifier.height(8.dp))
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        listOf("Male", "Female", "Other").forEach { option ->
            FilterChip(
                selected = gender == option,
                onClick = { onGenderChange(option) },
                label = { Text(option) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = PrimaryFixed,
                    selectedLabelColor = PrimaryCobalt
                )
            )
        }
    }
}

@Composable
private fun CompanyStep(
    companyName: String,
    onCompanyNameChange: (String) -> Unit,
    website: String,
    onWebsiteChange: (String) -> Unit,
    gstin: String,
    onGstinChange: (String) -> Unit,
    isGstEnabled: Boolean,
    onGstEnabledChange: (Boolean) -> Unit,
    logoUri: Uri?,
    existingLogoUrl: String = "",
    onPickLogo: () -> Unit
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text("Tell us about your business.", color = TextSecondary, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(16.dp))

    Box(
        modifier = Modifier
            .size(96.dp)
            .clip(CircleShape)
            .background(SurfaceContainerLow)
            .border(1.dp, OutlineHairline, CircleShape)
            .clickable { onPickLogo() },
        contentAlignment = Alignment.Center
    ) {
        if (logoUri != null || existingLogoUrl.isNotBlank()) {
            AsyncImage(
                model = logoUri ?: existingLogoUrl,
                contentDescription = "Company logo",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
        } else {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.AddAPhoto, contentDescription = "Add logo", tint = PrimaryCobalt)
                Text("Add logo", fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
    Spacer(modifier = Modifier.height(20.dp))

    StepField(value = companyName, onValueChange = onCompanyNameChange, label = "Company name")
    StepField(value = website, onValueChange = onWebsiteChange, label = "Website (optional)")

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("This business is GST registered", modifier = Modifier.weight(1f), fontSize = 14.sp, color = OnSurfaceObsidian)
        Switch(
            checked = isGstEnabled,
            onCheckedChange = onGstEnabledChange,
            colors = SwitchDefaults.colors(checkedThumbColor = PrimaryCobalt, checkedTrackColor = PrimaryFixed)
        )
    }
    if (isGstEnabled) {
        Spacer(modifier = Modifier.height(8.dp))
        StepField(value = gstin, onValueChange = onGstinChange, label = "GSTIN")
    }
}

@Composable
private fun ContactStep(
    phone: String,
    onPhoneChange: (String) -> Unit,
    email: String,
    onEmailChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit,
    city: String,
    onCityChange: (String) -> Unit,
    state: String,
    onStateChange: (String) -> Unit,
    pincode: String,
    onPincodeChange: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text("How can customers reach your business?", color = TextSecondary, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(16.dp))

    StepField(value = phone, onValueChange = onPhoneChange, label = "Phone number", keyboardType = KeyboardType.Phone)
    StepField(value = email, onValueChange = onEmailChange, label = "Business email", keyboardType = KeyboardType.Email)
    StepField(value = address, onValueChange = onAddressChange, label = "Address")

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box(modifier = Modifier.weight(1f)) {
            StepField(value = city, onValueChange = onCityChange, label = "City")
        }
        Box(modifier = Modifier.weight(1f)) {
            StepField(value = state, onValueChange = onStateChange, label = "State")
        }
    }
    StepField(value = pincode, onValueChange = onPincodeChange, label = "Pincode", keyboardType = KeyboardType.Number)
}

@Composable
private fun BankStep(
    bankName: String,
    onBankNameChange: (String) -> Unit,
    accountHolderName: String,
    onAccountHolderNameChange: (String) -> Unit,
    accountNumber: String,
    onAccountNumberChange: (String) -> Unit,
    ifscCode: String,
    onIfscChange: (String) -> Unit,
    branch: String,
    onBranchChange: (String) -> Unit,
    upiId: String,
    onUpiChange: (String) -> Unit
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text(
        "Used to show payment details on your invoices. You can skip this and add it later from Settings.",
        color = TextSecondary,
        fontSize = 14.sp
    )
    Spacer(modifier = Modifier.height(16.dp))

    StepField(value = bankName, onValueChange = onBankNameChange, label = "Bank name")
    StepField(value = accountHolderName, onValueChange = onAccountHolderNameChange, label = "Account holder name")
    StepField(value = accountNumber, onValueChange = onAccountNumberChange, label = "Account number", keyboardType = KeyboardType.Number)
    StepField(value = ifscCode, onValueChange = onIfscChange, label = "IFSC code")
    StepField(value = branch, onValueChange = onBranchChange, label = "Branch")
    StepField(value = upiId, onValueChange = onUpiChange, label = "UPI ID (optional)")
}

@Composable
private fun ReviewStep(
    ownerName: String,
    companyName: String,
    phone: String,
    email: String,
    address: String,
    bankName: String,
    accountNumber: String
) {
    Spacer(modifier = Modifier.height(8.dp))
    Text("Quick check before we finish.", color = TextSecondary, fontSize = 14.sp)
    Spacer(modifier = Modifier.height(16.dp))

    ReviewRow(label = "Owner", value = ownerName)
    ReviewRow(label = "Company", value = companyName)
    ReviewRow(label = "Phone", value = phone)
    ReviewRow(label = "Email", value = email)
    ReviewRow(label = "Address", value = address)
    ReviewRow(label = "Bank", value = bankName)
    ReviewRow(label = "Account No.", value = maskedAccountNumber(accountNumber))
}

private fun maskedAccountNumber(accountNumber: String): String {
    if (accountNumber.length <= 4) return accountNumber
    return "•".repeat(accountNumber.length - 4) + accountNumber.takeLast(4)
}

@Composable
private fun ReviewRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(label, modifier = Modifier.width(110.dp), color = TextSecondary, fontSize = 13.sp)
        Text(
            text = value.ifBlank { "—" },
            color = OnSurfaceObsidian,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}
