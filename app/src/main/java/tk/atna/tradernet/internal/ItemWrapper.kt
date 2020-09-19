package tk.atna.tradernet.internal

open class ItemWrapper<T>(
    val viewType: T,
    val id: String,
    val item: Any?
) {

    fun itemEquals(item: ItemWrapper<T>?): Boolean =
        this.id == item?.id
                && this.viewType == item.viewType

    open fun contentEquals(item: ItemWrapper<T>?): Boolean =
        this.item == item?.item

}
