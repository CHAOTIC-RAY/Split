package com.example.data

import kotlinx.coroutines.flow.Flow

class BillRepository(private val billDao: BillDao) {
    val allBills: Flow<List<Bill>> = billDao.getAllBills()
    val allHomes: Flow<List<Home>> = billDao.getAllHomes()

    suspend fun saveBillWithItems(bill: Bill, items: List<BillItem>) {
        val billId = billDao.insertBill(bill)
        val itemsWithId = items.map { it.copy(billId = billId) }
        billDao.insertItems(itemsWithId)
    }

    fun getItemsForBill(billId: Long) = billDao.getItemsForBill(billId)
    fun getBillsByHome(homeId: Int) = billDao.getBillsByHome(homeId)
    
    suspend fun createHome(name: String) = billDao.insertHome(Home(name = name))

    fun searchProducts(query: String) = billDao.searchProducts("%$query%")

    suspend fun addBankSender(address: String, name: String) {
        billDao.insertBankSender(BankSender(address, name))
    }

    fun getPersonalBills() = billDao.getPersonalBills()

    fun getAllBankSenders() = billDao.getAllBankSenders()

    suspend fun isBankSender(address: String) = billDao.isBankSender(address)
}
