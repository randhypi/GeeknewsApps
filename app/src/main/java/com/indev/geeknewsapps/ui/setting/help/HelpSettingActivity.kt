package com.indev.geeknewsapps.ui.setting.help

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.indev.geeknewsapps.R
import kotlinx.android.synthetic.main.activity_help_setting.*

class HelpSettingActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help_setting)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

//        Validation if user not have photo
        if (currentUser != null) {
            if (currentUser.photoUrl != null) {
                Glide.with(this)
                        .load(currentUser.photoUrl)
                        .into(iv_profile)
            } else {
                Glide.with(this)
                        .load("https://upload.wikimedia.org/wikipedia/commons/thumb/a/ac/No_image_available.svg/1200px-No_image_available.svg.png")
                        .into(iv_profile)
            }
        }
//        Validation if user not have name
        if (currentUser != null) {
            if (currentUser.displayName != null) {
                tv_fullName.text = currentUser.displayName
            } else {
                tv_fullName.text = currentUser.isAnonymous.toString()
            }
        }

        tv_emailUser.text = currentUser?.email

        btn_send.setOnClickListener {

            val recipient = et_recipient.text.toString().trim()
            val subject = et_subject.text.toString().trim()
            val message = et_message.text.toString().trim()

            sendEmail(recipient, subject, message)
        }

        iv_back.setOnClickListener {
            onBackPressed()
            return@setOnClickListener
        }
    }

    private fun sendEmail(recipient: String, subject: String, message: String) {
        val mIntent = Intent(Intent.ACTION_SEND)

        mIntent.data = Uri.parse("mailto:")
        mIntent.type = "text/plain"

        mIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
        mIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        mIntent.putExtra(Intent.EXTRA_TEXT, message)

        try {
            startActivity(Intent.createChooser(mIntent, "Choose email client..."))
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }
}