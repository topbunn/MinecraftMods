package com.youlovehamit.app.app

import android.util.Log
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging

fun initFirebasePush(){
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FIREBASE_PUSH", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            val token = task.result
            Log.d("FIREBASE_PUSH", token)
        })
    }