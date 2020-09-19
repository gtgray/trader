package tk.atna.tradernet.model

data class Quote(
    val id: String,
    val tickerUrl: String?,
    val rialto: String,
    val name: String,
    val latinName: String,
    val price: String,
    val points: String,
    val percents: Double,
    val label: Boolean?
)
