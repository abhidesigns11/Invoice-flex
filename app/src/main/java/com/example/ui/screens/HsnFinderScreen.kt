package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.HsnDatabase
import com.example.data.db.HsnRecord
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HsnFinderScreen(
    onNavigateBack: () -> Unit,
    onSelectHsnForNewItem: ((HsnRecord) -> Unit)? = null
) {
    val context = LocalContext.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryFilter by remember { mutableStateOf("All") }
    var selectedTab by remember { mutableIntStateOf(1) } // 0: GST Calculator, 1: Rate Finder

    val searchResults = remember(searchQuery, selectedCategoryFilter) {
        val baseList = if (selectedCategoryFilter == "All") {
            HsnDatabase.hsnList
        } else {
            HsnDatabase.hsnList.filter { it.keywords.contains(selectedCategoryFilter, ignoreCase = true) || it.chapter.contains(selectedCategoryFilter, ignoreCase = true) }
        }

        if (searchQuery.isBlank()) {
            baseList
        } else {
            HsnDatabase.search(searchQuery)
        }
    }

    fun copyHsnToClipboard(code: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("HSN Code", code)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, "HSN Code $code copied to clipboard", Toast.LENGTH_SHORT).show()
    }

    Scaffold(
        topBar = {
            Column {
                // Disclaimer Banner
                Surface(
                    color = Color(0xFF1E3A8A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Stainless Steel & Fabrication GST HSN/SAC Master Directory (18% Standard GST)",
                            color = Color.White,
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = "S.S. HSN Finder & Guidance",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "When & how to apply HSN codes for fabrication",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray,
                                fontSize = 11.sp
                            )
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("hsn_finder_back_btn")) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = PrimaryBlue
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(imageVector = Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        text = { Text("GST Calculator") }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                        text = { Text("HSN & Scenario Guide") }
                    )
                }
            }
        }
    ) { paddingValues ->
        if (selectedTab == 0) {
            // Embedded GST Calculator
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    SmartGstCalculatorCard()
                }
            }
        } else {
            // HSN Rate Finder List
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FAFC))
            ) {
                // Search Input Box
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("hsn_search_input"),
                        placeholder = { Text("Search locker, table, cabinet, 9403, 7308, 9988...") },
                        leadingIcon = {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PrimaryBlue)
                        },
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryBlue,
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor = Color.White,
                            unfocusedBorderColor = Color(0xFFE2E8F0)
                        )
                    )

                    if (searchQuery.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Clear",
                            color = Color(0xFFE11D48),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            modifier = Modifier
                                .clickable { searchQuery = "" }
                                .padding(horizontal = 8.dp, vertical = 12.dp)
                                .testTag("hsn_clear_search_btn")
                        )
                    }
                }

                // Quick Category Filters
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val filterOptions = listOf(
                        "All" to "All Categories",
                        "locker" to "🗄️ SS Lockers & Cabinets (9403)",
                        "table" to "🏢 SS Office Tables (9403)",
                        "job work" to "🛠️ Job Work & Services (9988)",
                        "railing" to "🏛️ Railings & Structures (7308)",
                        "tank" to "🛢️ SS Tanks & Vessels (7309)",
                        "sheet" to "🔩 Raw SS Sheets & Pipes (7219)"
                    )
                    items(filterOptions) { (key, label) ->
                        FilterChip(
                            selected = selectedCategoryFilter == key,
                            onClick = {
                                selectedCategoryFilter = key
                                if (key != "All") searchQuery = ""
                            },
                            label = { Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlueContainer,
                                selectedLabelColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                // Search Results Counter
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Results: ${searchResults.size} HSN & SAC Codes",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF475569)
                    )
                    Text(
                        text = "Standard GST: 18%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue
                    )
                }

                // HSN Code Cards
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(searchResults, key = { it.code + it.description.hashCode() }) { record ->
                        HsnCardItem(
                            record = record,
                            onCopy = { copyHsnToClipboard(record.code) },
                            onUseInItem = {
                                onSelectHsnForNewItem?.invoke(record)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(50.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HsnCardItem(
    record: HsnRecord,
    onCopy: () -> Unit,
    onUseInItem: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("hsn_card_${record.code}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // HSN Code Pill with Copy Action
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(Color(0xFFEEF2FF), RoundedCornerShape(8.dp))
                        .clickable { onCopy() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "HSN " + record.code,
                        color = Color(0xFF1E3A8A),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy HSN",
                        tint = PrimaryBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }

                Text(
                    text = record.chapter,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF64748B),
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 10.dp),
                    maxLines = 1
                )

                // GST Rate Badge
                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                ) {
                    Text(
                        text = "GST ${record.gstRate.toInt()}%",
                        color = PrimaryBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Description
            Text(
                text = record.description,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1E293B),
                fontSize = 13.sp,
                lineHeight = 18.sp
            )

            // When to use scenario guidance
            if (record.whenToUse.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = Color(0xFFF0FDF4),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBBF7D0)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = null,
                            tint = Color(0xFF16A34A),
                            modifier = Modifier
                                .size(16.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = record.whenToUse,
                            color = Color(0xFF166534),
                            fontSize = 12.sp,
                            lineHeight = 17.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Goods or Services pill tag
                Surface(
                    color = if (record.type == "Goods") Color(0xFFFFF7ED) else Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (record.type == "Goods") "📦 Goods / Fabrication" else "🛠️ Services / Job Work",
                        color = if (record.type == "Goods") Color(0xFFC2410C) else Color(0xFF7E22CE),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = onUseInItem,
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create Bill", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
