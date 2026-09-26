package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.BusinessProfile
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.InvoiceType
import com.example.data.model.InvoiceWithDetails
import com.example.data.model.Item
import com.example.data.model.Party
import com.example.data.model.PartyType
import com.example.data.model.PaymentTransaction
import com.example.data.model.StockTransaction
import kotlinx.coroutines.flow.Flow

@Dao
interface BillNovaDao {

    // --- Business Profile ---
    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    fun getBusinessProfileFlow(): Flow<BusinessProfile?>

    @Query("SELECT * FROM business_profile WHERE id = 1 LIMIT 1")
    suspend fun getBusinessProfile(): BusinessProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: BusinessProfile)

    // --- Parties (Customers & Suppliers) ---
    @Query("SELECT * FROM parties ORDER BY name ASC")
    fun getAllParties(): Flow<List<Party>>

    @Query("SELECT * FROM parties WHERE partyType = :type ORDER BY name ASC")
    fun getPartiesByType(type: PartyType): Flow<List<Party>>

    @Query("SELECT * FROM parties WHERE id = :id LIMIT 1")
    fun getPartyById(id: Long): Flow<Party?>

    @Query("SELECT * FROM parties WHERE id = :id LIMIT 1")
    suspend fun getPartyByIdSync(id: Long): Party?

    @Query("SELECT * FROM parties WHERE name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchParties(query: String): Flow<List<Party>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertParty(party: Party): Long

    @Update
    suspend fun updateParty(party: Party)

    @Delete
    suspend fun deleteParty(party: Party)

    @Query("UPDATE parties SET currentBalance = currentBalance + :amountDiff WHERE id = :partyId")
    suspend fun updatePartyBalance(partyId: Long, amountDiff: Double)

    // --- Items / Stock ---
    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE isService = 0 AND currentStock <= minStockAlert ORDER BY currentStock ASC")
    fun getLowStockItems(): Flow<List<Item>>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    fun getItemById(id: Long): Flow<Item?>

    @Query("SELECT * FROM items WHERE id = :id LIMIT 1")
    suspend fun getItemByIdSync(id: Long): Item?

    @Query("SELECT * FROM items WHERE name LIKE '%' || :query || '%' OR itemCode LIKE '%' || :query || '%' OR barcode LIKE '%' || :query || '%' ORDER BY name ASC")
    fun searchItems(query: String): Flow<List<Item>>

    @Query("SELECT DISTINCT category FROM items WHERE category != '' ORDER BY category ASC")
    fun getAllCategories(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: Item): Long

    @Update
    suspend fun updateItem(item: Item)

    @Delete
    suspend fun deleteItem(item: Item)

    @Query("UPDATE items SET currentStock = currentStock + :qtyChange WHERE id = :itemId")
    suspend fun updateItemStock(itemId: Long, qtyChange: Double)

    // --- Invoices & Items ---
    @Transaction
    @Query("SELECT * FROM invoices ORDER BY date DESC, id DESC")
    fun getAllInvoicesWithDetails(): Flow<List<InvoiceWithDetails>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE invoiceType = :type ORDER BY date DESC, id DESC")
    fun getInvoicesByType(type: InvoiceType): Flow<List<InvoiceWithDetails>>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    fun getInvoiceWithDetailsById(id: Long): Flow<InvoiceWithDetails?>

    @Transaction
    @Query("SELECT * FROM invoices WHERE id = :id LIMIT 1")
    suspend fun getInvoiceWithDetailsByIdSync(id: Long): InvoiceWithDetails?

    @Transaction
    @Query("SELECT * FROM invoices WHERE partyId = :partyId ORDER BY date DESC")
    fun getInvoicesByPartyId(partyId: Long): Flow<List<InvoiceWithDetails>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoice(invoice: Invoice): Long

    @Update
    suspend fun updateInvoice(invoice: Invoice)

    @Delete
    suspend fun deleteInvoice(invoice: Invoice)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvoiceItems(items: List<InvoiceItem>)

    @Query("DELETE FROM invoice_items WHERE invoiceId = :invoiceId")
    suspend fun deleteInvoiceItems(invoiceId: Long)

    @Query("SELECT COUNT(*) FROM invoices WHERE invoiceType = :type")
    suspend fun getInvoiceCountByType(type: InvoiceType): Int

    // --- Payments & Expenses ---
    @Query("SELECT * FROM payment_transactions ORDER BY date DESC, id DESC")
    fun getAllTransactions(): Flow<List<PaymentTransaction>>

    @Query("SELECT * FROM payment_transactions WHERE partyId = :partyId ORDER BY date DESC")
    fun getTransactionsByParty(partyId: Long): Flow<List<PaymentTransaction>>

    @Query("SELECT * FROM payment_transactions WHERE type = 'EXPENSE' ORDER BY date DESC")
    fun getAllExpenses(): Flow<List<PaymentTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: PaymentTransaction): Long

    @Delete
    suspend fun deleteTransaction(tx: PaymentTransaction)

    // --- Stock Transactions / History ---
    @Query("SELECT * FROM stock_transactions ORDER BY date DESC")
    fun getAllStockTransactions(): Flow<List<StockTransaction>>

    @Query("SELECT * FROM stock_transactions WHERE itemId = :itemId ORDER BY date DESC")
    fun getStockTransactionsByItem(itemId: Long): Flow<List<StockTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStockTransaction(tx: StockTransaction): Long

    // --- Dashboard Aggregates ---
    @Query("SELECT SUM(currentBalance) FROM parties WHERE currentBalance > 0 AND partyType = 'CUSTOMER'")
    fun getTotalReceivables(): Flow<Double?>

    @Query("SELECT SUM(ABS(currentBalance)) FROM parties WHERE currentBalance < 0 OR partyType = 'SUPPLIER'")
    fun getTotalPayables(): Flow<Double?>

    @Query("SELECT SUM(grandTotal) FROM invoices WHERE invoiceType IN ('SALE_INVOICE', 'POS_BILL') AND date >= :startOfDay")
    fun getTodaySales(startOfDay: Long): Flow<Double?>

    @Query("SELECT SUM(grandTotal) FROM invoices WHERE invoiceType IN ('SALE_INVOICE', 'POS_BILL')")
    fun getTotalSales(): Flow<Double?>

    @Query("SELECT SUM(grandTotal) FROM invoices WHERE invoiceType = 'PURCHASE_INVOICE'")
    fun getTotalPurchases(): Flow<Double?>
}
