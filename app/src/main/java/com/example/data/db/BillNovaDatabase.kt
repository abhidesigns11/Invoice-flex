package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.BusinessProfile
import com.example.data.model.Invoice
import com.example.data.model.InvoiceItem
import com.example.data.model.Item
import com.example.data.model.Party
import com.example.data.model.PaymentTransaction
import com.example.data.model.StockTransaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        BusinessProfile::class,
        Party::class,
        Item::class,
        Invoice::class,
        InvoiceItem::class,
        PaymentTransaction::class,
        StockTransaction::class
    ],
    version = 3,
    exportSchema = false
)
abstract class BillNovaDatabase : RoomDatabase() {

    abstract fun billNovaDao(): BillNovaDao

    companion object {
        @Volatile
        private var INSTANCE: BillNovaDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): BillNovaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BillNovaDatabase::class.java,
                    "billnova_database.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.billNovaDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: BillNovaDao) {
                dao.insertOrUpdateProfile(SampleDataGenerator.defaultProfile)
                SampleDataGenerator.sampleParties.forEach { dao.insertParty(it) }
                SampleDataGenerator.sampleItems.forEach { dao.insertItem(it) }
                SampleDataGenerator.getSampleInvoices().forEach { (invoice, items) ->
                    val invId = dao.insertInvoice(invoice)
                    val updatedItems = items.map { it.copy(invoiceId = invId) }
                    dao.insertInvoiceItems(updatedItems)
                }
                SampleDataGenerator.sampleExpenses.forEach { dao.insertTransaction(it) }
            }
        }
    }
}
