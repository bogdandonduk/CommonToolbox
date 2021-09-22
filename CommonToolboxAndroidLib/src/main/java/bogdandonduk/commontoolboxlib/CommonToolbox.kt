package bogdandonduk.commontoolboxlib

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.inputmethod.InputMethodManager
import androidx.annotation.ColorInt
import androidx.annotation.RequiresApi
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.ColorUtils
import bogdandonduk.kotlinxcoroutineswrappersandroidlibrary.ScopesAndJobs
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

object CommonToolbox {
    @PublishedApi
    internal var currentActivityClass: Class<out Activity>? = null

    internal var systemModalShown = false

    fun executeForVersion(apiLevel: Int, lowerAction: () -> Unit, equalOrHigherAction: () -> Unit) {
        if(Build.VERSION.SDK_INT >= apiLevel)
            equalOrHigherAction.invoke()
        else
            lowerAction.invoke()
    }

    fun getAppSharedPreferences(context: Context) =
        context.getSharedPreferences(context.packageName + SharedPrefsKeysExtensionVocabulary.SHARED_PREFERENCES_SUFFIX, Context.MODE_PRIVATE)

    fun getRippleColorByLuminance(context: Context, @ColorInt backgroundColor: Int) =
        ResourcesCompat.getColor(context.resources, if(ColorUtils.calculateLuminance(backgroundColor) >= 0.5) R.color.ripple_dark else R.color.ripple_light, null)

    fun getRippleColorByLuminanceWeak(context: Context, @ColorInt backgroundColor: Int) =
        ResourcesCompat.getColor(context.resources, if(ColorUtils.calculateLuminance(backgroundColor) >= 0.5) R.color.ripple_dark_weak else R.color.ripple_light_weak, null)

    fun applyColorFilter(drawable: Drawable, resolvedColor: Int) {
        executeForVersion(Build.VERSION_CODES.Q, {
            drawable.setColorFilter(resolvedColor, PorterDuff.Mode.SRC_ATOP)
        }) {
            @SuppressLint("NewApi")
            drawable.colorFilter = BlendModeColorFilter(resolvedColor, BlendMode.SRC_ATOP)
        }
    }

    inline fun openActivity(context: Context, targetActivityClass: Class<out Activity>, extras: Bundle? = null, options: Bundle? = null, beforeAction: () -> Unit = {}, afterAction: () -> Unit = {}) {
        if(currentActivityClass != targetActivityClass && context::class.java != targetActivityClass) {
            beforeAction.invoke()

            context.startActivity(Intent(context, targetActivityClass).apply {
                if(extras != null) putExtras(extras)

                if(context !is Activity) flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, options)

            afterAction.invoke()
        }
    }

    inline fun openActivity(context: Context, targetActivityClass: Class<out Activity>, dataString: String?, options: Bundle? = null, beforeAction: () -> Unit = {}, afterAction: () -> Unit = {}) {
        if(currentActivityClass != targetActivityClass && context::class.java != targetActivityClass) {
            beforeAction.invoke()

            context.startActivity(Intent(context, targetActivityClass).apply {
                if(dataString != null) data = Uri.parse(dataString)

                if(context !is Activity) flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, options)

            afterAction.invoke()
        }
    }

    fun registerCurrentActivity(activity: Activity) {
        currentActivityClass = activity::class.java
    }

    fun unregisterCurrentActivity() {
        currentActivityClass = null
    }

    fun vibrateOneShot(context: Context, duration: Long = 50) {
        executeForVersion(Build.VERSION_CODES.O, {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(50)
        }) {
            @Suppress("NewApi")
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun vibrateOneShot(context: Context, duration: Long, amplitude: Int = VibrationEffect.DEFAULT_AMPLITUDE) {
        executeForVersion(Build.VERSION_CODES.O, {
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(50)
        }) {
            @Suppress("NewApi")
            (context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(VibrationEffect.createOneShot(duration, amplitude))
        }
    }

    fun rotateViewOnZ(view: View, half: Boolean, duration: Long = 300) {
        view.animate().setDuration(duration).rotationXBy(if(half) 0.5f else 1f).start()
    }

    fun openGooglePlayListing(appContext: Context) {
        val appId = appContext.packageName

        Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appId")).run {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

            appContext.packageManager.queryIntentActivities(this, 0).forEach {
                if(it.activityInfo.applicationInfo.packageName == "com.android.vending") {
                    it.activityInfo.let { activityInfo ->
                        component = ComponentName(activityInfo.packageName, activityInfo.name)

                        appContext.startActivity(this@run)
                    }

                    return
                }
            }

            data = Uri.parse("https://play.google.com/store/apps/details?id=$appId")

            appContext.startActivity(this)
        }
    }

    @JvmOverloads
    fun sendEmail(context: Context, chooserModalTitle: String, subject: String? = null, message: String? = null, vararg addresses: String?) {
        if(!systemModalShown) {
            systemModalShown = true

            context.startActivity(
                Intent.createChooser(
                    Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:")

                        putExtra(Intent.EXTRA_EMAIL, addresses)
                        putExtra(Intent.EXTRA_SUBJECT, subject)
                        putExtra(Intent.EXTRA_TEXT, message)
                    }, chooserModalTitle
                )
            )
        }
    }

    fun doActivityOnResumeConfigurations() {
        systemModalShown = false
    }

    fun getDateTime(millis: Long) : String {
        val date = Date(millis * 1000)

        return "${SimpleDateFormat.getDateInstance().format(date)}, ${SimpleDateFormat.getTimeInstance().format(date)}"
    }

    fun appendGlideConfiguration(requestBuilder: RequestBuilder<*>) =
        requestBuilder
            .placeholder(R.drawable.glide_placeholder)
            .error(R.drawable.glide_placeholder_error)
            .thumbnail(0.5f)
            .priority(Priority.HIGH)
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)

    fun setOnTouchListeners(onTouchAction: (view: View, event: MotionEvent) -> Unit, vararg views: View?) {
        views.forEach {
            it?.setOnTouchListener { view, event ->
                onTouchAction.invoke(view, event)

                false
            }
        }
    }

    fun getWeakerColor(@ColorInt color: Int) =
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val colorValue = Color.valueOf(color)

            Color.argb(88, colorValue.red().toInt(), colorValue.green().toInt(), colorValue.blue().toInt())
        } else color

    suspend fun <T> manageAsynchronouslyInitializedObject(initializationAction: () -> T, managementAction: (obj: T) -> Unit) : T {
        val initializedObj = initializationAction.invoke()

        managementAction.invoke(initializedObj)

        return initializedObj
    }

    fun detectInputMethodShown(context: Context) =
        (InputMethodManager::class.java.getMethod("getInputMethodWindowVisibleHeight")
            .invoke(context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager) as Int) > 0

    fun hideSystemUI(window: Window) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                and View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                and View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                and View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                and View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                and View.SYSTEM_UI_FLAG_FULLSCREEN)
    }

    fun showSystemUI(window: Window) {
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
    }

    /** BUGGY */
    fun applyPulsationEffectToViewWhileVisible(view: View, speed: Long = 800) {
        ScopesAndJobs.getMainScope().launch {
            while(view.visibility == View.VISIBLE) {
                if(view.alpha == 1f) {
                    view.animate().alpha(0.7f).setInterpolator(AccelerateDecelerateInterpolator()).setDuration(300).start()
                    delay(1300)
                } else {
                    view.animate().alpha(1f).setInterpolator(AccelerateDecelerateInterpolator()).setDuration(300).start()
                    delay(1300)
                }
            }
        }
    }
}












