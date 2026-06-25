package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "homes")
data class Home(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String
)

@Serializable
@Entity(
    tableName = "bills",
    foreignKeys = [
        ForeignKey(
            entity = Home::class,
            parentColumns = ["id"],
            childColumns = ["homeId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("homeId")]
)
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val totalAmount: Double,
    val date: Long = System.currentTimeMillis(),
    val category: String,
    val shopName: String,
    val homeId: Int? = null,
    val imagePath: String? = null,
    val splitCount: Int = 1,
    val dueDate: Long? = null,
    val isPersonal: Boolean = true
)

@Serializable
@Entity(tableName = "bank_senders")
data class BankSender(
    @PrimaryKey val senderAddress: String,
    val bankName: String
)

@Serializable
@Entity(
    tableName = "bill_items",
    foreignKeys = [
        ForeignKey(
            entity = Bill::class,
            parentColumns = ["id"],
            childColumns = ["billId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("billId")]
)
data class BillItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val billId: Long,
    val name: String,
    val price: Double,
    val quantity: Int = 1
)

@Dao
interface BillDao {
    @Query("SELECT * FROM bills ORDER BY date DESC")
    fun getAllBills(): Flow<List<Bill>>

    @Query("SELECT * FROM bills WHERE homeId = :homeId ORDER BY date DESC")
    fun getBillsByHome(homeId: Int): Flow<List<Bill>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long

    @Query("SELECT * FROM bill_items WHERE billId = :billId")
    fun getItemsForBill(billId: Long): Flow<List<BillItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<BillItem>)

    @Query("SELECT * FROM homes")
    fun getAllHomes(): Flow<List<Home>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHome(home: Home): Long

    @Query("SELECT * FROM bills WHERE category = :category")
    fun getBillsByCategory(category: String): Flow<List<Bill>>
    
    @Query("SELECT DISTINCT shopName FROM bills")
    fun getAllShops(): Flow<List<String>>

    @Query("SELECT * FROM bill_items WHERE name LIKE :query")
    fun searchProducts(query: String): Flow<List<BillItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankSender(sender: BankSender)

    @Query("SELECT * FROM bank_senders")
    fun getAllBankSenders(): Flow<List<BankSender>>

    @Query("SELECT EXISTS(SELECT 1 FROM bank_senders WHERE senderAddress = :address)")
    suspend fun isBankSender(address: String): Boolean

    @Query("SELECT * FROM bills WHERE isPersonal = 1 AND homeId IS NULL ORDER BY date DESC")
    fun getPersonalBills(): Flow<List<Bill>>
}

@Database(entities = [Home::class, Bill::class, BillItem::class, BankSender::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun billDao(): BillDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: android.content.Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "bill_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
