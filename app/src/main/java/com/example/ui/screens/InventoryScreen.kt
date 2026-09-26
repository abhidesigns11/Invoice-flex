package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Item
import com.example.data.model.StockTransactionType
import com.example.ui.components.Formatters
import com.example.ui.theme.DangerRed
import com.example.ui.theme.DangerRedLight
import com.example.ui.theme.PrimaryBlue
import com.example.ui.theme.PrimaryBlueContainer
import com.example.ui.theme.PrimaryNavy
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.SuccessGreenLight
import com.example.ui.theme.WarningGold
import com.example.ui.theme.WarningGoldLight
import com.example.ui.viewmodel.BillingUiState
import com.example.ui.viewmodel.BillingViewModel

val ssFabricationCategories = listOf(
    "S.S. Lockers",
    "S.S. Cabinets",
    "S.S. Office Tables",
    "Railings & Architectural",
    "Custom Fabrication & Job Work",
    "Tanks & Vessels",
    "Raw Materials (Sheets/Pipes)"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InventoryScreen(
    state: BillingUiState,
    viewModel: BillingViewModel,
    onNavigateBack: () -> Unit = {},
    onNavigateToHsnFinder: () -> Unit = {}
) {
    BackHandler {
        onNavigateBack()
    }

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var filterLowStockOnly by remember { mutableStateOf(false) }

    var showAddItemDialog by remember { mutableStateOf(false) }
    var editingItem by remember { mutableStateOf<Item?>(null) }
    var adjustingItem by remember { mutableStateOf<Item?>(null) }
    var viewingSpecsItem by remember { mutableStateOf<Item?>(null) }

    val filteredItems = remember(state.allItems, searchQuery, selectedCategory, filterLowStockOnly) {
        state.allItems.filter { item ->
            val matchesSearch = searchQuery.isBlank() ||
                    item.name.contains(searchQuery, ignoreCase = true) ||
                    item.itemCode.contains(searchQuery, ignoreCase = true) ||
                    item.hsnCode.contains(searchQuery, ignoreCase = true) ||
                    item.description.contains(searchQuery, ignoreCase = true) ||
                    item.category.contains(searchQuery, ignoreCase = true)
            val matchesCategory = selectedCategory == "All" || item.category == selectedCategory
            val matchesLowStock = !filterLowStockOnly || (!item.isService && item.currentStock <= item.minStockAlert)
            matchesSearch && matchesCategory && matchesLowStock
        }
    }

    val totalCatalogValuation = remember(state.allItems) {
        state.allItems.sumOf { it.currentStock * it.purchasePrice }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Column {
                            Text("S.S. Product Catalog & Specs", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            Text("Lockers, Cabinets, Office Tables & SS Fabrication", style = MaterialTheme.typography.bodySmall, color = Color.Gray, fontSize = 11.sp)
                        }
                    }
                },
                actions = {
                    Button(
                        onClick = onNavigateToHsnFinder,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlueContainer, contentColor = PrimaryBlue),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.padding(end = 8.dp).height(34.dp).testTag("btn_catalog_hsn_finder")
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("HSN Finder", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingItem = null
                    showAddItemDialog = true
                },
                containerColor = PrimaryNavy,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("inventory_fab_add")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "+ Add S.S. Product / Job", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FAFC))
        ) {
            // iPhone Frosted Dual-Summary Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Material & Product Stock Valuation",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF64748B),
                            fontSize = 11.sp
                        )
                        Text(
                            text = Formatters.formatCurrency(totalCatalogValuation, showDecimals = false),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryNavy
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Surface(
                            color = Color(0xFFEFF6FF),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFBFDBFE))
                        ) {
                            Text(
                                text = "${state.allItems.size} Products",
                                color = PrimaryBlue,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        if (state.lowStockItems.isNotEmpty()) {
                            Surface(
                                color = WarningGoldLight,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "${state.lowStockItems.size} Low",
                                    color = WarningGold,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp)
                    .testTag("inventory_search_input"),
                placeholder = { Text("Search S.S. Locker, Cabinet, Office Table, HSN 9403...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = PrimaryBlue) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
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

            // Category & Low Stock Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = filterLowStockOnly,
                        onClick = { filterLowStockOnly = !filterLowStockOnly },
                        label = { Text("⚠️ Low Stock (${state.lowStockItems.size})", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = WarningGoldLight,
                            selectedLabelColor = WarningGold
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                item {
                    FilterChip(
                        selected = selectedCategory == "All",
                        onClick = { selectedCategory = "All" },
                        label = { Text("All Products", fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryBlueContainer,
                            selectedLabelColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
                ssFabricationCategories.forEach { cat ->
                    item {
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryBlueContainer,
                                selectedLabelColor = PrimaryBlue
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // Products Catalog List
            if (filteredItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Inventory2, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(54.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "No matching SS products found", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Try searching for Locker, Cabinet, Table, or Railing", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(filteredItems, key = { it.id }) { item ->
                        CatalogItemCard(
                            item = item,
                            onViewSpecs = { viewingSpecsItem = item },
                            onEdit = {
                                editingItem = item
                                showAddItemDialog = true
                            },
                            onAdjustStock = {
                                adjustingItem = item
                            },
                            onDelete = { viewModel.deleteItem(item) }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Item Dialog
    if (showAddItemDialog) {
        AddEditCatalogItemDialog(
            initial = editingItem,
            categories = ssFabricationCategories,
            onDismiss = { showAddItemDialog = false },
            onSave = { savedItem ->
                viewModel.saveItem(savedItem)
                showAddItemDialog = false
            }
        )
    }

    // Adjust Stock Dialog
    if (adjustingItem != null) {
        val target = adjustingItem!!
        AdjustStockDialog(
            item = target,
            onDismiss = { adjustingItem = null },
            onConfirm = { type, qty, note ->
                val adjustedQty = if (type == StockTransactionType.ADJUSTMENT_REDUCE || type == StockTransactionType.STOCK_OUT || type == StockTransactionType.DAMAGED) -qty else qty
                viewModel.adjustItemStock(target.id, target.name, adjustedQty, type, note)
                adjustingItem = null
            }
        )
    }

    // Engineering Spec Sheet Dialog
    if (viewingSpecsItem != null) {
        EngineeringSpecSheetDialog(
            item = viewingSpecsItem!!,
            onDismiss = { viewingSpecsItem = null }
        )
    }
}

@Composable
fun CatalogItemCard(
    item: Item,
    onViewSpecs: () -> Unit,
    onEdit: () -> Unit,
    onAdjustStock: () -> Unit,
    onDelete: () -> Unit
) {
    val isLowStock = !item.isService && item.currentStock <= item.minStockAlert

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("catalog_item_card_${item.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Badge + HSN Code + Stock Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = item.category,
                            color = Color(0xFF334155),
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }

                    if (item.hsnCode.isNotEmpty()) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "HSN: ${item.hsnCode}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                if (item.isService) {
                    Surface(
                        color = Color(0xFFF3E8FF),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "🛠️ Job Service",
                            color = Color(0xFF7E22CE),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else {
                    Surface(
                        color = if (isLowStock) DangerRedLight else SuccessGreenLight,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = if (isLowStock) "⚠️ Stock: ${item.currentStock.toInt()} ${item.unit}" else "In Stock: ${item.currentStock.toInt()} ${item.unit}",
                            color = if (isLowStock) DangerRed else SuccessGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Item Name
            Text(
                text = item.name,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleSmall,
                fontSize = 15.sp,
                color = Color(0xFF0F172A)
            )

            // Description / Specs
            if (item.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(8.dp))

            // Pricing & Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = Formatters.formatCurrency(item.salePrice),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = PrimaryNavy
                        )
                        Text(
                            text = " / ${item.unit}",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(start = 2.dp, bottom = 1.dp)
                        )
                    }
                    if (item.purchasePrice > 0) {
                        Text(
                            text = "Cost: ${Formatters.formatCurrency(item.purchasePrice)} | GST: ${item.taxRate.toInt()}%",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onViewSpecs,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Specs 📐", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PrimaryBlue)
                    }

                    if (!item.isService) {
                        IconButton(onClick = onAdjustStock, modifier = Modifier.size(32.dp)) {
                            Icon(imageVector = Icons.Default.Tune, contentDescription = "Stock", tint = Color(0xFF475569), modifier = Modifier.size(18.dp))
                        }
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit", tint = PrimaryBlue, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun EngineeringSpecSheetDialog(
    item: Item,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.PrecisionManufacturing, contentDescription = null, tint = PrimaryBlue)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Engineering & Material Specs", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF0F172A))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = item.description, fontSize = 12.sp, color = Color(0xFF475569))
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SpecItemBox(label = "Category", value = item.category, modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    SpecItemBox(label = "HSN / SAC", value = item.hsnCode.ifEmpty { "9403" }, modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SpecItemBox(label = "Default Material", value = "SS 304 / SS 316", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    SpecItemBox(label = "Standard Unit", value = item.unit, modifier = Modifier.weight(1f))
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    SpecItemBox(label = "Tax Rate (GST)", value = "${item.taxRate.toInt()}%", modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(8.dp))
                    SpecItemBox(label = "Current Stock", value = "${item.currentStock.toInt()} ${item.unit}", modifier = Modifier.weight(1f))
                }

                Surface(
                    color = Color(0xFFEFF6FF),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Standard Selling Price:", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        Text(Formatters.formatCurrency(item.salePrice), fontWeight = FontWeight.Bold, color = PrimaryNavy, fontSize = 15.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Close")
            }
        }
    )
}

@Composable
fun SpecItemBox(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(
        color = Color(0xFFF8FAFC),
        shape = RoundedCornerShape(8.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = label, fontSize = 10.sp, color = Color(0xFF64748B))
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1E293B))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCatalogItemDialog(
    initial: Item?,
    categories: List<String>,
    onDismiss: () -> Unit,
    onSave: (Item) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "S.S. Lockers") }
    var unit by remember { mutableStateOf(initial?.unit ?: "Pcs") }
    var salePrice by remember { mutableStateOf(initial?.salePrice?.toString() ?: "") }
    var purchasePrice by remember { mutableStateOf(initial?.purchasePrice?.toString() ?: "") }
    var taxRate by remember { mutableStateOf(initial?.taxRate?.toString() ?: "18") }
    var hsnCode by remember { mutableStateOf(initial?.hsnCode ?: "9403") }
    var openingStock by remember { mutableStateOf(initial?.currentStock?.toString() ?: "10") }
    var minStockAlert by remember { mutableStateOf(initial?.minStockAlert?.toString() ?: "2") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var isService by remember { mutableStateOf(initial?.isService ?: false) }

    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (initial == null) "Add S.S. Product to Catalog" else "Edit S.S. Product", fontWeight = FontWeight.Bold) },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Product / Job Work Name *") },
                        placeholder = { Text("e.g. S.S. 304 6-Door Staff Locker") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("add_item_input_name")
                    )
                }

                item {
                    ExposedDropdownMenuBox(
                        expanded = categoryDropdownExpanded,
                        onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = category,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                            modifier = Modifier.menuAnchor().fillMaxWidth()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryDropdownExpanded,
                            onDismissRequest = { categoryDropdownExpanded = false }
                        ) {
                            categories.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat) },
                                    onClick = {
                                        category = cat
                                        if (cat == "Custom Fabrication & Job Work") {
                                            isService = true
                                            hsnCode = "9988"
                                            unit = "Hour"
                                        } else if (cat == "Raw Materials (Sheets/Pipes)") {
                                            isService = false
                                            hsnCode = "7219"
                                            unit = "Kg"
                                        } else {
                                            isService = false
                                            hsnCode = "9403"
                                            unit = "Pcs"
                                        }
                                        categoryDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Engineering Specs (Grade, Dimensions, Gauge)") },
                        placeholder = { Text("e.g. SS 304, 18 Gauge (1.2mm), Satin Matt finish, Master key") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = salePrice,
                            onValueChange = { salePrice = it },
                            label = { Text("Sale Rate (₹) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f).testTag("add_item_input_sale_price")
                        )
                        OutlinedTextField(
                            value = unit,
                            onValueChange = { unit = it },
                            label = { Text("Unit (Pcs/Kg/Mtr)") },
                            modifier = Modifier.weight(0.8f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = purchasePrice,
                            onValueChange = { purchasePrice = it },
                            label = { Text("Purchase Cost (₹)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = hsnCode,
                            onValueChange = { hsnCode = it },
                            label = { Text("HSN Code (e.g. 9403)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                item {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = openingStock,
                            onValueChange = { openingStock = it },
                            label = { Text("Current Stock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minStockAlert,
                            onValueChange = { minStockAlert = it },
                            label = { Text("Min Alert Stock") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val sPrice = salePrice.toDoubleOrNull() ?: 0.0
                    val pPrice = purchasePrice.toDoubleOrNull() ?: 0.0
                    val tRate = taxRate.toDoubleOrNull() ?: 18.0
                    val stock = openingStock.toDoubleOrNull() ?: 0.0
                    val minStock = minStockAlert.toDoubleOrNull() ?: 2.0

                    val itemToSave = (initial ?: Item(
                        name = name.ifBlank { "S.S. Product" },
                        category = category,
                        unit = unit.ifBlank { "Pcs" }
                    )).copy(
                        name = name.ifBlank { "S.S. Product" },
                        category = category,
                        unit = unit.ifBlank { "Pcs" },
                        salePrice = sPrice,
                        purchasePrice = pPrice,
                        taxRate = tRate,
                        hsnCode = hsnCode.ifBlank { "9403" },
                        currentStock = stock,
                        minStockAlert = minStock,
                        description = description,
                        isService = isService
                    )
                    onSave(itemToSave)
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("add_item_dialog_save_btn")
            ) {
                Text("Save to Catalog")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AdjustStockDialog(
    item: Item,
    onDismiss: () -> Unit,
    onConfirm: (StockTransactionType, Double, String) -> Unit
) {
    var quantityText by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(StockTransactionType.ADJUSTMENT_ADD) }
    var noteText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Adjust Stock Quantity", fontWeight = FontWeight.Bold)
                Text(item.name, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Surface(
                    color = Color(0xFFF1F5F9),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Current Stock: ${item.currentStock.toInt()} ${item.unit}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = PrimaryNavy,
                        modifier = Modifier.padding(10.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedType == StockTransactionType.ADJUSTMENT_ADD,
                        onClick = { selectedType = StockTransactionType.ADJUSTMENT_ADD },
                        label = { Text("+ Add Stock") }
                    )
                    FilterChip(
                        selected = selectedType == StockTransactionType.ADJUSTMENT_REDUCE,
                        onClick = { selectedType = StockTransactionType.ADJUSTMENT_REDUCE },
                        label = { Text("- Reduce Stock") }
                    )
                }

                OutlinedTextField(
                    value = quantityText,
                    onValueChange = { quantityText = it },
                    label = { Text("Quantity (${item.unit}) *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = noteText,
                    onValueChange = { noteText = it },
                    label = { Text("Reason / Note (Optional)") },
                    placeholder = { Text("e.g. New consignment / Physical recount") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val qty = quantityText.toDoubleOrNull() ?: 0.0
                    if (qty > 0) {
                        onConfirm(selectedType, qty, noteText)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Confirm Adjustment")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
