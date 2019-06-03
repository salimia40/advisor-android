package ir.puyaars.advisor.api.auth

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ir.puyaars.advisor.Constants
import ir.puyaars.advisor.Constants.PASSWORD_KEY
import ir.puyaars.advisor.Constants.PREFERENCES_NAME
import ir.puyaars.advisor.Constants.TOKEN_KEY
import ir.puyaars.advisor.Constants.USERNAME_KEY
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors

data class LoginResult(val success: Boolean,val message: String)

class AuthProvider(context: Context) {

    private val executor : Executor = Executors.newSingleThreadExecutor()
    private var sharedPreferences : SharedPreferences = context.getSharedPreferences(PREFERENCES_NAME,0)

    private var api : AuthApi
    var loginResponse : MutableLiveData<LoginResult> = MutableLiveData()

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(Constants.SERVER_ADRESS)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(AuthApi::class.java)

    }


    fun getToken(): String? {
        val contains = sharedPreferences.contains(TOKEN_KEY)
        return if(!contains) null else sharedPreferences.getString( TOKEN_KEY,null)
    }

    fun hasToken(): Boolean {
        return sharedPreferences.contains(TOKEN_KEY)
    }

    fun login(username:String,password:String,saveCredetials: Boolean
//              cb: (success:Boolean,message:String) -> Unit
    ){
        val call = api.login(username ,password)
        executor.execute {
            val res : Response<LoginResponse> = call.execute()
            println(res)
            when (res.code()) {
                200 -> {
                    saveToken(res.body()?.token)
                    loginResponse.postValue(LoginResult(true,"login successful"))
                    if(saveCredetials) saveCredits(username,password)
                }
                404 ->
                    loginResponse.postValue(LoginResult(false,"user not found"))
                406 -> loginResponse.postValue(LoginResult(false,"wrong password"))
                else -> loginResponse.postValue(LoginResult(false,"login failed"))
            }
            println(res.body())
        }
    }

    private fun saveCredits(username: String, password: String) {
        sharedPreferences.edit().run {
            putString(USERNAME_KEY,username)
            putString(PASSWORD_KEY,password)
            apply()
        }
    }

    private fun saveToken(token: String?) {
        sharedPreferences.edit().run {
            putString(TOKEN_KEY,token)
            apply()
        }
    }

    fun deleteToken() {
        sharedPreferences.edit().run {
            remove(TOKEN_KEY)
            apply()
        }
    }

    companion object{
        @Volatile private var INSTANCE: AuthProvider? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthProvider(context).also { INSTANCE = it }
            }

    }

}