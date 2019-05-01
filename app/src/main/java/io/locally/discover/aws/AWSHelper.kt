package io.locally.discover.aws

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.amazonaws.auth.*
import com.amazonaws.regions.Regions
import io.locally.discover.authentication.managers.AuthenticationManager.Companion.FILENAME
import io.locally.discover.authentication.managers.TokenManager

class AWSHelper(context: Context) {

    private val region = Regions.US_WEST_2
    var credentialProvider: CognitoCachingCredentialsProvider
    private val tokenManager = TokenManager(context.getSharedPreferences(FILENAME, MODE_PRIVATE))

    init {
        credentialProvider = CognitoCachingCredentialsProvider(context, tokenManager.identity, region)
    }
}
