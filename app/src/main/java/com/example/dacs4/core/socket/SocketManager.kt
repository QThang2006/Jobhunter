package com.example.dacs4.core.socket

import android.util.Log
import com.example.dacs4.BuildConfig
import io.reactivex.disposables.Disposable
import ua.naiksoftware.stomp.Stomp
import ua.naiksoftware.stomp.StompClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketManager @Inject constructor() {
    private var mStompClient: StompClient
    private var lifecycleDisposable: Disposable? = null
    private val TAG = "SocketManager"

    init {
        // Chuyển đổi http -> ws cho đường dẫn socket
        val wsUrl = BuildConfig.BASE_URL.replace("http", "ws") + "ws/websocket"
        Log.d(TAG, "Initializing SocketManager with URL: $wsUrl")
        mStompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl)
        
        setupLifecycle()
    }

    private fun setupLifecycle() {
        lifecycleDisposable = mStompClient.lifecycle().subscribe { lifecycleEvent ->
            when (lifecycleEvent.type) {
                LifecycleEvent.Type.OPENED -> Log.d(TAG, "Stomp connection opened")
                LifecycleEvent.Type.ERROR -> Log.e(TAG, "Stomp connection error", lifecycleEvent.exception)
                LifecycleEvent.Type.CLOSED -> Log.d(TAG, "Stomp connection closed")
                LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> Log.e(TAG, "Stomp failed server heartbeat")
                else -> Log.d(TAG, "Stomp unknown event: ${lifecycleEvent.message}")
            }
        }
    }

    fun connect() {
        if (mStompClient.isConnected) {
            Log.d(TAG, "Socket already connected")
            return
        }
        Log.d(TAG, "Connecting to WebSocket...")
        mStompClient.connect()
    }

    fun subscribe(topic: String, onMessage: (String) -> Unit): Disposable {
        Log.d(TAG, "Subscribing to topic: $topic")
        return mStompClient.topic(topic).subscribe { stompMessage ->
            Log.d(TAG, "RECEIVED MESSAGE on $topic: ${stompMessage.payload}")
            onMessage(stompMessage.payload)
        }
    }

    fun disconnect() {
        Log.d(TAG, "Disconnecting Socket...")
        mStompClient.disconnect()
        lifecycleDisposable?.dispose()
    }
}
