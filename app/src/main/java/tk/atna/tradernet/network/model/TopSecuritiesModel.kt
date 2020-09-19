package tk.atna.tradernet.network.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TopSecuritiesModel(
    val cmd: String,
    val params: Params
)

@JsonClass(generateAdapter = true)
data class Params(
    val type: String, //'stocks', 'bonds', 'futures', 'funds', 'indexes'
    val exchange: String, //'russia', 'kazakhstan', 'europe', 'usa', 'ukraine', 'currencies'
    val gainers: Int, // 1 to get gainers, 0 to get most traded
    val limit: Int // up to 100
)