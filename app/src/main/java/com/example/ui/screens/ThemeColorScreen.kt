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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.InvoiceFlexLogo
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeColorScreen(
    onNavigateBack: () -> Unit = {},
    onThemeSaved: (String, Long) -> Unit = { _, _ -> },
    onNavigateToCustomTheme: () -> Unit = {}
) {
    val context = LocalContext.current

    val regionalThemes = listOf("Uttarakhand", "Jagganth", "Uttar Pradesh", "Maharashtra", "Gujarat", "Standard")
    var selectedRegionalIndex by remember { mutableIntStateOf(0) }

    val formatCategories = listOf(
        "Advance GST \uD83D\uDC51",
        "Luxury NEW",
        "Stylish",
        "Simple",
        "Modern",
        "Billbook (A5)",
        "GST (A5)"
    )
    var selectedCategoryIndex by remember { mutableIntStateOf(0) }

    val colorSwatches = listOf(
        0xFF1E293B to "Charcoal Black",
        0xFF15803D to "Forest Green",
        0xFF0066FF to "Invoice Flex Blue",
        0xFF7E22CE to "Royal Purple",
        0xFFDC2626 to "Crimson Red",
        0xFF4338CA to "Slate Indigo",
        0xFFD97706 to "Mustard Gold",
        0xFFB45309 to "Amber Terracotta",
        0xFF0D9488 to "Teal Cyan"
    )
    var selectedColorIndex by remember { mutableIntStateOf(2) } // default Invoice Flex Blue

    val currentColor = Color(colorSwatches[selectedColorIndex].first)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Theme & Color", fontWeight = FontWeight.Bold, color = Color(0xFF1E293B)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color(0xFF1E293B))
                    }
                },
                actions = {
                    TextButton(onClick = onNavigateToCustomTheme) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = PrimaryCobalt, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Custom Theme", fontWeight = FontWeight.Bold, color = PrimaryCobalt, fontSize = 12.sp)
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = {
                            val themeName = "${regionalThemes[selectedRegionalIndex]} - ${formatCategories[selectedCategoryIndex]}"
                            val colorHex = colorSwatches[selectedColorIndex].first
                            onThemeSaved(themeName, colorHex)
                            Toast.makeText(context, "Theme saved successfully!", Toast.LENGTH_SHORT).show()
                            onNavigateBack()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryCobalt),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PureWhite)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFFF1F5F9))
                .verticalScroll(rememberScrollState())
        ) {
            // Live Paper Preview Container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
                    .background(Color(0xFFE2E8F0), RoundedCornerShape(8.dp))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(4.dp),
                    color = Color.White,
                    shadowElevation = 4.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, currentColor.copy(alpha = 0.6f))
                            .padding(10.dp)
                    ) {
                        // Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Text("TAX INVOICE", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = currentColor)
                                Surface(
                                    color = Color(0xFFF1F5F9),
                                    shape = RoundedCornerShape(2.dp)
                                ) {
                                    Text("ORIGINAL FOR RECIPIENT", fontSize = 7.sp, color = TextSecondary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                InvoiceFlexLogo(size = 18.dp, showText = true)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Business & Invoice Meta Grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color(0xFFE2E8F0))
                        ) {
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(6.dp)
                            ) {
                                Text("Apex Stainless Steel & Engineering", fontWeight = FontWeight.Bold, fontSize = 9.sp, color = currentColor)
                                Text("Mobile: 9822098765 | Bhosari MIDC, Pune", fontSize = 7.sp, color = TextSecondary)
                                Text("GSTIN: 27AAACA9876F1Z4", fontSize = 7.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(
                                modifier = Modifier
                                    .weight(1f)
                                    .border(width = 0.5.dp, color = Color(0xFFE2E8F0))
                                    .padding(6.dp)
                            ) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    Text("Invoice No: SS-INV-2026-042", fontSize = 7.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("Invoice Date: 26/09/2026", fontSize = 7.sp, color = TextSecondary)
                                Text("Due Date: 11/10/2026", fontSize = 7.sp, color = TextSecondary)
                            }
                        }

                        // Bill To Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color(0xFFE2E8F0))
                                .padding(6.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("BILL TO", fontSize = 7.sp, fontWeight = FontWeight.Bold, color = currentColor)
                                Text("RAKESH ENTERPRISES", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                Text("2nd Floor, 12th Main Road, Mysore, Karnataka - 570001", fontSize = 7.sp, color = TextSecondary)
                                Text("GSTIN: 29BDNPXXXXXX | Mobile: 999XXXXXX9", fontSize = 7.sp)
                            }
                        }

                        // Table Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(currentColor)
                                .padding(vertical = 3.dp, horizontal = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("S.NO", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("ITEMS DESCRIPTION", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("HSN", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("QTY", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("RATE", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("DISC", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("TAX (18%)", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                            Text("AMOUNT", fontSize = 6.sp, color = PureWhite, fontWeight = FontWeight.Bold)
                        }

                        // Sample Item Rows
                        val items = listOf(
                            Triple("S.S. 304 Staff Locker 12-Door", "9403", "2 PCS | Rate: 24,500 | ₹49,000"),
                            Triple("S.S. Dual Door Storage Cabinet", "9403", "1 PCS | Rate: 32,000 | ₹32,000"),
                            Triple("Laser Cutting & CNC Bending Work", "9988", "10 HRS | Rate: 1,500 | ₹15,000")
                        )

                        items.forEachIndexed { idx, itm ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .border(0.5.dp, Color(0xFFF1F5F9))
                                    .padding(vertical = 3.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("${idx + 1}", fontSize = 6.sp)
                                Text(itm.first, fontSize = 6.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f).padding(horizontal = 4.dp))
                                Text(itm.second, fontSize = 6.sp)
                                Text(itm.third, fontSize = 6.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Tax & Totals Breakdown Table
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(0.5.dp, Color(0xFFCBD5E1))
                                .padding(4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Total Taxable Value: ₹96,000.00", fontSize = 7.sp)
                                Text("CGST @ 9%: ₹8,640.00 | SGST @ 9%: ₹8,640.00", fontSize = 7.sp, color = TextSecondary)
                                Text("Total Tax Amount: ₹17,280.00", fontSize = 7.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Grand Total (in words): One Lakh Thirteen Thousand...", fontSize = 6.sp, color = TextSecondary)
                                Text("₹1,13,280.00", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = currentColor)
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Footer Bank & Stamp
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Column {
                                Text("Bank: State Bank of India | A/c: 40291827364", fontSize = 6.sp)
                                Text("IFSC: SBIN0004521 | UPI: apexsteel@upi", fontSize = 6.sp, color = TextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("For Apex Stainless Steel", fontSize = 6.sp, fontWeight = FontWeight.Bold)
                                Text("Authorized Signatory", fontSize = 6.sp, color = TextSecondary)
                            }
                        }
                    }
                }
            }

            // Theme Styling Regional Selector
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(PureWhite)
                    .padding(vertical = 12.dp)
            ) {
                Text(
                    text = "Theme Styling",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1E293B),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedRegionalIndex,
                    edgePadding = 16.dp,
                    containerColor = PureWhite,
                    indicator = {},
                    divider = {}
                ) {
                    regionalThemes.forEachIndexed { index, title ->
                        val isSelected = selectedRegionalIndex == index
                        Surface(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { selectedRegionalIndex = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) currentColor.copy(alpha = 0.12f) else Color(0xFFF1F5F9),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, currentColor) else null
                        ) {
                            Text(
                                text = title,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) currentColor else Color(0xFF475569)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Format Categories (Advance GST, Luxury, Stylish, Simple...)
                ScrollableTabRow(
                    selectedTabIndex = selectedCategoryIndex,
                    edgePadding = 16.dp,
                    containerColor = PureWhite,
                    indicator = {},
                    divider = {}
                ) {
                    formatCategories.forEachIndexed { index, cat ->
                        val isSelected = selectedCategoryIndex == index
                        Surface(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .clickable { selectedCategoryIndex = index },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) currentColor.copy(alpha = 0.15f) else Color(0xFFF8FAFC),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) currentColor else Color(0xFFE2E8F0)
                            )
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) currentColor else Color(0xFF334155)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Color Swatches
                Text(
                    text = "Select Accent Color",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    colorSwatches.forEachIndexed { idx, (cHex, name) ->
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
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = name,
                                    tint = PureWhite,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
