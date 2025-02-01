package com.grup4.googleauth.presentation.viewModel

import com.google.firebase.auth.FirebaseAuth

class AuthCheckViewModel {

    private lateinit var auth: FirebaseAuth

    fun init(): Boolean {
        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser == null) {
            return false
        } else {
            return true
        }
    }

}