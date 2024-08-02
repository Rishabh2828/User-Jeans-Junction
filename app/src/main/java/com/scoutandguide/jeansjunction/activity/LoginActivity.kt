package com.scoutandguide.jeansjunction.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.viewModel.SignInViewModel
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.databinding.ActivityLoginBinding
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private val viewModel: SignInViewModel by viewModels()


    companion object {
        const val RC_SIGN_IN = 9001
    }

    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        lifecycleScope.launch {
            viewModel.isACurrentUser.collect{

                if (it) {
                    Utils.showToast(this@LoginActivity, "Welcome Back")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }
        }

        lifecycleScope.launch {

            viewModel.signedInSuccessfully.collect{
                if (it) {
                    Utils.hideDialog()
                    Utils.showToast(this@LoginActivity, "Signed in")
                    startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                }
            }
        }

        binding.loginGg.setOnClickListener {
            signIn()

        }
    }

    private fun signIn() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.web_client_id))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(this, gso)
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    viewModel.signInWithGoogle(account.idToken!!)
                    Log.d("login check", " Successfull")

                    Utils.showDialog(this, "Signing you..")

                }
            } catch (e: ApiException) {
                // Handle sign-in failure
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }

}