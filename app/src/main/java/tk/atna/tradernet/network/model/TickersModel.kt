package tk.atna.tradernet.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TickersModel(
    val tickers: List<String>
)
