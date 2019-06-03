package ir.puyaars.advisor

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import ir.puyaars.advisor.api.auth.AuthProvider

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var handler: Handler
    private val runner = Runnable(this::navigate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_splash)

        handler = Handler()
        handler.postDelayed(runner,3000)
    }

    override fun onDestroy() {
        handler.removeCallbacks(runner)
        super.onDestroy()
    }

    private fun navigate() {
        val authProvider  = AuthProvider.getInstance(this)
//        authProvider.deleteToken()

        val loggedIn = authProvider.hasToken()
        Log.d( Constants.TAG,"user is logged in")
        val i = Intent(this,
            if(loggedIn) MainActivity::class.java else LoginActivity::class.java
        )
        startActivity(i)
    }
}
