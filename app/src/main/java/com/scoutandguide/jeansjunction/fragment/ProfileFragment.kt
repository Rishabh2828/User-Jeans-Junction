package com.scoutandguide.jeansjunction.fragment

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.scoutandguide.jeansjunction.R
import com.scoutandguide.jeansjunction.Utils
import com.scoutandguide.jeansjunction.activity.LoginActivity
import com.scoutandguide.jeansjunction.databinding.FragmentProfileBinding
import com.scoutandguide.jeansjunction.model.User
import com.scoutandguide.jeansjunction.viewModel.SignInViewModel
import com.scoutandguide.jeansjunction.viewModel.UserViewModel


class ProfileFragment : Fragment() {

    private lateinit var binding : FragmentProfileBinding
    var userId = Utils.getCurrentUserId()
    val viewModel  : SignInViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(layoutInflater)


        showProfileDetails()

        fetchUserData()

        binding.btnUpdate.setOnClickListener {
            updateProfile()
        }

        onLogout()

        return binding.root
    }

    fun showProfileDetails(){
        val userProfileImage : Uri? = Utils.getAuthInstance().currentUser?.photoUrl
        Glide.with(this)
            .load(userProfileImage)
            .into(binding.profilePicture)
    }

    private fun fetchUserData() {
        val currentUser = Utils.getAuthInstance().currentUser
        currentUser?.uid?.let { uid ->
            val userRef = Utils.getDatabseInstance().reference.child("User profiles").child(uid)
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
                    Log.e("ProfileFragment", "Failed to fetch user data", error.toException())
                }
            })
        }
    }

    private fun updateProfile() {

        Utils.showDialog(requireContext(),"Updating Profile")
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
                                Log.d("ProfileFragment", "Profile updated successfully")
                                Utils.hideDialog()
                                Utils.showToast(requireContext(), "Updated Successfully")
                            } else {
                                Log.e("ProfileFragment", "Failed to update profile", task.exception)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ProfileFragment", "Failed to fetch user data", error.toException())
                }
            })
        }
    }

    private fun onLogout() {
        binding.tbProfileFragment.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menuLogout ->{
                    logoutUser()
                    true
                }
                else -> {false}
            }
        }
    }
    private fun logoutUser() {





        val builder = AlertDialog.Builder(requireContext())
        val alertDialog = builder.create()
        builder.setTitle("Log Out")
            .setMessage("Are you sure to logout ?")
            .setPositiveButton("Yes"){_,_->
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .build()

                val googleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
                googleSignInClient.signOut().addOnCompleteListener {
                    // Proceed with your logout logic here
                    viewModel.logOutUser()
                    startActivity(Intent(requireContext(), LoginActivity::class.java))
                    requireActivity().finish()
                }
                requireActivity().finish()
            }
            .setNegativeButton("No"){_,_->
                alertDialog.dismiss()

            }
            .show()
            .setCancelable(false)




    }

}