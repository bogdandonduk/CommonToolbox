package bogdandonduk.commontoolboxlib

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import java.lang.ref.WeakReference

class GlideWrapper private constructor(context: Context) {
    var contextWR = WeakReference(context)

    companion object {
        private var singleton: GlideWrapper? = null

        fun getSingleton(context: Context, override: Boolean = false) = if(override || singleton == null) {
            singleton = GlideWrapper(context)

            singleton!!
        } else
            singleton!!
    }

    var bitmapRequestBuilder = Glide.with(contextWR.get()!!)
        .asBitmap()
        .placeholder(R.drawable.glide_placeholder)
        .error(R.drawable.glide_placeholder_error)
        .thumbnail(0.5f)
        .priority(Priority.HIGH)

    var drawableRequestBuilder = Glide.with(contextWR.get()!!)
        .asDrawable()
        .placeholder(R.drawable.glide_placeholder)
        .error(R.drawable.glide_placeholder_error)
        .thumbnail(0.5f)
        .priority(Priority.HIGH)

    var fileRequestBuilder = Glide.with(contextWR.get()!!)
        .asFile()
        .placeholder(R.drawable.glide_placeholder)
        .error(R.drawable.glide_placeholder_error)
        .thumbnail(0.5f)
        .priority(Priority.HIGH)

    var gifRequestBuilder = Glide.with(contextWR.get()!!)
        .asGif()
        .placeholder(R.drawable.glide_placeholder)
        .error(R.drawable.glide_placeholder_error)
        .thumbnail(0.5f)
        .priority(Priority.HIGH)
}
