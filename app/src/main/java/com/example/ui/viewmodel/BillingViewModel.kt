package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.BillNovaDatabase
import com.example.data.model.BusinessProfile
import com.example.data.model.ExpenseCategory
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.InvoiceType
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.Item
import com.example.data.model.Party
import com.example.data.model.PartyType
import com.example.data.model.PaymentMode
import com.example.data.model.PaymentStatus
import com.example.data.model.PaymentTransaction
import com.example.data.model.StockTransactionType
import com.example.data.repository.BillingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class BillingViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BillingRepository

    init {
        val database = BillNovaDatabase.getDatabase(application, viewModelScope)
        repository = BillingRepository(database.billNovaDao())
    }

    private val _uiState = MutableStateFlow(BillingUiState())
    val uiState: StateFlow<BillingUiState> = _uiState.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.businessProfile.collect { prof ->
                _uiState.update { it.copy(profile = prof) }
            }
        }

        viewModelScope.launch {
            repository.allInvoices.collect { invs ->
                _uiState.update { it.copy(allInvoices = invs) }
            }
        }

        viewModelScope.launch {
            repository.allParties.collect { parties ->
                _uiState.update { it.copy(allParties = parties) }
            }
        }

        viewModelScope.launch {
            repository.allItems.collect { items ->
                val lowStock = items.filter { !it.isService && it.currentStock <= it.minStockAlert }
                val cats = items.map { it.category }.filter { it.isNotBlank() }.distinct().sorted()
                _uiState.update {
                    it.copy(
                        allItems = items,
                        lowStockItems = lowStock,
                        categories = cats
                    )
                }
            }
        }

        viewModelScope.launch {
            repository.allTransactions.collect { txs ->
                _uiState.update { it.copy(transactions = txs) }
            }
        }

        viewModelScope.launch {
            repository.allExpenses.collect { exps ->
                _uiState.update { it.copy(expenses = exps) }
            }
        }

        viewModelScope.launch {
            repository.allStockTransactions.collect { st ->
                _uiState.update { it.copy(stockTransactions = st) }
            }
        }

        viewModelScope.launch {
            repository.totalReceivables.collect { rec ->
                _uiState.update { it.copy(totalReceivables = rec ?: 0.0) }
            }
        }

        viewModelScope.launch {
            repository.totalPayables.collect { pay ->
                _uiState.update { it.copy(totalPayables = pay ?: 0.0) }
            }
        }

        viewModelScope.launch {
            repository.getTodaySales().collect { today ->
                _uiState.update { it.copy(todaySales = today ?: 0.0) }
            }
        }

        viewModelScope.launch {
            repository.totalSales.collect { sales ->
                _uiState.update { it.copy(totalSales = sales ?: 0.0) }
            }
        }

        viewModelScope.launch {
            repository.totalPurchases.collect { purchases ->
                _uiState.update { it.copy(totalPurchases = purchases ?: 0.0) }
            }
        }
    }

    // --- Message handling ---
    fun showMessage(msg: String) {
        _uiState.update { it.copy(userMessage = msg) }
    }

    fun clearMessage() {
        _uiState.update { it.copy(userMessage = null) }
    }

    // --- Invoice Creation & Draft Management ---
    fun initNewInvoiceDraft(type: InvoiceType) {
        viewModelScope.launch {
            val nextNumber = repository.generateNextInvoiceNumber(type)
            val profile = _uiState.value.profile
            _uiState.update {
                it.copy(
                    invoiceDraft = InvoiceDraft(
                        invoiceType = type,
                        invoiceNumber = nextNumber,
                        terms = profile?.termsAndConditions ?: "",
                        items = emptyList()
                    )
                )
            }
        }
    }

    fun setDraftParty(party: Party) {
        _uiState.update {
            val isInterState = party.stateCode.isNotBlank() &&
                    _uiState.value.profile?.stateCode?.isNotBlank() == true &&
                    party.stateCode != _uiState.value.profile?.stateCode

            it.copy(
                invoiceDraft = it.invoiceDraft.copy(
                    partyId = party.id,
                    partyName = party.name,
                    partyPhone = party.phone,
                    partyGstin = party.gstin,
                    partyAddress = "${party.address}, ${party.city}",
                    isInterState = isInterState
                )
            )
        }
    }

    fun setDraftPartyCustom(name: String, phone: String, gstin: String, address: String) {
        _uiState.update {
            it.copy(
                invoiceDraft = it.invoiceDraft.copy(
                    partyId = null,
                    partyName = name,
                    partyPhone = phone,
                    partyGstin = gstin,
                    partyAddress = address
                )
            )
        }
    }

    fun addDraftItemFromCatalog(item: Item) {
        val draftItem = InvoiceDraftItem(
            itemId = item.id,
            itemName = item.name,
            hsnCode = item.hsnCode,
            quantity = 1.0,
            unit = item.unit,
            unitPrice = item.salePrice,
            taxRate = item.taxRate,
            discountPercent = 0.0
        )
        _uiState.update {
            it.copy(
                invoiceDraft = it.invoiceDraft.copy(
                    items = it.invoiceDraft.items + draftItem
                )
            )
        }
    }

    fun addDraftCustomItem(draftItem: InvoiceDraftItem) {
        _uiState.update {
            it.copy(
                invoiceDraft = it.invoiceDraft.copy(
                    items = it.invoiceDraft.items + draftItem
                )
            )
        }
    }

    fun updateDraftItem(tempId: String, updated: InvoiceDraftItem) {
        _uiState.update {
            val updatedItems = it.invoiceDraft.items.map { item ->
                if (item.tempId == tempId) updated else item
            }
            it.copy(invoiceDraft = it.invoiceDraft.copy(items = updatedItems))
        }
    }

    fun removeDraftItem(tempId: String) {
        _uiState.update {
            it.copy(
                invoiceDraft = it.invoiceDraft.copy(
                    items = it.invoiceDraft.items.filter { item -> item.tempId != tempId }
                )
            )
        }
    }

    fun updateDraftDetails(
        invoiceNumber: String? = null,
        date: Long? = null,
        dueDate: Long? = null,
        isInterState: Boolean? = null,
        discountAmount: Double? = null,
        roundOff: Double? = null,
        paidAmount: Double? = null,
        paymentMode: PaymentMode? = null,
        notes: String? = null,
        terms: String? = null,
        eWayBillNo: String? = null,
        vehicleNo: String? = null
    ) {
        _uiState.update {
            var draft = it.invoiceDraft
            invoiceNumber?.let { v -> draft = draft.copy(invoiceNumber = v) }
            date?.let { v -> draft = draft.copy(date = v) }
            dueDate?.let { v -> draft = draft.copy(dueDate = v) }
            isInterState?.let { v -> draft = draft.copy(isInterState = v) }
            discountAmount?.let { v -> draft = draft.copy(discountAmount = v) }
            roundOff?.let { v -> draft = draft.copy(roundOff = v) }
            paidAmount?.let { v -> draft = draft.copy(paidAmount = v) }
            paymentMode?.let { v -> draft = draft.copy(paymentMode = v) }
            notes?.let { v -> draft = draft.copy(notes = v) }
            terms?.let { v -> draft = draft.copy(terms = v) }
            eWayBillNo?.let { v -> draft = draft.copy(eWayBillNo = v) }
            vehicleNo?.let { v -> draft = draft.copy(vehicleNo = v) }
            it.copy(invoiceDraft = draft)
        }
    }

    fun saveDraftInvoice(onSuccess: (Long) -> Unit) {
        val draft = _uiState.value.invoiceDraft
        if (draft.items.isEmpty()) {
            showMessage("Please add at least one item to the invoice.")
            return
        }
        if (draft.partyName.isBlank()) {
            showMessage("Please specify customer/party name.")
            return
        }

        viewModelScope.launch {
            val invoice = Invoice(
                invoiceNumber = draft.invoiceNumber.ifBlank { "INV-${System.currentTimeMillis()}" },
                invoiceType = draft.invoiceType,
                partyId = draft.partyId,
                partyName = draft.partyName,
                partyPhone = draft.partyPhone,
                partyGstin = draft.partyGstin,
                partyAddress = draft.partyAddress,
                date = draft.date,
                dueDate = draft.dueDate,
                subTotal = draft.subTotal,
                totalTax = draft.totalTax,
                cgstAmount = draft.cgstAmount,
                sgstAmount = draft.sgstAmount,
                igstAmount = draft.igstAmount,
                discountAmount = draft.discountAmount,
                roundOff = draft.roundOff,
                grandTotal = draft.calculatedGrandTotal,
                paidAmount = draft.paidAmount,
                balanceDue = draft.balanceDue,
                paymentStatus = draft.paymentStatus,
                paymentMode = draft.paymentMode,
                isInterState = draft.isInterState,
                notes = draft.notes,
                terms = draft.terms,
                eWayBillNo = draft.eWayBillNo,
                vehicleNo = draft.vehicleNo
            )

            val invoiceItems = draft.items.map { item ->
                InvoiceItem(
                    itemId = item.itemId,
                    itemName = item.itemName,
                    hsnCode = item.hsnCode,
                    quantity = item.quantity,
                    unit = item.unit,
                    unitPrice = item.unitPrice,
                    taxRate = item.taxRate,
                    taxAmount = item.taxAmount,
                    discountPercent = item.discountPercent,
                    totalAmount = item.totalAmount
                )
            }

            val invId = repository.createInvoice(invoice, invoiceItems)
            showMessage("Invoice ${invoice.invoiceNumber} created successfully!")
            selectInvoiceById(invId)
            onSuccess(invId)
        }
    }

    // --- POS Quick Billing Mode ---
    fun addPosCartItem(item: Item) {
        _uiState.update { state ->
            val existingIndex = state.posCart.indexOfFirst { it.item.id == item.id }
            val newCart = if (existingIndex >= 0) {
                state.posCart.mapIndexed { idx, cartItem ->
                    if (idx == existingIndex) cartItem.copy(quantity = cartItem.quantity + 1.0) else cartItem
                }
            } else {
                state.posCart + PosCartItem(item = item, quantity = 1.0)
            }
            state.copy(posCart = newCart)
        }
    }

    fun updatePosCartItemQty(itemId: Long, qty: Double) {
        _uiState.update { state ->
            val newCart = if (qty <= 0) {
                state.posCart.filter { it.item.id != itemId }
            } else {
                state.posCart.map { if (it.item.id == itemId) it.copy(quantity = qty) else it }
            }
            state.copy(posCart = newCart)
        }
    }

    fun clearPosCart() {
        _uiState.update { it.copy(posCart = emptyList(), posTenderedCash = 0.0) }
    }

    fun setPosCategory(category: String) {
        _uiState.update { it.copy(posSelectedCategory = category) }
    }

    fun setPosCustomer(name: String, phone: String) {
        _uiState.update { it.copy(posCustomerName = name, posCustomerPhone = phone) }
    }

    fun setPosTenderedCash(amount: Double) {
        _uiState.update { it.copy(posTenderedCash = amount) }
    }

    fun checkoutPosBill(paymentMode: PaymentMode, onSuccess: (Long) -> Unit) {
        val cart = _uiState.value.posCart
        if (cart.isEmpty()) {
            showMessage("Cart is empty.")
            return
        }

        viewModelScope.launch {
            val nextNumber = repository.generateNextInvoiceNumber(InvoiceType.POS_BILL)
            val subTotal = cart.sumOf { it.quantity * it.customPrice * (1 - it.discountPercent / 100.0) }
            val totalTax = cart.sumOf { (it.quantity * it.customPrice * (1 - it.discountPercent / 100.0)) * (it.item.taxRate / 100.0) }
            val grandTotal = subTotal + totalTax

            val invoice = Invoice(
                invoiceNumber = nextNumber,
                invoiceType = InvoiceType.POS_BILL,
                partyId = null,
                partyName = _uiState.value.posCustomerName.ifBlank { "Walk-in Customer" },
                partyPhone = _uiState.value.posCustomerPhone,
                date = System.currentTimeMillis(),
                dueDate = System.currentTimeMillis(),
                subTotal = subTotal,
                totalTax = totalTax,
                cgstAmount = totalTax / 2.0,
                sgstAmount = totalTax / 2.0,
                grandTotal = grandTotal,
                paidAmount = grandTotal,
                balanceDue = 0.0,
                paymentStatus = PaymentStatus.PAID,
                paymentMode = paymentMode,
                notes = "Quick POS Counter Bill"
            )

            val invoiceItems = cart.map { cItem ->
                val base = cItem.quantity * cItem.customPrice * (1 - cItem.discountPercent / 100.0)
                val tax = base * (cItem.item.taxRate / 100.0)
                InvoiceItem(
                    itemId = cItem.item.id,
                    itemName = cItem.item.name,
                    hsnCode = cItem.item.hsnCode,
                    quantity = cItem.quantity,
                    unit = cItem.item.unit,
                    unitPrice = cItem.customPrice,
                    taxRate = cItem.item.taxRate,
                    taxAmount = tax,
                    discountPercent = cItem.discountPercent,
                    totalAmount = base + tax
                )
            }

            val invId = repository.createInvoice(invoice, invoiceItems)
            clearPosCart()
            showMessage("POS Bill $nextNumber completed!")
            selectInvoiceById(invId)
            onSuccess(invId)
        }
    }

    // --- Invoice Viewing / Selection ---
    fun selectInvoiceById(id: Long) {
        viewModelScope.launch {
            val invoiceWithDetails = repository.getInvoiceByIdSync(id)
            _uiState.update { it.copy(selectedInvoice = invoiceWithDetails) }
        }
    }

    fun deleteInvoice(invoiceWithDetails: InvoiceWithDetails) {
        viewModelScope.launch {
            repository.deleteInvoice(invoiceWithDetails)
            _uiState.update { it.copy(selectedInvoice = null) }
            showMessage("Invoice ${invoiceWithDetails.invoice.invoiceNumber} deleted.")
        }
    }

    // --- Parties Management ---
    fun selectParty(party: Party) {
        viewModelScope.launch {
            _uiState.update { it.copy(selectedParty = party) }
            repository.getTransactionsByParty(party.id).collect { txs ->
                _uiState.update { it.copy(selectedPartyTransactions = txs) }
            }
        }
        viewModelScope.launch {
            repository.getInvoicesByParty(party.id).collect { invs ->
                _uiState.update { it.copy(selectedPartyInvoices = invs) }
            }
        }
    }

    fun saveParty(party: Party, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            if (party.id == 0L) {
                repository.saveParty(party.copy(currentBalance = party.openingBalance))
                showMessage("Party '${party.name}' added successfully.")
            } else {
                repository.updateParty(party)
                showMessage("Party '${party.name}' updated.")
            }
            onComplete()
        }
    }

    fun deleteParty(party: Party) {
        viewModelScope.launch {
            repository.deleteParty(party)
            if (_uiState.value.selectedParty?.id == party.id) {
                _uiState.update { it.copy(selectedParty = null) }
            }
            showMessage("Party '${party.name}' removed.")
        }
    }

    fun recordPartyPayment(
        partyId: Long,
        partyName: String,
        amount: Double,
        isPaymentIn: Boolean,
        paymentMode: PaymentMode,
        referenceNo: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.recordPartyPayment(
                partyId = partyId,
                partyName = partyName,
                amount = amount,
                isPaymentIn = isPaymentIn,
                paymentMode = paymentMode,
                referenceNo = referenceNo,
                notes = notes
            )
            val actionName = if (isPaymentIn) "Payment Received (In)" else "Payment Paid (Out)"
            showMessage("$actionName of ₹$amount recorded for $partyName.")
        }
    }

    fun selectPartyById(partyId: Long) {
        viewModelScope.launch {
            val party = _uiState.value.allParties.find { it.id == partyId }
                ?: repository.getPartyById(partyId).firstOrNull()
            party?.let { selectParty(it) }
        }
    }

    fun saveScannedSupplierBill(
        supplierName: String,
        supplierPhone: String,
        supplierGstin: String,
        billNumber: String,
        billAmount: Double,
        paidAmount: Double,
        paymentMode: PaymentMode,
        billPhotoUri: String?,
        hasReminder: Boolean,
        reminderDate: Long?,
        notes: String,
        onSuccess: (Long) -> Unit
    ) {
        viewModelScope.launch {
            // Find existing supplier party or create a new one
            val existingParty = _uiState.value.allParties.find {
                it.name.trim().equals(supplierName.trim(), ignoreCase = true)
            }

            val partyId: Long = if (existingParty != null) {
                existingParty.id
            } else {
                val newParty = Party(
                    name = supplierName.trim(),
                    phone = supplierPhone.ifBlank { "9800000000" },
                    gstin = supplierGstin,
                    partyType = PartyType.SUPPLIER,
                    openingBalance = 0.0,
                    currentBalance = 0.0,
                    notes = "Added via Bill Scanner"
                )
                repository.saveParty(newParty)
            }

            val balanceDue = (billAmount - paidAmount).coerceAtLeast(0.0)
            val paymentStatus = when {
                balanceDue <= 0.0 -> PaymentStatus.PAID
                paidAmount > 0.0 -> PaymentStatus.PARTIAL
                else -> PaymentStatus.UNPAID
            }

            val invoice = Invoice(
                invoiceNumber = billNumber.ifBlank { "BILL-SUP-${System.currentTimeMillis() % 10000}" },
                invoiceType = InvoiceType.PURCHASE_INVOICE,
                partyId = partyId,
                partyName = supplierName.trim(),
                partyPhone = supplierPhone,
                partyGstin = supplierGstin,
                date = System.currentTimeMillis(),
                dueDate = reminderDate ?: (System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000L),
                subTotal = billAmount / 1.18,
                totalTax = billAmount - (billAmount / 1.18),
                cgstAmount = (billAmount - (billAmount / 1.18)) / 2,
                sgstAmount = (billAmount - (billAmount / 1.18)) / 2,
                grandTotal = billAmount,
                paidAmount = paidAmount,
                balanceDue = balanceDue,
                paymentStatus = paymentStatus,
                paymentMode = paymentMode,
                billPhotoUri = billPhotoUri,
                hasPaymentReminder = hasReminder,
                reminderDate = reminderDate,
                notes = notes
            )

            val invoiceItem = InvoiceItem(
                itemName = "Raw Materials / Supplies (Scanned Bill)",
                quantity = 1.0,
                unit = "Lot",
                unitPrice = billAmount / 1.18,
                taxRate = 18.0,
                taxAmount = billAmount - (billAmount / 1.18),
                totalAmount = billAmount
            )

            val invId = repository.createInvoice(invoice, listOf(invoiceItem))

            // If payment was made at bill entry, also log the payment transaction
            if (paidAmount > 0.0) {
                repository.insertTransaction(
                    PaymentTransaction(
                        partyId = partyId,
                        partyName = supplierName.trim(),
                        invoiceId = invId,
                        invoiceNumber = invoice.invoiceNumber,
                        type = "PAYMENT_OUT",
                        amount = paidAmount,
                        paymentMode = paymentMode,
                        notes = "Scanned bill payment for ${invoice.invoiceNumber}"
                    )
                )
            }

            selectPartyById(partyId)
            val reminderMsg = if (hasReminder) " • 1-Month payment reminder active" else ""
            showMessage("Supplier bill ${invoice.invoiceNumber} saved under $supplierName$reminderMsg")
            onSuccess(partyId)
        }
    }

    fun updateInvoicePayment(invoiceId: Long, additionalPayment: Double, paymentMode: PaymentMode, notes: String = "") {
        viewModelScope.launch {
            repository.updateInvoicePayment(invoiceId, additionalPayment, paymentMode, notes)
            _uiState.value.selectedParty?.let { selectParty(it) }
            showMessage("Payment of ₹$additionalPayment recorded.")
        }
    }

    // --- Inventory & Items Management ---
    fun saveItem(item: Item, onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            if (item.id == 0L) {
                repository.saveItem(item)
                showMessage("Item '${item.name}' added to inventory.")
            } else {
                repository.updateItem(item)
                showMessage("Item '${item.name}' updated.")
            }
            onComplete()
        }
    }

    fun deleteItem(item: Item) {
        viewModelScope.launch {
            repository.deleteItem(item)
            showMessage("Item '${item.name}' deleted.")
        }
    }

    fun adjustItemStock(
        itemId: Long,
        itemName: String,
        adjustmentQty: Double,
        reason: StockTransactionType,
        notes: String
    ) {
        viewModelScope.launch {
            repository.adjustStock(itemId, itemName, adjustmentQty, reason, notes)
            showMessage("Stock adjusted for $itemName (${if (adjustmentQty >= 0) "+$adjustmentQty" else "$adjustmentQty"}).")
        }
    }

    // --- Expense Management ---
    fun recordExpense(
        title: String,
        amount: Double,
        category: ExpenseCategory,
        paymentMode: PaymentMode,
        referenceNo: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.recordExpense(
                PaymentTransaction(
                    partyName = title,
                    type = "EXPENSE",
                    amount = amount,
                    paymentMode = paymentMode,
                    referenceNo = referenceNo,
                    notes = notes,
                    expenseCategory = category
                )
            )
            showMessage("Expense of ₹$amount recorded.")
        }
    }

    // --- Business Profile ---
    fun saveBusinessProfile(profile: BusinessProfile) {
        viewModelScope.launch {
            repository.saveBusinessProfile(profile)
            showMessage("Business profile updated successfully.")
        }
    }

    // --- Search & Filters ---
    fun setInvoiceFilter(type: InvoiceType?) {
        _uiState.update { it.copy(invoiceFilterType = type) }
    }

    fun setPartyFilter(type: PartyType) {
        _uiState.update { it.copy(partyFilterType = type) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    // --- Appointments & Reminders ---
    fun addAppointment(task: com.example.data.model.AppointmentTask) {
        _uiState.update {
            it.copy(appointments = listOf(task) + it.appointments)
        }
        showMessage("Appointment scheduled: ${task.title}")
    }

    fun toggleAppointmentComplete(taskId: String) {
        _uiState.update { state ->
            val updated = state.appointments.map {
                if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
            }
            state.copy(appointments = updated)
        }
    }

    fun deleteAppointment(taskId: String) {
        _uiState.update { state ->
            state.copy(appointments = state.appointments.filter { it.id != taskId })
        }
        showMessage("Appointment removed.")
    }

    // --- Reward Points ---
    fun addRewardEntry(entry: com.example.data.model.RewardEntry) {
        _uiState.update {
            it.copy(rewardEntries = listOf(entry) + it.rewardEntries)
        }
        showMessage("${entry.type}: ${entry.points} points for ${entry.partyName}")
    }

    // --- Recurring Bills ---
    fun addRecurringBill(profile: com.example.data.model.RecurringBillProfile) {
        _uiState.update {
            it.copy(recurringBills = listOf(profile) + it.recurringBills)
        }
        showMessage("Recurring invoice rule created for ${profile.partyName}")
    }

    fun deleteRecurringBill(ruleId: String) {
        _uiState.update { state ->
            state.copy(recurringBills = state.recurringBills.filter { it.id != ruleId })
        }
        showMessage("Recurring rule deleted.")
    }

    fun generateRecurringInvoiceNow(profile: com.example.data.model.RecurringBillProfile) {
        viewModelScope.launch {
            val nextNo = repository.generateNextInvoiceNumber(InvoiceType.SALE_INVOICE)
            val subtotal = profile.amount / 1.18
            val tax = profile.amount - subtotal
            val invoice = Invoice(
                invoiceNumber = nextNo,
                invoiceType = InvoiceType.SALE_INVOICE,
                partyName = profile.partyName,
                partyPhone = profile.partyPhone,
                subTotal = subtotal,
                totalTax = tax,
                cgstAmount = tax / 2,
                sgstAmount = tax / 2,
                grandTotal = profile.amount,
                paidAmount = 0.0,
                balanceDue = profile.amount,
                paymentStatus = PaymentStatus.UNPAID,
                notes = "Auto-generated recurring billing for ${profile.itemName} (${profile.frequency})"
            )
            val invItem = InvoiceItem(
                itemName = profile.itemName,
                hsnCode = "9403",
                quantity = 1.0,
                unit = "Month",
                unitPrice = subtotal,
                taxRate = 18.0,
                taxAmount = tax,
                totalAmount = profile.amount
            )
            repository.createInvoice(invoice, listOf(invItem))
            showMessage("Recurring Invoice $nextNo generated for ₹${profile.amount.toInt()}")
        }
    }

    // --- Online Store ---
    fun updateOnlineStoreConfig(config: com.example.data.model.OnlineStoreConfig) {
        _uiState.update { it.copy(onlineStoreConfig = config) }
        showMessage("Online store settings saved.")
    }
}

