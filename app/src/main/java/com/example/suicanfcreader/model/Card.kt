package com.example.suicanfcreader.model

import android.content.Context
import com.example.suicanfcreader.lib.SuicaReader

data class Card(
    var date: String? = null,
    var number: String? = null,
    var payment: String? = null,
    var kindResId: Int = 0,
    var deviceResId: Int = 0,
    var actionResId: Int = 0,
    var inLine: String? = null,
    var inStation: String? = null,
    var outLine: String? = null,
    var outStation: String? = null,
    var balance: String? = null,
    var inCompany: String? = null,
    var outCompany: String? = null,
    // Store raw values for date parts to format in UI if needed
    var year: Int = 0,
    var month: Int = 0,
    var day: Int = 0
) {
    companion object {
        fun getCard(context: Context?, felica: SuicaReader): Card {
            val card = Card().apply {
                year = 2000 + felica.year
                month = felica.month
                day = felica.day
                // Date will be formatted in UI using resources
                
                number = felica.seqNo.toString()
                payment = ""
                kindResId = felica.kindResId
                deviceResId = felica.deviceResId
                actionResId = felica.actionResId

                // Retrieve station details safely
                val inStationDetails = context?.let {
                    Station.getStation(it, felica.inLine, felica.inStation)
                }
                inLine = inStationDetails?.lineName ?: "-"
                inStation = inStationDetails?.stationName ?: "-"
                inCompany = inStationDetails?.company ?: "-"

                val outStationDetails = context?.let {
                    Station.getStation(it, felica.outLine, felica.outStation)
                }
                outLine = outStationDetails?.lineName ?: "-"
                outStation = outStationDetails?.stationName ?: "-"
                outCompany = outStationDetails?.company ?: "-"

                balance = felica.remain.toString()
            }
            return card
        }
    }
}
