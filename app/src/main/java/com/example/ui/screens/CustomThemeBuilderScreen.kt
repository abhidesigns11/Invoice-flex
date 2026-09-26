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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InvoiceFlexLogo
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.BillingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomThemeBuilderScreen(
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current

    var themeTitle by remember { mutableStateOf("My Custom Steel Theme") }
    var selectedBorderType by remember { mutableStateOf("Solid Tally Grid") } // Solid Tally Grid, Minimal Outline, Modern Double
    var showWatermark by remember { mutableStateOf(true) }
    var showUpiQrOnTop by remember { mutableStateOf(false) }
    var showHsnColumn by remember { mutableStateOf(true) }
    var showDiscountColumn by remember { mutableStateOf(true) }
    var showTaxBreakdown by remember { mutableStateOf(true) }
    var showAuthorizedStamp by remember { mutableStateOf(true) }

    val colors = listOf(
        0xFF0066FF to "Invoice Flex Blue",
        0xFF0A2540 to "Deep Navy",
        0xFF15803D to "Emerald Green",
        0xFF7E22CE to "Royal Purple",
        0xFFDC2626 to "Crimson Red",
        0xFFD97706 to "Amber Gold"
    )
    var selectedColorIndex by remember { mutableIntStateOf(0) }
    val activeColor = Color(colors[selectedColorIndex].first)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Invoice Theme Studio", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
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
                        .padding(16.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.showMessage("Custom theme '$themeTitle' saved and set as default!")
                            Toast.makeText(context, "Theme '$themeTitle' applied successfully!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save & Apply Theme", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PureWhite)
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
                .verticalScroll(rememberScrollState())
        ) {
            // Live Preview Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White,
                    shadowElevation = 3.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = if (selectedBorderType == "Modern Double") 2.dp else 1.dp,
                                color = activeColor
                            )
                            .padding(10.dp)
                    ) {
                        // Header Bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(activeColor.copy(alpha = 0.08f), RoundedCornerShape(4.dp))
                                .padding(6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                InvoiceFlexLogo(size = 20.dp, showText = true)
                            }
                            Text("TAX INVOICE", fontSize = 10.sp, fontWeight = FontWeight.ExtraBold, color = activeColor)
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Table preview
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(activeColor)
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("ITEM", fontSize = 7.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            if (showHsnColumn) Text("HSN", fontSize = 7.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("QTY", fontSize = 7.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("RATE", fontSize = 7.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            if (showDiscountColumn) Text("DISC", fontSize = 7.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("AMOUNT", fontSize = 7.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color(0xFFE2E8F0))
                                .padding(horizontal = 6.dp, vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("S.S. Office Workstation", fontSize = 7.sp, fontWeight = FontWeight.SemiBold)
                            if (showHsnColumn) Text("9403", fontSize = 7.sp)
                            Text("2 Pcs", fontSize = 7.sp)
                            Text("₹18,000", fontSize = 7.sp)
                            if (showDiscountColumn) Text("₹0", fontSize = 7.sp)
                            Text("₹36,000", fontSize = 7.sp, fontWeight = FontWeight.Bold)
                        }

                        if (showTaxBreakdown) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(Color(0xFFF8FAFC))
                                    .padding(4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("CGST (9%) + SGST (9%): ₹6,480", fontSize = 7.sp, color = TextSecondary)
                                Text("Total: ₹42,480.00", fontSize = 8.sp, fontWeight = FontWeight.ExtraBold, color = activeColor)
                            }
                        }

                        if (showAuthorizedStamp) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Surface(
                                    color = Color(0xFFF3E8FF),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFC084FC))
                                ) {
                                    Text("AUTHORIZED SIGN & STAMP", fontSize = 6.sp, color = Color(0xFF7E22CE), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                    }
                }
            }

            // Controls Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = PureWhite),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Theme Name", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = themeTitle,
                        onValueChange = { themeTitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Border Style", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Solid Tally Grid", "Minimal Outline", "Modern Double").forEach { b ->
                            FilterChip(
                                selected = selectedBorderType == b,
                                onClick = { selectedBorderType = b },
                                label = { Text(b, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PrimaryCobalt,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Accent Color Palette", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        colors.forEachIndexed { idx, (cHex, name) ->
                            val col = Color(cHex)
                            val isSelected = selectedColorIndex == idx
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(col, CircleShape)
                                    .border(
                                        width = if (isSelected) 3.dp else 1.dp,
                                        color = if (isSelected) Color(0xFF0F172A) else Color.Transparent,
                                        shape = CircleShape
                                    )
                                    .clickable { selectedColorIndex = idx },
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = name, tint = PureWhite, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFF1F5F9))

                    // Toggles
                    ThemeOptionToggle(title = "Display HSN/SAC Column", checked = showHsnColumn, onToggle = { showHsnColumn = it })
                    ThemeOptionToggle(title = "Display Discount Column", checked = showDiscountColumn, onToggle = { showDiscountColumn = it })
                    ThemeOptionToggle(title = "Itemized Tax Breakdown Table", checked = showTaxBreakdown, onToggle = { showTaxBreakdown = it })
                    ThemeOptionToggle(title = "Show Authorized Seal & Signature", checked = showAuthorizedStamp, onToggle = { showAuthorizedStamp = it })
                    ThemeOptionToggle(title = "Invoice Flex Verified Security Watermark", checked = showWatermark, onToggle = { showWatermark = it })
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThemeOptionToggle(
    title: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, fontSize = 13.sp, color = Color(0xFF334155))
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = PureWhite, checkedTrackColor = PrimaryCobalt)
        )
    }
}
