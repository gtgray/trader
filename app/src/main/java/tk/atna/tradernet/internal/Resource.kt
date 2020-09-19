package tk.atna.tradernet.internal

open class Resource<T>(
    val resultCode: ResultCode,
    val data: T? = null,
    val message: String? = null
)

