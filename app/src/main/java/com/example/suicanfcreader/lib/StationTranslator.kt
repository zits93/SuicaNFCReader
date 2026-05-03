package com.example.suicanfcreader.lib

import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.Translator
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.tasks.await
import java.util.Locale

object StationTranslator {
    private var translator: Translator? = null
    private var currentTargetLang: String = ""

    suspend fun translate(text: String): String {
        val lang = Locale.getDefault().language
        if (lang == "ja") return text // No translation needed for Japanese
        
        val targetLangCode = when (lang) {
            "ko" -> TranslateLanguage.KOREAN
            "zh" -> TranslateLanguage.CHINESE
            "en" -> TranslateLanguage.ENGLISH
            else -> TranslateLanguage.ENGLISH
        }

        if (translator == null || currentTargetLang != targetLangCode) {
            translator?.close()
            currentTargetLang = targetLangCode
            val options = TranslatorOptions.Builder()
                .setSourceLanguage(TranslateLanguage.JAPANESE)
                .setTargetLanguage(targetLangCode)
                .build()
            translator = Translation.getClient(options)
        }

        val conditions = DownloadConditions.Builder().build()
        try {
            // Ensure model is downloaded
            translator?.downloadModelIfNeeded(conditions)?.await()
            return translator?.translate(text)?.await() ?: text
        } catch (e: Exception) {
            e.printStackTrace()
            return text
        }
    }
}
