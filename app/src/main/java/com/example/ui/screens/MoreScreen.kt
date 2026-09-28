package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.StarOutline
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InvoiceFlexLogo
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateToInvoiceSettings: () -> Unit = {},
    onNavigateToAccountSettings: () -> Unit = {},
    onNavigateToHsnFinder: () -> Unit = {},
    onNavigateToSetupWizard: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val context = LocalContext.current
    val businessName = state.profile?.businessName ?: "Apex Stainless Steel"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InvoiceFlexLogo(size = 28.dp, showText = true, subtitle = "Account & Settings")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    Toast.makeText(context, "Invoice Flex VIP Priority Support: Connecting to agent...", Toast.LENGTH_SHORT).show()
                },
                containerColor = Color(0xFF1E293B),
                contentColor = PureWhite,
                shape = CircleShape
            ) {
                Row(modifier = Modifier.padding(horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Headphones, contentDescription = "Help")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Help", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            // Header Profile Strip (Matching reference Screenshot 6)
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PureWhite
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = businessName,
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1E293B)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFF1F5F9),
                                    modifier = Modifier.clickable { onNavigateToAccountSettings() }
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "BUSINESS & GST SETTINGS",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF475569)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                            contentDescription = null,
                                            modifier = Modifier.size(14.dp),
                                            tint = Color(0xFF475569)
                                        )
                                    }
                                }
                            }

                            // Avatar Circle
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .background(Color(0xFF7DD3FC), RoundedCornerShape(14.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = businessName.take(1).uppercase(),
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF0369A1)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // User Setup Q&A Bar
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onNavigateToSetupWizard() },
                            shape = RoundedCornerShape(10.dp),
                            color = PrimaryCobalt.copy(alpha = 0.08f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Workspace: Abhishek Panchal (Owner) • Edit Setup Q&A",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = PrimaryCobalt
                                )
                                Text("Change >", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Top Quick Rows
            item {
                Surface(color = PureWhite) {
                    Column {
                        MoreNavRow(
                            icon = Icons.Default.WorkspacePremium,
                            iconTint = Color(0xFFD97706),
                            title = "Invoice Flex Subscription Plan",
                            onClick = { Toast.makeText(context, "You are on Invoice Flex Enterprise Platinum", Toast.LENGTH_SHORT).show() }
                        )
                        MoreNavRow(
                            icon = Icons.Default.HelpOutline,
                            iconTint = Color(0xFF3B82F6),
                            title = "Help & Tutorials",
                            onClick = { Toast.makeText(context, "Guides for S.S. billing & e-Way Bill", Toast.LENGTH_SHORT).show() }
                        )
                        MoreNavRow(
                            icon = Icons.Default.CardGiftcard,
                            iconTint = Color(0xFF8B5CF6),
                            title = "Invite & Earn",
                            onClick = {
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_TEXT, "Use Invoice Flex for smart GST billing, e-Way bills & fabrication catalog! Download now.")
                                }
                                context.startActivity(Intent.createChooser(intent, "Invite Friends"))
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Section "Settings"
            item {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Surface(color = PureWhite) {
                    Column {
                        MoreNavRow(
                            icon = Icons.Default.ReceiptLong,
                            iconTint = Color(0xFF6366F1),
                            title = "Invoice Settings",
                            onClick = onNavigateToInvoiceSettings
                        )
                        MoreNavRow(
                            icon = Icons.Default.PersonOutline,
                            iconTint = Color(0xFF0284C7),
                            title = "Account Settings (Personal & Business Q&A)",
                            onClick = onNavigateToSetupWizard
                        )
                        MoreNavRow(
                            icon = Icons.Default.NotificationsNone,
                            iconTint = Color(0xFF10B981),
                            title = "Reminder Settings",
                            onClick = { Toast.makeText(context, "Payment reminders set for 3 days before due date", Toast.LENGTH_SHORT).show() }
                        )
                        MoreNavRow(
                            icon = Icons.Default.Delete,
                            iconTint = Color(0xFFEF4444),
                            title = "Recover Deleted Invoices",
                            onClick = { Toast.makeText(context, "No deleted invoices found in recycle bin", Toast.LENGTH_SHORT).show() }
                        )
                        MoreNavRow(
                            icon = Icons.AutoMirrored.Filled.Logout,
                            iconTint = Color(0xFFEF4444),
                            title = "Log Out",
                            onClick = onLogout
                        )
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Section "Others"
            item {
                Text(
                    text = "Others",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                )

                Surface(color = PureWhite) {
                    Column {
                        MoreNavRow(
                            icon = Icons.Default.Search,
                            iconTint = Color(0xFF0284C7),
                            title = "GST Rate & HSN Finder",
                            onClick = onNavigateToHsnFinder
                        )
                        MoreNavRow(
                            icon = Icons.Default.Print,
                            iconTint = Color(0xFF475569),
                            title = "Buy Thermal & Barcode Printer",
                            onClick = { Toast.makeText(context, "Connecting to verified hardware vendor", Toast.LENGTH_SHORT).show() }
                        )
                        MoreNavRow(
                            icon = Icons.Default.StarOutline,
                            iconTint = Color(0xFFF59E0B),
                            title = "Rate app on Play Store",
                            onClick = { Toast.makeText(context, "Thank you for rating Invoice Flex 5 Stars!", Toast.LENGTH_SHORT).show() }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(60.dp))
            }
        }
    }
}

@Composable
private fun MoreNavRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF1E293B),
            modifier = Modifier.weight(1f)
        )
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
        )
    }
    HorizontalDivider(color = Color(0xFFF8FAFC), thickness = 1.dp)
}
