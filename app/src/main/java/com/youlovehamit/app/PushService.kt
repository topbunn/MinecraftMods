package com.youlovehamit.app

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class PushService: FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("FIREBASE_PUSH", "Refreshed token: $token")
    }
}