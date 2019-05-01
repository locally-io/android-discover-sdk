package io.locally.discover.authentication.managers

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.provider.Telephony.Mms.Part.FILENAME
import io.locally.discover.authentication.network.AuthenticationServices
import io.locally.discover.aws.AWSHelper
import org.jetbrains.anko.doAsync

class AuthenticationManager(private val context: Context) {
    private val tokenManager = TokenManager(context.getSharedPreferences(FILENAME, MODE_PRIVATE))
    var helper: AWSHelper? = null
    val isLogged: Boolean
        get() = tokenManager.isTokenValid

    fun login(username: String, password: String, callback: ((Boolean) -> Unit)?) {
        if (tokenManager.isTokenValid) {
            callback?.invoke(true)
            return
        }

        doAsync {
            AuthenticationServices.login(username, password)
                    .subscribe({ response ->
                        tokenManager.accessToken(response.data)
                        helper = AWSHelper(context)

                        callback?.invoke(true)
                    }, { err ->
                        tokenManager.date = null /* invalidate token */

                        err.printStackTrace()
                        callback?.invoke(false)
                    })
        }
    }

    val publisher: String
        get() = tokenManager.publisher

    companion object {
        const val FILENAME = "io.locally.discover.preferences"
    }
}