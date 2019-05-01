package io.locally.discover.authentication.managers

import android.content.SharedPreferences
import io.locally.discover.authentication.model.LoginResponse.Data
import java.util.*

class TokenManager(private val preferences: SharedPreferences) {
    private var calendar: Calendar = Calendar.getInstance()

    fun accessToken(data: Data) {
        publisher = data.publisherId
        identity = data.identityId
        token = data.token

        calendar.apply {
            time = Date()
            add(Calendar.DATE, 7)
        }

        date = calendar.time
    }

    var publisher: String
        get() = preferences.getString(PUBLISHER, "") ?: ""
        set(value) = preferences.edit { it.putString(PUBLISHER, value) }

    var identity: String
        get() = preferences.getString(IDENTITY, "") ?: ""
        set(value) = preferences.edit { it.putString(IDENTITY, value) }

    var token: String
        get() = preferences.getString(TOKEN, "") ?: ""
        set(value) = preferences.edit { it.putString(TOKEN, value) }

    var date: Date?
        get() {
            val time = preferences.getLong(DATE, 0)
            return if(time > 0) Date(time)
            else null
        }
        set(value) {
            value?.let { date ->
                preferences.edit { it.putLong(DATE, date.time) }
            } ?: preferences.edit { it.putLong(DATE, 0) }
        }

    val isTokenValid: Boolean
        get() {
            return date?.let {
                Date().before(it)
            } ?: false
        }

    companion object {
        private const val DATE = "io.locally.discover.date"
        private const val TOKEN = "io.locally.discover.token"
        private const val IDENTITY = "io.locally.discover.identity"
        private const val PUBLISHER = "io.locally.discover.publisher"
    }

    private inline fun SharedPreferences.edit(operation: (SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        operation(editor)
        editor.apply()
    }
}