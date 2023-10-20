package com.bignerdranch.android.interestingnumbers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.TypefaceSpan
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.textView)
        val welcomeText = resources.getString(R.string.welcome)
        val spannableString = SpannableString(welcomeText)
        val startIndex = welcomeText.indexOf("API")
        val endIndex = startIndex + "API".length
        val whiteSpan = ForegroundColorSpan(0xFFCCE4F4.toInt())
        spannableString.setSpan(whiteSpan, startIndex, endIndex, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        textView.text = spannableString

        // регистрация
        val registerButton = findViewById<Button>(R.id.registerButton)
        registerButton.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        usernameEditText = findViewById(R.id.usernameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        val loginButton: Button = findViewById(R.id.loginButton)

        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)

        // загрузка сохраненного имя пользователя и пароля из SharedPreferences
        val savedUsername = sharedPreferences.getString("login", "")
        val savedPassword = sharedPreferences.getString("password", "")
        // установка сохраненного имя пользователя в EditText
        usernameEditText.setText(savedUsername)

        loginButton.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty()) {
                usernameEditText.error = "Пожалуйста, введите имя пользователя"
                if (password.isEmpty()) {
                    passwordEditText.error = "Пожалуйста, введите пароль"
                    return@setOnClickListener
                }
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordEditText.error = "Пожалуйста, введите пароль"
                return@setOnClickListener
            }

            if (savedUsername == username && savedPassword == password) {
                val intent = Intent(this, GetNumbersActivity::class.java)
                intent.putExtra("saved_username", username)
                startActivity(intent)
                finish()
            } else {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Ошибка")
                alertDialogBuilder.setMessage("Неверный логин или пароль.")
                alertDialogBuilder.setPositiveButton("OK") { _, _ -> }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }
}