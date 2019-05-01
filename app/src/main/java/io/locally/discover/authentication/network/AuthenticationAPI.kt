package io.locally.discover.authentication.network

import io.locally.discover.authentication.RestClient
import io.locally.discover.authentication.model.LoginRequest
import io.locally.discover.authentication.model.LoginResponse
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationAPI {

    @POST("/apps/cognitologin.json")
    fun login(@Body loginRequest: LoginRequest): Observable<LoginResponse>

    companion object {
        val instance: AuthenticationAPI by lazy {
            RestClient.instance.create(AuthenticationAPI::class.java)
        }
    }
}