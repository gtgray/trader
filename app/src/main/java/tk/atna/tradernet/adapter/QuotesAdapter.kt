package tk.atna.tradernet.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import tk.atna.tradernet.R
import tk.atna.tradernet.databinding.ItemQuoteItemBinding
import tk.atna.tradernet.databinding.ItemQuotesEmptyBinding
import tk.atna.tradernet.internal.DiffCallback
import tk.atna.tradernet.internal.ItemWrapper
import tk.atna.tradernet.model.Quote
import tk.atna.tradernet.stuff.getColor
import tk.atna.tradernet.stuff.getColorStateList
import tk.atna.tradernet.stuff.placeImage

class QuotesAdapter private constructor(
    private val inflater: LayoutInflater,
    private val itemActionListener: ItemActionListener
) :
    ListAdapter<QuotesAdapter.QuoteItemWrapper, QuotesAdapter.ItemViewHolder<ViewBinding>>(
        DiffCallback<QuoteItemWrapper, ItemViewType>()
    ) {

    private val internalActionListener: InternalActionListener

    constructor(
        context: Context,
        itemActionListener: ItemActionListener
    ) : this(LayoutInflater.from(context), itemActionListener)

    init {
        this.internalActionListener = object : InternalActionListener {

            override fun onItemClick(item: Quote) {
                itemActionListener.onItemClick(item)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position)?.viewType?.ordinal ?: -1
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ItemViewHolder<ViewBinding> {
        return when (ItemViewType.values()[viewType]) {
            ItemViewType.EMPTY -> EmptyItemViewHolder.create(inflater, parent)
            ItemViewType.ITEM -> QuoteItemViewHolder.create(inflater, parent, internalActionListener)
            else -> throw IllegalArgumentException("Unknown item view type")
        }
    }

    override fun onBindViewHolder(holder: ItemViewHolder<ViewBinding>, position: Int) {
        val item = getItem(position)
        holder.onBindView(item!!)
    }

    override fun getItem(position: Int): QuoteItemWrapper? {
        return if (position in 0 until itemCount)
            super.getItem(position)
        else
            null
    }

    fun setData(data: List<Quote>?) {
        val wrapped = if (!data.isNullOrEmpty()) {
            data.mapIndexed { index, item ->
                QuoteItemWrapper(
                    viewType = ItemViewType.ITEM,
                    id = item.name,
                    item = item,
                    withDivider = index > 0
                )
            }
        // otherwise, show empty view
        } else
            arrayListOf(
                QuoteItemWrapper(
                    viewType = ItemViewType.EMPTY,
                    id = ItemViewType.EMPTY.toString()
                )
            )
        //
        submitList(wrapped)
    }


    interface ItemActionListener {

        fun onItemClick(item: Quote)

    }


    interface InternalActionListener {

        fun onItemClick(item: Quote)

    }


    class QuoteItemWrapper(
        viewType: ItemViewType,
        id: String,
        item: Any? = null,
        val withDivider: Boolean = false
    ) : ItemWrapper<ItemViewType>(viewType, id, item) {
        // nothing here
    }


    enum class ItemViewType {

        EMPTY,
        ITEM,
        ;
    }


    abstract class ItemViewHolder<out T>(
        val binding: T,
        var internalActionListener: InternalActionListener? = null
    ) : RecyclerView.ViewHolder(binding.root) where T : ViewBinding {

        open fun onBindView(wrapper: QuoteItemWrapper) {
            // override to use
        }

    }


    // R.layout.item_quotes_empty
    class EmptyItemViewHolder(binding: ItemQuotesEmptyBinding) :
        ItemViewHolder<ItemQuotesEmptyBinding>(binding) {

        companion object {
            fun create(inflater: LayoutInflater, parent: ViewGroup): EmptyItemViewHolder {
                return EmptyItemViewHolder(ItemQuotesEmptyBinding.inflate(inflater, parent, false))
            }
        }

    }


    // R.layout.item_quote_item
    class QuoteItemViewHolder(binding: ItemQuoteItemBinding) :
        ItemViewHolder<ItemQuoteItemBinding>(binding) {

        companion object {
            fun create(
                inflater: LayoutInflater,
                parent: ViewGroup,
                listener: InternalActionListener
            ): QuoteItemViewHolder {
                return QuoteItemViewHolder(ItemQuoteItemBinding.inflate(inflater, parent, false))
                    .apply { internalActionListener = listener }
            }
        }

        override fun onBindView(wrapper: QuoteItemWrapper) {
            val item = wrapper.item as Quote
            with(binding) {
                divider.isVisible = wrapper.withDivider
                placeImage(ivTicker, item.tickerUrl)
                tvId.text = item.id
                tvRialto.text = item.rialto
                tvName.text = item.latinName
                tvPrice.text = item.price.toString()
                tvPoints.text = item.points.toString()
                placePercents(tvPercents, item.percents, item.label)
            }
            // on item click
            itemView.setOnClickListener { v -> internalActionListener?.onItemClick(item) }
        }

        private fun placePercents(view: TextView, percents: Double, label: Boolean?) {
            var textColor = R.color.clr_malachite
            var tintColor = R.color.clr_transparent
            when {
                // need to fill percents background
                (label != null) -> {
                    textColor = R.color.clr_white
                    tintColor = if (label) R.color.clr_malachite else R.color.clr_torch_red
                }
                // just paint percents text
                (percents < 0) -> textColor = R.color.clr_torch_red
            }
            // apply
            view.text = percents.toString()
            view.setTextColor(getColor(view.context, textColor))
            view.backgroundTintList = getColorStateList(view.context, tintColor)
        }

    }


}
