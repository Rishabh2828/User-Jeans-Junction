package com.scoutandguide.jeansjunction

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.scoutandguide.jeansjunction.databinding.ProgressDialogBinding
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

object Utils {

    private  var dialog : AlertDialog? = null
    fun showDialog(context: Context, message: String){
        val progress = ProgressDialogBinding.inflate(LayoutInflater.from(context))
        progress.tvMessage.text = message
        dialog = AlertDialog.Builder(context).setView(progress.root).setCancelable(false).create()
        dialog!!.show()
    }

    fun hideDialog(){
        dialog?.dismiss()
    }

    fun showToast(context: Context, message : String){
        Toast.makeText(context,message, Toast.LENGTH_SHORT).show()
    }

    private var firebaseAuthInstance : FirebaseAuth? = null

    fun getAuthInstance() : FirebaseAuth {
        if(firebaseAuthInstance == null){
            firebaseAuthInstance = FirebaseAuth.getInstance()
        }

        return firebaseAuthInstance!!
    }

    fun getCurrentUserId() : String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }

    fun getRandomId():String{

        return (1..25).map { (('A'..'Z') + ('a'..'z') + ('0'..'9')).random() }.joinToString("")
    }

    private var firebaseDatabseInstance : FirebaseDatabase? = null
    fun getDatabseInstance() : FirebaseDatabase {
        if (firebaseDatabseInstance == null){
            firebaseDatabseInstance = FirebaseDatabase.getInstance()
        }

        return  firebaseDatabseInstance!!
    }

    fun getCurrentDate(): String?{
        val currentDate= LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")
        return currentDate.format(formatter)
    }

    fun getCurrentTime(): String {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        return sdf.format(Date())
    }

    fun getCurrentTimestamp(): Long {
        return System.currentTimeMillis()
    }

}