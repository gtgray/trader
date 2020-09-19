package tk.atna.tradernet.internal

import androidx.lifecycle.MutableLiveData
import io.socket.client.Socket
import kotlinx.coroutines.*
import org.json.JSONArray
import tk.atna.tradernet.model.Quote
import tk.atna.tradernet.network.ApiClient
import tk.atna.tradernet.network.ServerApi
import tk.atna.tradernet.network.model.Params
import tk.atna.tradernet.network.model.QuoteModel
import tk.atna.tradernet.network.model.QuoteModelWrapped
import tk.atna.tradernet.network.model.TopSecuritiesModel
import tk.atna.tradernet.stuff.CompressHelper
import tk.atna.tradernet.stuff.Log
import java.math.BigDecimal
import kotlin.coroutines.CoroutineContext

class QuotesRepository(private val apiClient: ApiClient) {

    private val serverApi: ServerApi
    private val socket: Socket

    companion object {
        private val TICKERS_TO_WATCH: Array<String> = arrayOf(
//            "MICEXINDEXCF", "RTSI",
//            "TRNFP", "TATNP", "TATN", "SU52001RMFS3", "SU29011RMFS2", "SU26216RMFS0", "SU26215RMFS2", "SU26212RMFS9", "SU26207RMFS9", "SU25077RMFS7",
            "RSTI", "GAZP", "MRKZ", "RUAL", "HYDR", "MRKS", "SBER", "FEES", "TGKA", "VTBR",
            "ANH.US", "VICL.US", "BURG.US", "NBL.US", "YETI.US", "WSFS.US", "NIO.US", "DXC.US",
            "MIC.US", "HSBC.US", "EXPN.EU", "GSK.EU", "SHP.EU", "MAN.EU", "DB1.EU", "MUV2.EU",
            "TATE.EU", "KGF.EU", "MGGT.EU", "SGGD.EU"
        )
    }

    init {
        serverApi = apiClient.createService(ServerApi::class.java)
        socket = apiClient.launchSocket()
    }

    fun clear() {
        socket.off()
        socket.disconnect()
    }

    suspend fun pullQuotes(
        viewModelScope: CoroutineScope,
        liveQuotes: MutableLiveData<Resource<List<Quote>>>
    ) {
        supervisorScope {
            try {
                // pull tickers list
                val tickers = withContext(Dispatchers.IO) {
                    serverApi.getTopSecurities(
                        CompressHelper.toJson(
                            TopSecuritiesModel(
                                cmd = "getTopSecurities",
                                params = Params(
                                    type = "stocks",
                                    exchange = "russia",
                                    gainers = 0,
                                    limit = 30
                                )
                            )
                        )
                    )
                }
                // pull quotes
                launch {
                    doPullQuotes(viewModelScope, liveQuotes, tickers.tickers)
                }

            } catch (exception: Exception) {
                liveQuotes.postValue(Resource(ResultCode.UNKNOWN_ERROR, message = exception.message))
            }
        }
    }

    private suspend fun doPullQuotes(
        viewModelScope: CoroutineScope,
        liveQuotes: MutableLiveData<Resource<List<Quote>>>,
        tickers: List<String>
    ) {
        socket.on("q") { args ->

            Log.w("---------------- Q ${args.firstOrNull()}")

            viewModelScope.launch {
                val quotes = CompressHelper.fromJson<QuoteModelWrapped>(args[0].toString())
                liveQuotes.postValue(Resource(ResultCode.SUCCESS, convertQuotes(source = quotes)))
            }
        }
        //
        socket.on(Socket.EVENT_CONNECT) { args ->

            Log.w("---------------- CONNECT ${args.firstOrNull()}, ${socket.id()}, ${socket.connected()}")

            val json = CompressHelper.toJson(tickers)
            socket.emit("sup_updateSecurities2", JSONArray(json!!))

        }
        //
        socket.on(Socket.EVENT_CONNECT_ERROR) { args ->

            Log.w("---------------- CONNECT_ERROR ${args.firstOrNull()}")

            liveQuotes.postValue(
                Resource(
                    ResultCode.CONNECT_ERROR,
                    message = (args.firstOrNull() as Exception).message
                )
            )
        }
        //
        socket.on(Socket.EVENT_ERROR) { args ->
            Log.w("---------------- ERROR ${args.firstOrNull()}")
//            throw args.firstOrNull() as Exception
            liveQuotes.postValue(
                Resource(
                    ResultCode.UNKNOWN_ERROR,
                    message = (args.firstOrNull() as Exception).message
                )
            )
        }
        //
        socket.on(Socket.EVENT_DISCONNECT) { args ->
            Log.w("---------------- DISCONNECT ${args.firstOrNull()}")
        }
        //
        socket.connect()
    }

    private suspend fun convertQuotes(
        context: CoroutineContext = Dispatchers.Default,
        source: QuoteModelWrapped?
    ): List<Quote> {
        return withContext(context) {
            val result = toQuotes(source?.q.orEmpty())

            Log.w("---------------- CONVERT QUOTES $result")

            return@withContext result
        }
    }

    private fun toQuotes(source: List<QuoteModel>): List<Quote> {
        return source.map { item ->
            Quote(
                id = item.c,
                tickerUrl = apiClient.buildTickerUrl(item.c),
                rialto = item.ltr ?: "",
                name = item.name,
                latinName = item.name2,
                price = roundPrice(item.ltp, item.min_step).toString(),
                points = roundPrice(item.chg, item.min_step).toString(),
                percents = item.pcp,
                label = discoverLabel(item.ltc ?: "")
            )
        }
    }

    private fun roundPrice(price: Double, step: Double): BigDecimal {
        val bigPrice = price.toBigDecimal()
        val bigStep = step.toBigDecimal()
        val tail = bigPrice.rem(bigStep)
        return bigPrice - tail +
                if (tail >= bigStep.div(BigDecimal(2))) bigStep else BigDecimal.ZERO
    }

    private fun discoverLabel(label: String): Boolean? {
        return when {
            label.contains('U') -> true
            label.contains('D') -> false
            else -> null
        }
    }

}
