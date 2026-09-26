package com.example.data.repository

import com.example.data.db.BillNovaDao
import com.example.data.db.SampleDataGenerator
import com.example.data.model.BusinessProfile
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
import com.example.data.model.StockTransaction
import com.example.data.model.StockTransactionType
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class BillingRepository(private val dao: BillNovaDao) {

    // --- Profile ---
    val businessProfile: Flow<BusinessProfile?> = dao.getBusinessProfileFlow()
    suspend fun getBusinessProfileSync(): BusinessProfile? = dao.getBusinessProfile()
    suspend fun saveBusinessProfile(profile: BusinessProfile) = dao.insertOrUpdateProfile(profile)

    // --- Parties ---
    val allParties: Flow<List<Party>> = dao.getAllParties()
    val customers: Flow<List<Party>> = dao.getPartiesByType(PartyType.CUSTOMER)
    val suppliers: Flow<List<Party>> = dao.getPartiesByType(PartyType.SUPPLIER)
    fun getParty(id: Long): Flow<Party?> = dao.getPartyById(id)
    fun getPartyById(id: Long): Flow<Party?> = dao.getPartyById(id)
    fun searchParties(query: String): Flow<List<Party>> = dao.searchParties(query)

    suspend fun saveParty(party: Party): Long = dao.insertParty(party)
    suspend fun updateParty(party: Party) = dao.updateParty(party)
    suspend fun deleteParty(party: Party) = dao.deleteParty(party)

    // --- Items ---
    val allItems: Flow<List<Item>> = dao.getAllItems()
    val lowStockItems: Flow<List<Item>> = dao.getLowStockItems()
    val categories: Flow<List<String>> = dao.getAllCategories()
    fun getItem(id: Long): Flow<Item?> = dao.getItemById(id)
    fun searchItems(query: String): Flow<List<Item>> = dao.searchItems(query)

    suspend fun saveItem(item: Item): Long = dao.insertItem(item)
    suspend fun updateItem(item: Item) = dao.updateItem(item)
    suspend fun deleteItem(item: Item) = dao.deleteItem(item)

    suspend fun adjustStock(
        itemId: Long,
        itemName: String,
        adjustmentQty: Double, // positive or negative
        reason: StockTransactionType,
        notes: String
    ) {
        dao.updateItemStock(itemId, adjustmentQty)
        dao.insertStockTransaction(
            StockTransaction(
                itemId = itemId,
                itemName = itemName,
                type = reason,
                quantity = adjustmentQty,
                notes = notes
            )
        )
    }

    // --- Invoices ---
    val allInvoices: Flow<List<InvoiceWithDetails>> = dao.getAllInvoicesWithDetails()
    fun getInvoicesByType(type: InvoiceType): Flow<List<InvoiceWithDetails>> = dao.getInvoicesByType(type)
    fun getInvoiceById(id: Long): Flow<InvoiceWithDetails?> = dao.getInvoiceWithDetailsById(id)
    suspend fun getInvoiceByIdSync(id: Long): InvoiceWithDetails? = dao.getInvoiceWithDetailsByIdSync(id)
    fun getInvoicesByParty(partyId: Long): Flow<List<InvoiceWithDetails>> = dao.getInvoicesByPartyId(partyId)

    suspend fun createInvoice(
        invoice: Invoice,
        items: List<InvoiceItem>,
        updateStockAndBalance: Boolean = true
    ): Long {
        val invoiceId = dao.insertInvoice(invoice)
        val invoiceItemsWithId = items.map { it.copy(invoiceId = invoiceId) }
        dao.insertInvoiceItems(invoiceItemsWithId)

        if (updateStockAndBalance) {
            // Update party balance if party assigned and there is an outstanding amount
            invoice.partyId?.let { pId ->
                when (invoice.invoiceType) {
                    InvoiceType.SALE_INVOICE, InvoiceType.POS_BILL -> {
                        // Customer owes the balanceDue
                        if (invoice.balanceDue > 0) {
                            dao.updatePartyBalance(pId, invoice.balanceDue)
                        }
                    }
                    InvoiceType.PURCHASE_INVOICE -> {
                        // We owe supplier the balanceDue
                        if (invoice.balanceDue > 0) {
                            dao.updatePartyBalance(pId, -invoice.balanceDue)
                        }
                    }
                    InvoiceType.CREDIT_NOTE_RETURN -> {
                        // Reduces customer debt
                        dao.updatePartyBalance(pId, -invoice.grandTotal)
                    }
                    else -> {}
                }
            }

            // Update item stock inventory
            items.forEach { invItem ->
                invItem.itemId?.let { itemId ->
                    when (invoice.invoiceType) {
                        InvoiceType.SALE_INVOICE, InvoiceType.POS_BILL -> {
                            dao.updateItemStock(itemId, -invItem.quantity)
                            dao.insertStockTransaction(
                                StockTransaction(
                                    itemId = itemId,
                                    itemName = invItem.itemName,
                                    type = StockTransactionType.STOCK_OUT,
                                    quantity = invItem.quantity,
                                    referenceInvoiceId = invoiceId,
                                    referenceInvoiceNumber = invoice.invoiceNumber,
                                    notes = "Sold in ${invoice.invoiceNumber}"
                                )
                            )
                        }
                        InvoiceType.PURCHASE_INVOICE -> {
                            dao.updateItemStock(itemId, invItem.quantity)
                            dao.insertStockTransaction(
                                StockTransaction(
                                    itemId = itemId,
                                    itemName = invItem.itemName,
                                    type = StockTransactionType.STOCK_IN,
                                    quantity = invItem.quantity,
                                    referenceInvoiceId = invoiceId,
                                    referenceInvoiceNumber = invoice.invoiceNumber,
                                    notes = "Purchased in ${invoice.invoiceNumber}"
                                )
                            )
                        }
                        InvoiceType.CREDIT_NOTE_RETURN -> {
                            dao.updateItemStock(itemId, invItem.quantity)
                            dao.insertStockTransaction(
                                StockTransaction(
                                    itemId = itemId,
                                    itemName = invItem.itemName,
                                    type = StockTransactionType.STOCK_IN,
                                    quantity = invItem.quantity,
                                    referenceInvoiceId = invoiceId,
                                    referenceInvoiceNumber = invoice.invoiceNumber,
                                    notes = "Returned in ${invoice.invoiceNumber}"
                                )
                            )
                        }
                        else -> {}
                    }
                }
            }

            // Record payment transaction if amount was paid
            if (invoice.paidAmount > 0) {
                dao.insertTransaction(
                    PaymentTransaction(
                        partyId = invoice.partyId,
                        partyName = invoice.partyName,
                        invoiceId = invoiceId,
                        invoiceNumber = invoice.invoiceNumber,
                        type = if (invoice.invoiceType == InvoiceType.PURCHASE_INVOICE) "PAYMENT_OUT" else "PAYMENT_IN",
                        amount = invoice.paidAmount,
                        paymentMode = invoice.paymentMode,
                        date = invoice.date,
                        notes = "Payment for ${invoice.invoiceNumber}"
                    )
                )
            }
        }

        return invoiceId
    }

    suspend fun deleteInvoice(invoiceWithDetails: InvoiceWithDetails) {
        dao.deleteInvoiceItems(invoiceWithDetails.invoice.id)
        dao.deleteInvoice(invoiceWithDetails.invoice)
    }

    suspend fun updateInvoicePayment(invoiceId: Long, additionalPayment: Double, paymentMode: PaymentMode, notes: String = "") {
        val invWithDetails = dao.getInvoiceWithDetailsByIdSync(invoiceId) ?: return
        val inv = invWithDetails.invoice
        val newPaid = (inv.paidAmount + additionalPayment).coerceAtMost(inv.grandTotal)
        val newBalanceDue = (inv.grandTotal - newPaid).coerceAtLeast(0.0)
        val newStatus = when {
            newBalanceDue <= 0.0 -> PaymentStatus.PAID
            newPaid > 0.0 -> PaymentStatus.PARTIAL
            else -> PaymentStatus.UNPAID
        }
        dao.updateInvoice(inv.copy(paidAmount = newPaid, balanceDue = newBalanceDue, paymentStatus = newStatus))

        inv.partyId?.let { pId ->
            when (inv.invoiceType) {
                InvoiceType.SALE_INVOICE, InvoiceType.POS_BILL -> {
                    dao.updatePartyBalance(pId, -additionalPayment)
                }
                InvoiceType.PURCHASE_INVOICE -> {
                    dao.updatePartyBalance(pId, additionalPayment)
                }
                else -> {}
            }
        }

        dao.insertTransaction(
            PaymentTransaction(
                partyId = inv.partyId,
                partyName = inv.partyName,
                invoiceId = inv.id,
                invoiceNumber = inv.invoiceNumber,
                type = if (inv.invoiceType == InvoiceType.PURCHASE_INVOICE) "PAYMENT_OUT" else "PAYMENT_IN",
                amount = additionalPayment,
                paymentMode = paymentMode,
                date = System.currentTimeMillis(),
                notes = if (notes.isNotBlank()) notes else "Payment for ${inv.invoiceNumber}"
            )
        )
    }

    suspend fun generateNextInvoiceNumber(type: InvoiceType): String {
        val profile = dao.getBusinessProfile() ?: SampleDataGenerator.defaultProfile
        val count = dao.getInvoiceCountByType(type) + 1
        val prefix = when (type) {
            InvoiceType.SALE_INVOICE -> profile.invoicePrefix
            InvoiceType.QUOTATION_ESTIMATE -> profile.estimatePrefix
            InvoiceType.POS_BILL -> profile.posPrefix
            InvoiceType.PURCHASE_INVOICE -> "PUR-2026-"
            InvoiceType.CREDIT_NOTE_RETURN -> "CRN-2026-"
            InvoiceType.DELIVERY_CHALLAN -> "DC-2026-"
        }
        return String.format("%s%03d", prefix, count)
    }

    // --- Payments & Ledgers ---
    val allTransactions: Flow<List<PaymentTransaction>> = dao.getAllTransactions()
    val allExpenses: Flow<List<PaymentTransaction>> = dao.getAllExpenses()
    fun getTransactionsByParty(partyId: Long): Flow<List<PaymentTransaction>> = dao.getTransactionsByParty(partyId)

    suspend fun recordPartyPayment(
        partyId: Long,
        partyName: String,
        amount: Double,
        isPaymentIn: Boolean, // true = collected from customer, false = paid to supplier
        paymentMode: PaymentMode,
        referenceNo: String,
        notes: String
    ): Long {
        val type = if (isPaymentIn) "PAYMENT_IN" else "PAYMENT_OUT"
        val txId = dao.insertTransaction(
            PaymentTransaction(
                partyId = partyId,
                partyName = partyName,
                type = type,
                amount = amount,
                paymentMode = paymentMode,
                referenceNo = referenceNo,
                notes = notes
            )
        )
        // If payment in from customer, balance reduces (positive balance becomes less)
        // If payment out to supplier, payable debt reduces (negative balance becomes closer to 0)
        val balanceDelta = if (isPaymentIn) -amount else amount
        dao.updatePartyBalance(partyId, balanceDelta)
        return txId
    }

    suspend fun insertTransaction(tx: PaymentTransaction): Long = dao.insertTransaction(tx)
    suspend fun recordExpense(tx: PaymentTransaction): Long = dao.insertTransaction(tx)
    suspend fun deleteTransaction(tx: PaymentTransaction) = dao.deleteTransaction(tx)

    // --- Stock Transactions ---
    val allStockTransactions: Flow<List<StockTransaction>> = dao.getAllStockTransactions()
    fun getStockTransactionsByItem(itemId: Long): Flow<List<StockTransaction>> = dao.getStockTransactionsByItem(itemId)

    // --- Dashboard Aggregates ---
    val totalReceivables: Flow<Double?> = dao.getTotalReceivables()
    val totalPayables: Flow<Double?> = dao.getTotalPayables()
    val totalSales: Flow<Double?> = dao.getTotalSales()
    val totalPurchases: Flow<Double?> = dao.getTotalPurchases()

    fun getTodaySales(): Flow<Double?> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return dao.getTodaySales(calendar.timeInMillis)
    }
}
