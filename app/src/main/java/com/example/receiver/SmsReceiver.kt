package com.example.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.util.Log
import com.example.data.AppDatabase
import com.example.data.Bill
import com.example.data.BillRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (message in messages) {
                val sender = message.displayOriginatingAddress
                val body = message.displayMessageBody
                
                checkAndProcessBankSms(context, sender, body)
            }
        }
    }

    private fun checkAndProcessBankSms(context: Context, sender: String, body: String) {
        val database = AppDatabase.getDatabase(context)
        val repository = BillRepository(database.billDao())
        
        CoroutineScope(Dispatchers.IO).launch {
            if (repository.isBankSender(sender)) {
                val amount = extractAmount(body)
                if (amount != null) {
                    val bill = Bill(
                        name = "Bank Transaction",
                        totalAmount = amount,
                        category = "Finance",
                        shopName = sender,
                        isPersonal = true
                    )
                    repository.saveBillWithItems(bill, emptyList())
                    Log.d("SmsReceiver", "Saved bank transaction: $amount from $sender")
                }
            }
        }
    }

    private fun extractAmount(body: String): Double? {
        // Simple regex to find amount like $123.45 or 123.45 USD or similar
        // Pattern matches: $ followed by numbers, or numbers followed by currency
        val patterns = listOf(
            Pattern.compile("[$£€]\\s?(\\d+[.,]\\d{2})"),
            Pattern.compile("(\\d+[.,]\\d{2})\\s?(USD|EUR|GBP|AED|INR|Transaction)")
        )
        
        for (pattern in patterns) {
            val matcher = pattern.matcher(body)
            if (matcher.find()) {
                return matcher.group(1)?.replace(",", ".")?.toDoubleOrNull()
            }
        }
        return null
    }
}
