package com.scoutandguide.jeansjunction.activity

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.databinding.ActivityProfileBinding
import com.scoutandguide.jeansjunction.model.User
import com.scoutandguide.jeansjunction.viewModel.SignInViewModel

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: SignInViewModel by viewModels()
    private var userId = Utils.getCurrentUserId()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        showProfileDetails()
        fetchUserData()

        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }

        binding.tbProfileFragment.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuLogout -> {
                    logoutUser()
                    true
                }
                else -> false
            }
        }
    }

    private fun showProfileDetails() {
        val userProfileImage: Uri? = Utils.getAuthInstance().currentUser?.photoUrl
        Glide.with(this)
            .load(userProfileImage)
            .into(binding.profilePicture)
    }

    private fun fetchUserData() {
        val currentUser = Utils.getAuthInstance().currentUser
        currentUser?.uid?.let { uid ->
            val userRef = FirebaseDatabase.getInstance().getReference("User profiles").child(uid)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        binding.userName.setText(it.name)
                        binding.userNumber.setText(it.mobilenumber)
                        binding.userAddress.setText(it.address)
                        binding.userPincode.setText(it.pincode)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileActivity", "Failed to fetch user data", error.toException())
                }
            })
        }
    }

    private fun updateProfile() {
        Utils.showDialog(this, "Updating Profile")
        userId?.let { uid ->
            val userRef = FirebaseDatabase.getInstance().getReference("User profiles").child(uid)

            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val user = snapshot.getValue(User::class.java)
                    user?.let {
                        it.name = binding.userName.text.toString()
                        it.mobilenumber = binding.userNumber.text.toString()
                        it.address = binding.userAddress.text.toString()
                        it.pincode = binding.userPincode.text.toString()

                        userRef.setValue(it).addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Log.d("ProfileActivity", "Profile updated successfully")
                                Utils.hideDialog()
                                Utils.showToast(this@ProfileActivity, "Updated Successfully")
                            } else {
                                Log.e("ProfileActivity", "Failed to update profile", task.exception)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileActivity", "Failed to fetch user data", error.toException())
                }
            })
        }
    }

    private fun logoutUser() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Log Out")
            .setMessage("Are you sure to logout?")
            .setPositiveButton("Yes") { _, _ ->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(this, gso)
                googleSignInClient.signOut().addOnCompleteListener {
                    // Proceed with your logout logic here
                    viewModel.logOutUser()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }
            .setNegativeButton("No") { _, _ ->
                // Dismiss the dialog
            }
            .show()
            .setCancelable(false)
    }
}
