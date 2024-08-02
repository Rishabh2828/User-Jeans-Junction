package com.scoutandguide.jeansjunction.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.model.User

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SignInViewModel : ViewModel() {

    private val auth: FirebaseAuth = Utils.getAuthInstance()
    private val database: FirebaseDatabase = Utils.getDatabseInstance()


    private val _signedInSuccessfully = MutableStateFlow(false)
    val signedInSuccessfully: StateFlow<Boolean> = _signedInSuccessfully

    private val _isACurrentUser = MutableStateFlow(false)
    val isACurrentUser: StateFlow<Boolean> = _isACurrentUser


    init {
        Utils.getAuthInstance().currentUser?.let {
            _isACurrentUser.value = true
        }
    }

    fun signInWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("login", " Successful")
                    val user: FirebaseUser? = auth.currentUser
                    val firebaseUser = User(
                        uId = user?.uid,
                        name = user?.displayName,
                        profile = user?.photoUrl?.toString(),
                        mobilenumber = null,
                        address = null,
                        pincode = null
                    )
                    user?.uid?.let { uid ->
                        database.reference
                            .child("User profiles")
                            .child(uid)
                            .setValue(firebaseUser)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    _signedInSuccessfully.value = true
                                } else {
                                    Log.e("err", task.exception?.localizedMessage ?: "Unknown error")
                                }
                            }
                    }
                } else {
                    Log.e("err", task.exception?.localizedMessage ?: "Unknown error")
                }

            }
    }

    fun logOutUser(){
        FirebaseAuth.getInstance().signOut()
    }

}