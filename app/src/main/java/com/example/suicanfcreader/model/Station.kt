package com.example.suicanfcreader.model

import android.content.Context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.Locale

data class Station(
    var company: String = "",
    var lineName: String = "",
    var stationName: String = ""
) {
    companion object {
        private val stationCache = mutableMapOf<String, MutableMap<String, Station>>()
        private val stationCacheRaw = mutableMapOf<String, Station>()
        private var isLoaded = false
        private var translations: JSONObject? = null
        
        fun clearCache() {
            stationCache.clear()
        }

        private fun loadTranslations(context: Context) {
            if (translations != null) return
            try {
                context.assets.open("station_translations.json").use { inputStream ->
                    val size = inputStream.available()
                    val buffer = ByteArray(size)
                    inputStream.read(buffer)
                    translations = JSONObject(String(buffer))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        private fun translate(type: String, name: String, lang: String): String {
            val trans = translations ?: return name
            if (!trans.has(type)) return name
            val items = trans.getJSONObject(type)
            if (!items.has(name)) return name
            
            val localized = items.getJSONObject(name)
            return when {
                lang == "ko" && localized.has("ko") -> localized.getString("ko")
                (lang == "zh") && localized.has("zh") -> localized.getString("zh")
                lang == "en" && localized.has("en") -> localized.getString("en")
                else -> name
            }
        }

        private fun loadStations(context: Context) {
            if (isLoaded) return
            try {
                context.assets.open("StationCode.csv").use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { br ->
                        br.lineSequence().forEach { line ->
                            val tokens = line.split(",").map { it.trim() }
                            if (tokens.size >= 6) {
                                val lineCode = tokens[1]
                                val stationCode = tokens[2]
                                val key = "$lineCode-$stationCode"
                                
                                val rawCompany = tokens[3]
                                val rawLine = tokens[4]
                                val rawStation = tokens[5]

                                val rawStationObj = Station(
                                    company = rawCompany,
                                    lineName = rawLine,
                                    stationName = rawStation
                                )
                                stationCacheRaw[key] = rawStationObj
                            }
                        }
                    }
                }
                isLoaded = true
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        suspend fun getStation(context: Context, lineCode: Int, stationCode: Int): Station {
            loadStations(context)
            loadTranslations(context)
            
            val lang = Locale.getDefault().language
            val key = "$lineCode-$stationCode"
            
            // Check language-specific cache
            stationCache.getOrPut(lang) { mutableMapOf() }[key]?.let { return it }

            val rawStation = stationCacheRaw[key] ?: return Station(lineName = "-", stationName = "-", company = "-")
            
            // 1. Try JSON Translation
            var translatedStation = Station(
                company = translate("companies", rawStation.company, lang),
                lineName = translate("lines", rawStation.lineName, lang),
                stationName = translate("stations", rawStation.stationName, lang)
            )

            // 2. Fallback to ML Kit if still in Japanese and not Japanese requested
            if (lang != "ja") {
                if (translatedStation.stationName == rawStation.stationName) {
                    val query = if (rawStation.stationName.endsWith("駅")) rawStation.stationName else rawStation.stationName + "駅"
                    val translated = com.example.suicanfcreader.lib.StationTranslator.translate(query)
                    translatedStation.stationName = translated
                        .replace("역", "")
                        .replace(" Station", "", ignoreCase = true)
                        .replace(" station", "", ignoreCase = true)
                        .replace("站", "")
                        .trim()
                }
                if (translatedStation.lineName == rawStation.lineName) {
                    val query = if (rawStation.lineName.endsWith("線")) rawStation.lineName else rawStation.lineName + "線"
                    val translated = com.example.suicanfcreader.lib.StationTranslator.translate(query)
                    translatedStation.lineName = translated
                        .replace("선", "")
                        .replace(" Line", "", ignoreCase = true)
                        .replace(" line", "", ignoreCase = true)
                        .replace("线", "")
                        .trim()
                }
                if (translatedStation.company == rawStation.company) {
                    translatedStation.company = com.example.suicanfcreader.lib.StationTranslator.translate(rawStation.company)
                }
            }

            // Cache the result for this language
            stationCache[lang]!![key] = translatedStation
            return translatedStation
        }
    }
}
