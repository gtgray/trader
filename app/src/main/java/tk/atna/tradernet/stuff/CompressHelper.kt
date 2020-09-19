package tk.atna.tradernet.stuff

import com.squareup.moshi.Moshi
import java.io.IOException
import java.lang.reflect.Type

object CompressHelper {

    val MOSHI: Moshi = createMoshi()


    inline fun <reified T> toJson(source: T): String? {
        try {
            return MOSHI.adapter(T::class.java).toJson(source)
        //
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //
        return null
    }

    inline fun <reified T> fromJson(json: String?): T? {
        json?.let {
            try {
                return MOSHI.adapter(T::class.java).fromJson(json)
            //
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //
        return null
    }

    inline fun <reified T> fromJson(json: String?, type: Type): T? {
        json?.let {
            try {
                return MOSHI.adapter<T?>(type).fromJson(json)
                //
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        //
        return null
    }

    fun moshi() = MOSHI

    private fun createMoshi() =
        Moshi.Builder()
//            .add(KotlinJsonAdapterFactory()) // to use enable 'other.moshi_reflect' in build.gradle
            .build()

}
