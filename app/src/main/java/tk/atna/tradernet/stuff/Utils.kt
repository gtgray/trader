package tk.atna.tradernet.stuff

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DimenRes
import java.util.*


@ColorInt
fun getColor(context: Context?, @ColorRes colorRes: Int): Int {
    if (context != null
        && colorRes != 0) {
        return context.resources.getColor(colorRes, context.theme)
    }
    //
    return -1 // -1 is white color
}

fun getColorStateList(context: Context?, @ColorRes colorRes: Int): ColorStateList? {
    if (context != null
        && colorRes != 0) {
        return context.getColorStateList(colorRes)
    }
    //
    return null
}
