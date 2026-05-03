package com.example.suicanfcreader.lib

import com.example.suicanfcreader.lib.StationTranslator.getTargetLanguageCode
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import java.util.Locale

object StationTranslator {
    private var translator: Translator? = null
    private var currentTargetLang: String = ""

    fun getTargetLanguageCode(): String {
        val lang = Locale.getDefault().language
        return when (lang) {
            "ko" -> TranslateLanguage.KOREAN
            "zh" -> TranslateLanguage.CHINESE
            "en" -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }
    }

    suspend fun isModelDownloaded(): Boolean {
        val lang = Locale.getDefault().language
        if (lang == "ja") return true
        
        val modelManager = RemoteModelManager.getInstance()
        val japaneseModel = TranslateRemoteModel.Builder(TranslateLanguage.JAPANESE).build()
        val targetModel = TranslateRemoteModel.Builder(getTargetLanguageCode()).build()
        
        val isJapaneseDownloaded = modelManager.isModelDownloaded(japaneseModel).await()
        val isTargetDownloaded = modelManager.isModelDownloaded(targetModel).await()
        
        return isJapaneseDownloaded && isTargetDownloaded
    }

    suspend fun downloadModel(): Boolean = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) {
        val lang = Locale.getDefault().language
        if (lang == "ja") return@withContext true

        val modelManager = RemoteModelManager.getInstance()
        val targetCode = getTargetLanguageCode()
        
        val japaneseModel = TranslateRemoteModel.Builder(TranslateLanguage.JAPANESE).build()
        val targetModel = TranslateRemoteModel.Builder(targetCode).build()
        
        val conditions = DownloadConditions.Builder()
            .build()
        
        try {
            // Download Japanese first (required as source)
            modelManager.download(japaneseModel, conditions).await()
            // Then target language
            modelManager.download(targetModel, conditions).await()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun translate(text: String): String {
        val lang = Locale.getDefault().language
        if (lang == "ja") return text 
        
        val targetLangCode = getTargetLanguageCode()

        if (translator == null || currentTargetLang != targetLangCode) {
            translator?.close()
            currentTargetLang = targetLangCode
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.JAPANESE)
                .setTargetLanguage(targetLangCode)
                .build()
            translator = Translation.getClient(options)
        }

        try {
            return translator?.translate(text)?.await() ?: text
        } catch (e: Exception) {
            return text
        }
    }
}
