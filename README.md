
# CommonToolbox

  Android library that contains various utility methods and extensions wrapping common actions.
  
## Include in your project  
**Gradle dependency**  
  
Add this in your **app**-level **build.gradle** file:  
```groovy
dependencies {  
	...  
  
	def latest_version_tag = 2.1.1
	implementation "com.github.bogdandonduk:CommonToolbox:$latest_version_tag"  
  
	...  
}  
```  
You can always find the **latest_version_tag** [here](https://github.com/bogdandonduk/CommonToolbox/releases).  
  
Also make sure you have this repository in your **project**-level **build.gradle** file:  
```groovy  
allprojects {  
	repositories {  
		...  
  
		maven { url 'https://jitpack.io' }  
	}  
}  
```  

# Examples of usage
```kotlin 
// the main object is CommonToolbox
// it can help you perform many common operations
// for instance, it can open activity
val extras: Bundle = bundleOf("isJavaScriptEnabled" to true)

CommonToolbox.openActivity(context, LicenseActivity::class.java, dataString = "https://mysite.com/license", extras = extras, afterAction = {
	Toast.makeText(context, "Hang on, the license will show up now", Toast.LENGTH_SHORT).show()
})

// or open Google Play listing of your app
CommonToolbox.openGooglePlayListing(applicationContext)

// send e-mail intent configuring Android's Chooser modal and fixing some common bugs, e.g. making sure the Chooser is not shown twice
CommonToolbox.sendEmail(context, chooserModalTitle = "E-mail us!", message = "I am a user of your Android app, and I'll tell you what ...", adresses = arrayOf("myproject.support@gmail.com"))

// apply color filter to any drawable
CommonToolbox.applyColorFilter(findViewById<ImageView>(R.id.logo_image_view).drawable, Color.CYAN)

// vibrate
CommonToolbox.vibrateOneShot(context, duration = 100L, amplitude = VibrationEffect.DEFAULT_AMPLITUDE)

// detect if the keyboard is currently shown
val isKeyboardShown: Boolean = CommonToolbox.detectInputMethodShown(context)

// show and hide system UI (status bar, navigation bar)
CommonToolbox.showSystemUI(window = window)
CommonToolbox.hideSystemUI(window = window)

// and other useful things. Check it out!
```
