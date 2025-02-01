package com.grup4.googleauth.presentation.viewModel

import android.content.Intent
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class HomeScreenViewModel : ViewModel() {

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

}