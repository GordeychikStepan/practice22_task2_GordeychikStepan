package com.bignerdranch.android.interestingnumbers

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GetNumbersActivity : AppCompatActivity() {

    lateinit var snackbar: Snackbar
    private lateinit var numberEditText: EditText
    private lateinit var factTextView: TextView
    private lateinit var database: AppDatabase

    private val options = TranslatorOptions.Builder()
        .setSourceLanguage(TranslateLanguage.ENGLISH)
        .setTargetLanguage(TranslateLanguage.RUSSIAN)
        .build()
    private val englishRussianTranslator = Translation.getClient(options)
    private var conditions = DownloadConditions.Builder()
        .requireWifi()
        .build()

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_get_numbers)

        // переход на экран с историей
        val getHistoryButton: Button = findViewById(R.id.getHistory)
        getHistoryButton.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // скачать модель, если она еще не скачана
        englishRussianTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // модель была загружена
                snackbar = Snackbar.make(
                    findViewById<View>(android.R.id.content),
                    "Model downloaded successfully. Okay to start translating.",
                    Snackbar.LENGTH_LONG
                )
            }
            .addOnFailureListener {
                // модель не была загружена или другая ошибка, связанная с интернетом
                snackbar = Snackbar.make(
                    findViewById<View>(android.R.id.content),
                    "Model couldn’t be downloaded or other internal error.",
                    Snackbar.LENGTH_LONG
                )
            }

        // инициализация базы данных
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "fact-database"
        ).build()

        numberEditText = findViewById(R.id.numberEditText)
        val intent = intent
        if (intent.hasExtra("saved_username")) {
            val userName = intent.getStringExtra("saved_username")
            numberEditText.hint = "$userName, введите число"
        }

        val getFactButton: Button = findViewById(R.id.getFactButton)
        factTextView = findViewById(R.id.factTextView)

        getFactButton.setOnClickListener {
            val number = numberEditText.text.toString().trim()

            if (number.isNotEmpty() && number.matches(Regex("[0-9]+"))) {

                val url = "http://numbersapi.com/$number"
                val queue: RequestQueue = Volley.newRequestQueue(this)

                val stringRequest = StringRequest(
                    Request.Method.GET, url,
                    { response ->
                        englishRussianTranslator.translate(response)
                            .addOnSuccessListener {
                                // перевод завершен
                                factTextView.text = it

                                // сохранение факта в базе данных
                                val fact = Fact(text = it)
                                GlobalScope.launch(Dispatchers.IO) {
                                    database.factDao().insertFact(fact)
                                    Log.d(TAG, "Факт сохранен в базе данных: $fact")
                                }
                            }
                            .addOnFailureListener {
                                // ошибка
                                Log.e(TAG, "Error: " + it.localizedMessage)
                            }
                    },
                    {
                        factTextView.text = "Ошибка при получении факта"
                    })

                queue.add(stringRequest)
            } else {
                factTextView.text = "Пожалуйста, введите корректное число"
                numberEditText.error = "Пожалуйста, введите корректное число"
            }
        }
    }
}
