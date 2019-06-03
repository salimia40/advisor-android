package ir.puyaars.advisor

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import ir.puyaars.advisor.api.auth.AuthProvider
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val authProvider = AuthProvider.getInstance(this)
        authProvider.loginResponse.observe(this, Observer {
            it.apply {
                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                if (success) {
                    val i = Intent(this@LoginActivity, MainActivity::class.java)
                    startActivity(i)
                } else {
                    passwordField.text.clear()
                }
            }
        })

        loginButton.setOnClickListener {
            val username = usernameField.text.toString()
            val password = passwordField.text.toString()
            val saveCreds: Boolean = checkBox.isChecked

            authProvider.login(username, password, saveCreds)
        }
    }

}
