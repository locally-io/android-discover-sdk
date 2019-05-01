package io.locally.discover.authentication.network

import io.locally.discover.authentication.model.LoginRequest
import io.locally.discover.authentication.model.LoginResponse
import io.reactivex.Observable

class AuthenticationServices {

    companion object {
        fun login(username: String, password: String): Observable<LoginResponse>{
            val request = LoginRequest(username = username, password = password)

            return AuthenticationAPI.instance.login(request)
        }
    }
}