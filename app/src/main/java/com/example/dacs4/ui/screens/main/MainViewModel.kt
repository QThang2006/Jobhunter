package com.example.dacs4.ui.screens.main

import androidx.lifecycle.ViewModel
import com.example.dacs4.core.socket.SocketManager
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val socketManager: SocketManager
) : ViewModel() {

    fun initSocket() {
        socketManager.connect()
    }

    override fun onCleared() {
        super.onCleared()
        socketManager.disconnect()
    }
}
