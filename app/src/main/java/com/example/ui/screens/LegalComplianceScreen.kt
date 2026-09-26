package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CanvasBg
import com.example.ui.theme.OnSurfaceObsidian
import com.example.ui.theme.PrimaryCobalt
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextSecondary

enum class LegalTab(val title: String, val icon: ImageVector) {
    PRIVACY("Privacy Policy", Icons.Default.Security),
    TERMS("Terms of Service", Icons.Default.Gavel),
    COMPLIANCE("GST & Data", Icons.Default.Policy),
    ABOUT("About App", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalComplianceScreen(
    initialTab: LegalTab = LegalTab.PRIVACY,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(initialTab) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Legal & Compliance",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = OnSurfaceObsidian
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = OnSurfaceObsidian
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(CanvasBg)
        ) {
            PrimaryTabRow(
                selectedTabIndex = selectedTab.ordinal,
                containerColor = PureWhite,
                contentColor = PrimaryCobalt
            ) {
                LegalTab.values().forEach { tab ->
                    Tab(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    LegalTab.PRIVACY -> {
                        item {
                            LegalSectionCard(
                                title = "Privacy Policy",
                                lastUpdated = "September 2026",
                                content = """
                                1. Information We Collect:
                                Invoice Flex processes business invoice data, customer party records, and catalog inventory details. Your data is protected with enterprise-level security.
                                
                                2. Storage & Cloud Synchronization:
                                Data is persisted locally on your device with optional encrypted Firebase cloud synchronization for authenticated business owners.
                                
                                3. Telemetry & Analytics:
                                We do not sell your personal or financial transaction data. Anonymous diagnostic logs are only used to improve app stability and performance.
                                
                                4. Your Rights:
                                You can request full data export or account data deletion anytime through the Settings > Account menu.
                                """.trimIndent()
                            )
                        }
                    }

                    LegalTab.TERMS -> {
                        item {
                            LegalSectionCard(
                                title = "Terms of Service",
                                lastUpdated = "September 2026",
                                content = """
                                1. Acceptance of Terms:
                                By using Invoice Flex, you agree to comply with applicable commercial billing, GST accounting, and tax compliance regulations.
                                
                                2. Business Responsibilities:
                                You are solely responsible for ensuring that GSTIN numbers, HSN/SAC codes, tax rates, E-Way bills, and IRN details accurately represent your business transactions.
                                
                                3. Service Availability:
                                Offline capabilities allow continuous invoice generation even without network connectivity. Cloud sync resumes when online.
                                
                                4. Intellectual Property:
                                All trademarks, invoice templates, and system designs remain the property of Invoice Flex.
                                """.trimIndent()
                            )
                        }
                    }

                    LegalTab.COMPLIANCE -> {
                        item {
                            LegalSectionCard(
                                title = "GST & Regulatory Compliance",
                                lastUpdated = "September 2026",
                                content = """
                                1. GST Invoice Rules:
                                Invoice Flex complies with standard GST invoice formatting rules, including mandatory GSTIN display, sequential numbering, state codes, and HSN breakdowns.
                                
                                2. E-Way Bill & IRN Integration:
                                QR codes and IRN tokens generated adhere to standard GST verification specifications.
                                
                                3. Audit Trail & Record Retention:
                                Maintain your generated PDF vouchers and transaction logs according to the statutory retention duration required by your tax jurisdiction.
                                """.trimIndent()
                            )
                        }
                    }

                    LegalTab.ABOUT -> {
                        item {
                            LegalSectionCard(
                                title = "About Invoice Flex",
                                lastUpdated = "Version 1.0.0",
                                content = """
                                Invoice Flex is an all-in-one Smart GST & Non-GST Billing and Accounting suite designed for modern manufacturers, wholesalers, and retail businesses.
                                
                                Key Capabilities:
                                • GST & Non-GST Tax Invoices, Quotations, Proforma, Credit Notes
                                • Live HSN/SAC Code Search and Tax Calculators
                                • Multi-Theme Professional Invoicing with Custom Color Palettes
                                • Instant PDF Generation & WhatsApp Sharing
                                • Dynamic Feed, Party Balances, and Financial Statements
                                
                                Developer Contact: support@invoiceflex.app
                                """.trimIndent()
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun LegalSectionCard(
    title: String,
    lastUpdated: String,
    content: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = OnSurfaceObsidian
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Last updated: $lastUpdated",
                fontSize = 12.sp,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = content,
                fontSize = 14.sp,
                lineHeight = 22.sp,
                color = Color(0xFF334155)
            )
        }
    }
}
