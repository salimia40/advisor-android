package ir.puyaars.advisor.api.auth

import com.google.gson.annotations.SerializedName
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

data class LoginResponse (@SerializedName("token") val token: String)

interface AuthApi {

    @FormUrlEncoded
    @POST("/api/login")
    fun login(
        @Field("username") username : String,
        @Field("password") password : String
    ):Call<LoginResponse>

}