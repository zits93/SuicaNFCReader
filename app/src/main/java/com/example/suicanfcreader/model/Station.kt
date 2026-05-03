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
        private val stationCache = mutableMapOf<String, Station>()
        private val stationCacheRaw = mutableMapOf<String, Station>()
        private var isLoaded = false
        private var translations: JSONObject? = null

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

        private fun translate(type: String, name: String): String {
            val trans = translations ?: return name
            if (!trans.has(type)) return name
            val items = trans.getJSONObject(type)
            if (!items.has(name)) return name
            
            val localized = items.getJSONObject(name)
            val lang = Locale.getDefault().language
            return when {
                lang == "ko" && localized.has("ko") -> localized.getString("ko")
                (lang == "zh") && localized.has("zh") -> localized.getString("zh")
                lang == "en" && localized.has("en") -> localized.getString("en")
                else -> name
            }
        }

        private fun loadStations(context: Context) {
            if (isLoaded) return
            loadTranslations(context)
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

                                val station = Station(
                                    company = translate("companies", rawCompany),
                                    lineName = translate("lines", rawLine),
                                    stationName = translate("stations", rawStation)
                                )
                                stationCache[key] = station
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
            val key = "$lineCode-$stationCode"
            val station = stationCache[key] ?: Station(lineName = "-", stationName = "-", company = "-")
            
            // If the name is still the same as the raw one (not in JSON), try ML Kit translation
            val lang = Locale.getDefault().language
            if (lang != "ja" && station.stationName != "-") {
                val rawStation = stationCacheRaw[key]
                if (rawStation != null) {
                    if (station.stationName == rawStation.stationName) {
                        val query = if (station.stationName.endsWith("駅")) station.stationName else station.stationName + "駅"
                        val translated = com.example.suicanfcreader.lib.StationTranslator.translate(query)
                        station.stationName = translated
                            .replace("역", "")
                            .replace(" Station", "", ignoreCase = true)
                            .replace(" station", "", ignoreCase = true)
                            .replace("站", "")
                            .trim()
                    }
                    if (station.lineName == rawStation.lineName) {
                        val query = if (station.lineName.endsWith("線")) station.lineName else station.lineName + "線"
                        val translated = com.example.suicanfcreader.lib.StationTranslator.translate(query)
                        station.lineName = translated
                            .replace("선", "")
                            .replace(" Line", "", ignoreCase = true)
                            .replace(" line", "", ignoreCase = true)
                            .replace("线", "")
                            .trim()
                    }
                    if (station.company == rawStation.company) {
                        station.company = com.example.suicanfcreader.lib.StationTranslator.translate(station.company)
                    }
                }
            }
            return station
        }
    }
}
