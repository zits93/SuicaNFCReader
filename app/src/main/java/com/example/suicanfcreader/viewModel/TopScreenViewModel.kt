package com.example.suicanfcreader.viewModel

import android.app.Activity
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.content.IntentCompat
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcF
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.suicanfcreader.lib.SuicaReader
import com.example.suicanfcreader.model.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class TopScreenViewModel(
    context: Context
) : ViewModel() {

    private val nfcAdapter: NfcAdapter? = NfcAdapter.getDefaultAdapter(context)
    private val _nfcCards = MutableLiveData<List<Card>>()
    val nfcCards: LiveData<List<Card>> get() = _nfcCards
    private val _showNoNfcDialog = MutableLiveData<Boolean>()
    private val _isDataRefreshed = MutableLiveData<Boolean>()
    val isDataRefreshed: LiveData<Boolean> = _isDataRefreshed
    
    private var lastRawData: ByteArray? = null

    fun refreshTranslations(context: Context) {
        lastRawData?.let { data ->
            viewModelScope.launch {
                val cards = fromData(data, context)
                _nfcCards.postValue(cards)
            }
        }
    }
    fun enableNfcForegroundDispatch(activity: Activity) {
        nfcAdapter?.let { adapter ->
            if (adapter.isEnabled) {
                val flags = NfcAdapter.FLAG_READER_NFC_F or NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK
                adapter.enableReaderMode(activity, { tag ->
                    handleTag(tag, activity)
                }, flags, null)
            } else {
                _showNoNfcDialog.postValue(true)
            }
        }
    }

    fun disableNfcForegroundDispatch(activity: Activity) {
        nfcAdapter?.disableReaderMode(activity)
    }

    fun handleTag(tag: Tag, context: Context) {
        viewModelScope.launch {
            val cards = readTagData(tag, context)
            _nfcCards.postValue(cards)
            _isDataRefreshed.postValue(true)
        }
    }

    private suspend fun readTagData(tag: Tag,context: Context): List<Card> = withContext(Dispatchers.IO) {
        val id = tag.id
        try {
            val felica = NfcF.get(tag)
            felica.connect()
            val req = SuicaReader.readWithoutEncryption(id, 10)
            val res: ByteArray = felica.transceive(req)
            felica.close()
            lastRawData = res
            return@withContext fromData(res, context)
        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext emptyList()
        }
    }


    private suspend fun fromData(data: ByteArray, context: Context): List<Card> {
        val size: Int = data[12].toInt()
        val cards = mutableListOf<Card>()
        for (i in 0 until size) {
            val felica = SuicaReader.parse(data, 13 + i * 16)
            val card: Card = Card.getCard(context, felica)
            if (i < size - 1) {
                cards.add(card)
            }
        }
        return cards
    }
}



class TopScreenViewModelFactory(
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TopScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TopScreenViewModel(context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
