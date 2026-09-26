package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BusinessProfile
import com.example.ui.theme.*
import com.example.ui.viewmodel.BillingUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountProfileSheet(
    state: BillingUiState,
    onDismiss: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToThemeColor: () -> Unit = {},
    onNavigateToReports: () -> Unit = {},
    onNavigateToParties: () -> Unit = {}
) {
    val context = LocalContext.current
    val profile = state.profile
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Find any supplier bills with 1-month payment reminder
    val billsWithReminders = state.allInvoices.filter {
        it.invoice.hasPaymentReminder && it.invoice.balanceDue > 0
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Surface(
                        shape = CircleShape,
                        color = Color(0xFF0F172A),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = PureWhite, modifier = Modifier.size(24.dp))
                        }
                    }
                    Text(
                        "Account & Business Profile",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color(0xFF64748B))
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 580.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // 1. Business Profile Card
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
                                horizontalArrangement = Arrangement.spacedBy(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(14.dp),
                                    color = Color(0xFF0A2540),
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            profile?.businessName?.take(2)?.uppercase() ?: "IF",
                                            fontWeight = FontWeight.ExtraBold,
                                            fontSize = 20.sp,
                                            color = PureWhite
                                        )
                                    }
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            profile?.businessName ?: "Apex Stainless Steel & Engineering",
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF0F172A),
                                            maxLines = 1
                                        )
                                        Icon(
                                            Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = TertiaryEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        "GSTIN: ${profile?.gstin ?: "27AAACA9876F1Z4"}",
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                    Text(
                                        "${profile?.phone ?: "+91 98220 98765"} • ${profile?.city ?: "Pune"}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = Color(0xFFF1F5F9))
                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(20.dp),
                                    color = Color(0xFFECFDF5),
                                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFA7F3D0)))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Box(modifier = Modifier.size(6.dp).background(Color(0xFF059669), CircleShape))
                                        Text("Invoice Flex Pro", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                    }
                                }

                                TextButton(
                                    onClick = {
                                        onDismiss()
                                        onNavigateToSettings()
                                    },
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Edit Profile", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = PrimaryCobalt)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryCobalt)
                                }
                            }
                        }
                    }
                }

                // 2. Active 1-Month Supplier Payment Reminders Banner (if any)
                if (billsWithReminders.isNotEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFBEB)),
                            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Color(0xFFFDE68A)))
                        ) {
                            Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = Color(0xFFD97706), modifier = Modifier.size(18.dp))
                                        Text("Active 1-Month Bill Reminders", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFF92400E))
                                    }
                                    Surface(shape = CircleShape, color = Color(0xFFFEF3C7)) {
                                        Text("${billsWithReminders.size} Due", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFB45309))
                                    }
                                }

                                billsWithReminders.take(2).forEach { invItem ->
                                    val inv = invItem.invoice
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(PureWhite, RoundedCornerShape(8.dp))
                                            .padding(10.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(inv.partyName, fontWeight = FontWeight.SemiBold, fontSize = 12.sp, color = Color(0xFF0F172A))
                                            Text("Bill #${inv.invoiceNumber} • Due in 1 Month", fontSize = 10.sp, color = Color(0xFF64748B))
                                        }
                                        Text(
                                            "₹${Formatters.formatCurrency(inv.balanceDue)}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            color = Color(0xFFDC2626)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 3. Quick Account Actions List
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(vertical = 4.dp)) {
                            AccountMenuRow(
                                icon = Icons.Outlined.AccountBalance,
                                title = "Bank & UPI Accounts",
                                subtitle = "SBI A/c • apexsteel@upi",
                                onClick = {
                                    Toast.makeText(context, "Bank: ${profile?.bankName} | UPI: ${profile?.upiId}", Toast.LENGTH_LONG).show()
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF8FAFC), modifier = Modifier.padding(horizontal = 16.dp))

                            AccountMenuRow(
                                icon = Icons.Outlined.Groups,
                                title = "CA & Accountant Access",
                                subtitle = "Share sales & purchase ledgers",
                                onClick = {
                                    onDismiss()
                                    onNavigateToReports()
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF8FAFC), modifier = Modifier.padding(horizontal = 16.dp))

                            AccountMenuRow(
                                icon = Icons.Outlined.Palette,
                                title = "Invoice Themes & Colors",
                                subtitle = "Corporate, Modern, Vibrant styles",
                                onClick = {
                                    onDismiss()
                                    onNavigateToThemeColor()
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF8FAFC), modifier = Modifier.padding(horizontal = 16.dp))

                            AccountMenuRow(
                                icon = Icons.Outlined.SwapHoriz,
                                title = "Switch Business / Multi-Company",
                                subtitle = "Apex Stainless MIDC • Active",
                                onClick = {
                                    Toast.makeText(context, "Multi-Company Hub: Apex Stainless MIDC active", Toast.LENGTH_SHORT).show()
                                }
                            )
                            HorizontalDivider(color = Color(0xFFF8FAFC), modifier = Modifier.padding(horizontal = 16.dp))

                            AccountMenuRow(
                                icon = Icons.Outlined.Settings,
                                title = "Settings & Tax Config",
                                subtitle = "Invoice prefixes, terms & T&C",
                                onClick = {
                                    onDismiss()
                                    onNavigateToSettings()
                                }
                            )
                        }
                    }
                }

                // 4. App Info & Logout / Switch
                item {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                Toast.makeText(context, "Invoice Flex v2.4 • All Systems Synced", Toast.LENGTH_SHORT).show()
                            },
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFF1F5F9)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudDone, contentDescription = null, tint = TertiaryEmerald, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Invoice Flex Cloud Sync Active • Version 2026.4", fontSize = 11.sp, color = Color(0xFF475569), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                item { Spacer(modifier = Modifier.height(20.dp)) }
            }
        }
    }
}

@Composable
fun AccountMenuRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFFF1F5F9),
                modifier = Modifier.size(38.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(icon, contentDescription = null, tint = Color(0xFF334155), modifier = Modifier.size(20.dp))
                }
            }
            Column {
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = Color(0xFF0F172A))
                Text(subtitle, fontSize = 11.sp, color = Color(0xFF64748B))
            }
        }
        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(14.dp))
    }
}
