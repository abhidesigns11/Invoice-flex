package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.ViewColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
fun InvoiceSettingsScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToThemeColor: () -> Unit = {},
    onNavigateToCustomThemeBuilder: () -> Unit = {},
    onNavigateToEWayIrn: () -> Unit = {}
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice Settings", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF8FAFC))
        ) {
            // "Create your own Invoice Theme" Green Banner
            item {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToCustomThemeBuilder() },
                    color = Color(0xFFE8F5E9)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Create your own Invoice Theme",
                            color = Color(0xFF2E7D32),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF2E7D32)
                        )
                    }
                }
            }

            // Mini Invoice Paper Preview snippet
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .height(130.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = PureWhite),
                        elevation = CardDefaults.cardElevation(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    InvoiceFlexLogo(size = 18.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Invoice Flex Live Theme", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                                }
                                Text("ORIGINAL FOR RECIPIENT", fontSize = 8.sp, color = TextSecondary)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            HorizontalDivider(color = Color(0xFFE2E8F0))
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(PrimaryCobalt.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("ITEMS: SS Lockers, Cabinets & Tables", fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                Text("TOTAL: ₹1,13,280", fontSize = 9.sp, fontWeight = FontWeight.ExtraBold, color = PrimaryCobalt)
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text("Tap 'Theme & Color' below to customize fonts, tables and swatches", fontSize = 8.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // Settings Items (matching screenshots 4 & 5)
            item {
                InvoiceSettingRow(
                    icon = Icons.Default.LocalShipping,
                    title = "Generate e-Way Bills & e-Invoices",
                    subtitle = "Directly generate GST invoice documents and change added GSP credentials",
                    onClick = onNavigateToEWayIrn
                )

                InvoiceSettingRow(
                    icon = Icons.Default.ColorLens,
                    title = "Theme & Color",
                    subtitle = "Choose starting looks, regional styling, and palette accents",
                    onClick = onNavigateToThemeColor
                )

                InvoiceSettingRow(
                    icon = Icons.Default.FormatListNumbered,
                    title = "Invoice Number Prefix",
                    subtitle = "Current: ${state.profile?.invoicePrefix ?: "SS-INV-2026-"}",
                    onClick = {
                        Toast.makeText(context, "Prefix format SS-INV-2026- active", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Phone,
                    title = "Phone Number on Invoice",
                    subtitle = state.profile?.phone ?: "+91 98220 98765",
                    onClick = {
                        Toast.makeText(context, "Phone number configured in Business Profile", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Email,
                    title = "Email on Invoice",
                    subtitle = state.profile?.email ?: "fabrication@apexsteel.in",
                    onClick = {
                        Toast.makeText(context, "Email configured in Business Profile", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Description,
                    title = "Terms and Conditions",
                    subtitle = "Edit default payment, warranty, and return policies",
                    onClick = {
                        Toast.makeText(context, "Standard 15-day terms active", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Draw,
                    title = "Signature",
                    subtitle = "Toggle digital signature and authorized stamp display",
                    onClick = {
                        Toast.makeText(context, "Digital signature enabled on invoices", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.AccountBalance,
                    title = "Bank Account & Payment QR",
                    subtitle = "Choose the bank account and Payment QR, displayed on all new invoices",
                    onClick = {
                        Toast.makeText(context, "SBI Account & Dynamic UPI QR active", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Discount,
                    title = "Discount Type",
                    trailingText = "Discount After Tax",
                    onClick = {
                        Toast.makeText(context, "Toggled Discount calculation mode", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.TableChart,
                    title = "Add Fields to Invoice",
                    subtitle = "Add Additional Fields on top of your Invoice. Ex: PO Number, Vehicle Number",
                    onClick = {
                        Toast.makeText(context, "PO Number and Vehicle fields enabled", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.ViewColumn,
                    title = "Add/Remove Columns",
                    subtitle = "Add new columns ex. Batch no, Expiry date or remove existing columns ex. Qty, Rate",
                    onClick = {
                        Toast.makeText(context, "Columns: S.No, Item, HSN, Qty, Rate, Disc, Tax, Amount", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Receipt,
                    title = "Add Fields to Party",
                    subtitle = "Add extra fields in party section of invoices. Eg: Drug Licence No. and other Custom Fields",
                    onClick = {
                        Toast.makeText(context, "Custom Party DL & Site Code active", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.MoreHoriz,
                    title = "Additional Settings",
                    subtitle = "Party balance, Item Description, Free Quantity, Billing in other currency",
                    onClick = {
                        Toast.makeText(context, "Item description & Party balance shown", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Print,
                    title = "Printer Settings",
                    subtitle = "Thermal 2-inch, 3-inch, A4 Regular, A5 Landscape",
                    onClick = {
                        Toast.makeText(context, "Default format set to Regular A4 Laser/Inkjet", Toast.LENGTH_SHORT).show()
                    }
                )

                InvoiceSettingRow(
                    icon = Icons.Default.Share,
                    title = "Sharing Settings",
                    subtitle = "WhatsApp PDF templates, SMS alerts and automated billing copy",
                    onClick = {
                        Toast.makeText(context, "WhatsApp 1-Tap Sharing Active", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

@Composable
private fun InvoiceSettingRow(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    trailingText: String? = null,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        color = PureWhite
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(Color(0xFFEEF2F6), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = null,
                        tint = PrimaryCobalt,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF1E293B)
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }

                if (trailingText != null) {
                    Text(
                        text = trailingText,
                        fontSize = 12.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(end = 4.dp)
                    )
                }

                Icon(
                    Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF94A3B8),
                    modifier = Modifier.size(20.dp)
                )
            }
            HorizontalDivider(color = Color(0xFFF1F5F9), thickness = 1.dp)
        }
    }
}
