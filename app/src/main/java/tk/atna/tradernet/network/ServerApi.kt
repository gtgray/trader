package tk.atna.tradernet.network

import retrofit2.http.GET
import retrofit2.http.Query
import tk.atna.tradernet.network.model.TickersModel


interface ServerApi {

    @GET("api/")
    suspend fun getTopSecurities(
        @Query("q") params: String?
    ): TickersModel

}