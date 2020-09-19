package tk.atna.tradernet.stuff

import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeFactory
import tk.atna.tradernet.R


fun placeImage(view : ImageView?, url : String?) {
    view?.let {
        val context = view.context
        Glide.with(context)
            .load(if (url != null) Uri.parse(url) else url)
            .transition(
                DrawableTransitionOptions.withCrossFade(
                    DrawableCrossFadeFactory.Builder().setCrossFadeEnabled(true).build()
                )
            )
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .placeholder(ColorDrawable(getColor(context, R.color.clr_transparent)))
            .fallback(ColorDrawable(getColor(context, R.color.clr_accent)))
            .into(view)
    }
}

fun clearTarget(view: ImageView?) {
    view?.let {
        Glide.with(view.context)
            .clear(view)
    }
}
