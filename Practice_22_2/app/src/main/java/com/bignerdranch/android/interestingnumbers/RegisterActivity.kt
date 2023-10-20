package com.bignerdranch.android.interestingnumbers

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog

class RegisterActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val toRegisterButton = findViewById<Button>(R.id.registerButton)
        sharedPreferences = getSharedPreferences("my_app_prefs", Context.MODE_PRIVATE)

        // кнопка назад
        val returnImageButton = findViewById<ImageButton>(R.id.returnImageButton)
        returnImageButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        toRegisterButton.setOnClickListener {

            val loginTextView = findViewById<EditText>(R.id.usernameEditText)
            val passwordTextView = findViewById<EditText>(R.id.passwordEditText)

            val username = loginTextView.text.toString().trim()
            val password = passwordTextView.text.toString().trim()

            if (username.isEmpty()) {
                loginTextView.error = "Пожалуйста, введите имя пользователя"
                if (password.isEmpty()) {
                    passwordTextView.error = "Пожалуйста, введите пароль"
                    return@setOnClickListener
                }
                return@setOnClickListener
            }
            if (password.isEmpty()) {
                passwordTextView.error = "Пожалуйста, введите пароль"
                return@setOnClickListener
            }

            // сохранение имя пользователя и пароля в SharedPreferences
            sharedPreferences.edit().putString("login", username).apply()
            sharedPreferences.edit().putString("password", password).apply()

            val alertDialogBuilder = AlertDialog.Builder(this)
            alertDialogBuilder.setTitle("Регистрация завершена")
            alertDialogBuilder.setMessage("Вы успешно зарегистрировались.")
            alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            val alertDialog = alertDialogBuilder.create()
            alertDialog.show()
        }
    }
}