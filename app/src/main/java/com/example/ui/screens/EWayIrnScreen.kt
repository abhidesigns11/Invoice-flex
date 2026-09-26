package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EWayIrnScreen(
    onNavigateBack: () -> Unit = {},
    onNavigateToInvoiceDetail: (Long) -> Unit = {}
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedTransportMode by remember { mutableStateOf("Road") }
    var showCopiedToast by remember { mutableStateOf(false) }
    var activeVehicleNo by remember { mutableStateOf("MH-14-GH-8821") }

    val irnHash = "4f8b9e217d84a0c8b32e18591efb3c90712cae01a89c37e61bfd30291e84714c"
    val ewayBillNo = "2410-8893-1029"

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "e-Way & IRN Transit",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = OnSurfaceObsidian
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = SurfaceContainerHigh
                        ) {
                            Text(
                                "NIC DIRECT",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                                fontWeight = FontWeight.Bold,
                                color = PrimaryCobalt
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = OnSurfaceObsidian)
                    }
                },
                actions = {
                    IconButton(onClick = {
                        Toast.makeText(context, "NIC Gateway status: 100% Operational (38ms)", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Outlined.CloudDone, contentDescription = "NIC Gateway", tint = TertiaryEmerald)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PureWhite
                )
            )
        },
        containerColor = CanvasBg
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp)
        ) {
            // 1. NIC Gateway Live Telemetry Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceContainerLow),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineHairline))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .background(TertiaryEmerald, CircleShape)
                                )
                                Text(
                                    "NIC GST GATEWAY V1.04",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        letterSpacing = 0.5.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = OnSurfaceObsidian
                                )
                                Text("•", color = TextSecondary)
                                Text(
                                    "38ms",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = TextSecondary
                                )
                            }
                            Surface(
                                shape = CircleShape,
                                color = PureWhite,
                                shadowElevation = 1.dp
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(modifier = Modifier.size(6.dp).background(TertiaryEmerald, CircleShape))
                                    Text("SYNC OK", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TertiaryEmerald, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        HorizontalDivider(color = OutlineHairline.copy(alpha = 0.5f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text("Apex Works (MH-27)", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = OnSurfaceObsidian)
                                Text("27AAACA9876F1Z4", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = SecondaryContainer.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Speed, contentDescription = null, modifier = Modifier.size(12.dp), tint = OnSecondaryContainer)
                                    Text("24/50 API Quota", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = OnSecondaryContainer, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }

            // 2. Dual Segmented Tab
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = CircleShape,
                    color = SurfaceContainerLow
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 0 },
                            shape = CircleShape,
                            color = if (selectedTab == 0) PureWhite else Color.Transparent,
                            shadowElevation = if (selectedTab == 0) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Inventory2,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = if (selectedTab == 0) PrimaryCobalt else TextSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Active Invoices",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedTab == 0) PrimaryCobalt else TextSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = PrimaryContainer
                                ) {
                                    Text(
                                        "3",
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp),
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                        fontWeight = FontWeight.Bold,
                                        color = OnPrimary
                                    )
                                }
                            }
                        }

                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = 1 },
                            shape = CircleShape,
                            color = if (selectedTab == 1) PureWhite else Color.Transparent,
                            shadowElevation = if (selectedTab == 1) 2.dp else 0.dp
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.EditNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(15.dp),
                                    tint = if (selectedTab == 1) PrimaryCobalt else TextSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    "Manual Entry",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (selectedTab == 1) PrimaryCobalt else TextSecondary
                                )
                            }
                        }
                    }
                }
            }

            // 3. Active Document Hero Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineHairline)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text("#BN-2025-043", fontFamily = FontFamily.Monospace, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                    Surface(shape = RoundedCornerShape(4.dp), color = PrimaryFixed) {
                                        Text("B2B TAX", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = OnPrimaryFixed)
                                    }
                                }
                                Text("L&T Heavy Civil Infrastructure", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                Text("Hinjawadi Phase-2, Pune, MH", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("₹1,75,230", fontFamily = FontFamily.Monospace, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = PrimaryContainer)
                                Text("Tax: ₹26,730", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                            }
                        }

                        // Micro Tags
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(shape = CircleShape, color = SurfaceContainer) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.FormatAlignLeft, contentDescription = null, modifier = Modifier.size(11.dp), tint = PrimaryCobalt)
                                    Text("Mandatory e-Way (>₹50k)", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                                }
                            }
                            Surface(shape = CircleShape, color = TertiaryFixed) {
                                Row(modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(11.dp), tint = OnTertiaryFixedVariant)
                                    Text("B2B IRP Eligible", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = OnTertiaryFixedVariant)
                                }
                            }
                        }

                        // Material Line Preview
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = CanvasBg
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .background(SecondaryContainer, RoundedCornerShape(6.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.ViewInAr, contentDescription = null, modifier = Modifier.size(15.dp), tint = OnSecondaryContainer)
                                    }
                                    Column {
                                        Text("SS 316 Balcony Railing 50mm Pipes", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = OnSurfaceObsidian)
                                        Text("HSN: 73064000 • Gr: 316L Marine", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                                    }
                                }
                                Text("420.00 Kgs", fontFamily = FontFamily.Monospace, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                            }
                        }
                    }
                }
            }

            // 4. Part A • Invoice Registration (IRP Signed)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineHairline))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(TertiaryEmerald, CircleShape))
                                Text("PART A • INVOICE REGISTRATION", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                            }
                            Surface(shape = CircleShape, color = TertiaryFixed) {
                                Text("AUTHENTICATED (NIC PUSH)", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = OnTertiaryFixedVariant)
                            }
                        }

                        // Tri-pill validation matrix
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = CanvasBg) {
                                Row(modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(13.dp), tint = TertiaryEmerald)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Digest 256 ✓", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceObsidian)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = CanvasBg) {
                                Row(modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.CloudDone, contentDescription = null, modifier = Modifier.size(13.dp), tint = TertiaryEmerald)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("NIC 200 OK ✓", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceObsidian)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = CanvasBg) {
                                Row(modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(13.dp), tint = TertiaryEmerald)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Signed QR ✓", fontSize = 9.sp, fontWeight = FontWeight.SemiBold, color = OnSurfaceObsidian)
                                }
                            }
                        }

                        // SHA-256 Hash Display
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("64-CHAR SHA-256 IRN HASH", style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp), color = TextSecondary, fontWeight = FontWeight.Bold)
                                Row(
                                    modifier = Modifier.clickable {
                                        clipboardManager.setText(AnnotatedString(irnHash))
                                        Toast.makeText(context, "IRN Hash copied to clipboard!", Toast.LENGTH_SHORT).show()
                                    },
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(13.dp), tint = PrimaryCobalt)
                                    Text("Copy", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                                }
                            }
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(6.dp),
                                color = CanvasBg
                            ) {
                                Text(
                                    irnHash,
                                    modifier = Modifier.padding(8.dp),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    color = OnSurfaceObsidian,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Ack No: 112450982312", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = OnSurfaceObsidian, fontWeight = FontWeight.SemiBold)
                            Text("Ack Date: Today, 14:28 IST", fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                }
            }

            // 5. Part B • Transport & Logistics
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineHairline))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Box(modifier = Modifier.size(8.dp).background(PrimaryCobalt, CircleShape))
                                Text("PART B • TRANSPORT & LOGISTICS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                            }
                            Surface(shape = CircleShape, color = PrimaryFixed) {
                                Text("PART-B ACTIVE", modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = OnPrimaryFixed)
                            }
                        }

                        // Multi-modal Switcher
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceContainerLow
                        ) {
                            Row(
                                modifier = Modifier.padding(2.dp),
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                listOf("Road" to Icons.Default.LocalShipping, "Rail" to Icons.Default.Train, "Air" to Icons.Default.Flight, "Ship" to Icons.Default.DirectionsBoat).forEach { (mode, icon) ->
                                    val isSelected = selectedTransportMode == mode
                                    Surface(
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable { selectedTransportMode = mode },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) PureWhite else Color.Transparent,
                                        shadowElevation = if (isSelected) 1.dp else 0.dp
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(vertical = 6.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(icon, contentDescription = null, modifier = Modifier.size(13.dp), tint = if (isSelected) PrimaryCobalt else TextSecondary)
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(mode, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) PrimaryCobalt else TextSecondary)
                                        }
                                    }
                                }
                            }
                        }

                        // Route Distance Visualization
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = CanvasBg
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ORIGIN", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                    Text("Apex Works, Bhosari", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                    Text("PIN: 411026", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Box(modifier = Modifier.width(20.dp).height(1.dp).background(PrimaryCobalt))
                                        Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(14.dp), tint = PrimaryCobalt)
                                        Box(modifier = Modifier.width(20.dp).height(1.dp).background(PrimaryCobalt))
                                    }
                                    Text("28.4 km (Locked)", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = PrimaryCobalt)
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text("DESTINATION", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                    Text("L&T Site, Hinjawadi", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                    Text("PIN: 411057", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }

                        // Vehicle & Transporter
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), color = CanvasBg) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("ASSIGNED VEHICLE", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Text(activeVehicleNo, fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                        Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(13.dp), tint = TertiaryEmerald)
                                    }
                                    Text("Tata 407 LPT • VAHAN Match", fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), color = CanvasBg) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("TRANSPORTER & BILTY", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                    Text("Shree Balaji Logistics", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.SemiBold, color = OnSurfaceObsidian, maxLines = 1)
                                    Text("LR: BL-99214 • 27AAACB1982K", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }
            }

            // 6. Official Government Transit Pass (EWB-01 Preview)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineHairline))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp), tint = PrimaryCobalt)
                                Text("OFFICIAL EWB-01 TRANSIT PASS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                            }
                            Surface(shape = RoundedCornerShape(4.dp), color = SurfaceContainer) {
                                Text("FASTTAG ACTIVE", modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                            }
                        }

                        // QR + e-Way Number
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = CanvasBg
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(60.dp)
                                        .background(PureWhite, RoundedCornerShape(6.dp))
                                        .border(1.dp, OutlineHairline, RoundedCornerShape(6.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.QrCode2, contentDescription = "E-Way QR", modifier = Modifier.size(48.dp), tint = OnSurfaceObsidian)
                                }
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("E-WAY BILL NUMBER", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(ewayBillNo, fontFamily = FontFamily.Monospace, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                        IconButton(
                                            onClick = {
                                                clipboardManager.setText(AnnotatedString(ewayBillNo))
                                                Toast.makeText(context, "e-Way Bill # copied!", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(24.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", modifier = Modifier.size(14.dp), tint = PrimaryCobalt)
                                        }
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                        Icon(Icons.Default.Timer, contentDescription = null, modifier = Modifier.size(12.dp), tint = TertiaryEmerald)
                                        Text("Valid till tomorrow 23:59 IST (1 Day)", fontFamily = FontFamily.Monospace, fontSize = 10.sp, color = TertiaryEmerald, fontWeight = FontWeight.Medium)
                                    }
                                }
                            }
                        }

                        // Weight & Telemetry Breakdown
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = CanvasBg) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("NET METALLURGICAL WT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                    Text("420.00 Kgs", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                }
                            }
                            Surface(modifier = Modifier.weight(1f), shape = RoundedCornerShape(6.dp), color = CanvasBg) {
                                Column(modifier = Modifier.padding(8.dp)) {
                                    Text("GROSS FREIGHT WT", style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp), color = TextSecondary)
                                    Text("445.00 Kgs (Tare: 25Kg)", fontFamily = FontFamily.Monospace, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = OnSurfaceObsidian)
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(Icons.Default.Toll, contentDescription = null, modifier = Modifier.size(12.dp), tint = TertiaryEmerald)
                                Text("Electronic Toll: Wakad Bypass Synced", fontSize = 10.sp, color = TextSecondary)
                            }
                            Text("Toll PASS OK", fontFamily = FontFamily.Monospace, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TertiaryEmerald)
                        }
                    }
                }
            }

            // 7. Bottom Crystal Action Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            Toast.makeText(context, "Downloading e-Way Pass & IRN Digitally Signed PDF...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PureWhite,
                            contentColor = OnSurfaceObsidian
                        ),
                        border = ButtonDefaults.outlinedButtonBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(OutlineHairline)),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp)
                    ) {
                        Icon(Icons.Default.SimCardDownload, contentDescription = null, modifier = Modifier.size(17.dp), tint = TextSecondary)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Download PDF", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = {
                            Toast.makeText(context, "Transmitting e-Way Pass & IRN to Transporter on WhatsApp...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = OnPrimary
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(17.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Share e-Way + IRN", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}
