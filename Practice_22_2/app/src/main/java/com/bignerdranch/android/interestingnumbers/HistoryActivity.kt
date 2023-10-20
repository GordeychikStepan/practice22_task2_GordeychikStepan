package com.bignerdranch.android.interestingnumbers

import android.content.ContentValues.TAG
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*

class HistoryActivity : AppCompatActivity() {

    private lateinit var database: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FactAdapter

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // инициализация базы данных
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "fact-database"
        ).build()


        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = FactAdapter()
        recyclerView.adapter = adapter

        // загрузка истории фактов из базы данных и отображение их в RecyclerView
        GlobalScope.launch(Dispatchers.IO) {
            val facts = database.factDao().getRecentFacts()
            withContext(Dispatchers.Main) {
                adapter.submitList(facts)
                Log.d(TAG, "Загружено ${facts.size} фактов из базы данных")
            }
        }

        val deleteButton = findViewById<Button>(R.id.deleteButton)
        deleteButton.setOnClickListener {
            GlobalScope.launch(Dispatchers.IO) {
                // удаление всех фактов из базы данных
                database.factDao().deleteAllFacts()

                // обновление списка фактов на экране
                val facts = database.factDao().getRecentFacts()
                withContext(Dispatchers.Main) {
                    adapter.submitList(facts)
                }
            }
        }

        adapter.setOnDeleteClickListener { fact ->
            GlobalScope.launch(Dispatchers.IO) {
                // Удаление выбранного факта из базы данных
                database.factDao().deleteFact(fact)

                // Обновление списка фактов на экране
                val facts = database.factDao().getRecentFacts()
                withContext(Dispatchers.Main) {
                    adapter.submitList(facts)
                }
            }
        }

        // редактирование факта
        adapter.setOnEditClickListener { fact ->
            showEditDialog(fact)
        }

        // добавление факта
        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            showAddDialog()
        }

        val returnImageButton = findViewById<ImageButton>(R.id.returnImageButton)
        returnImageButton.setOnClickListener {
            val intent = Intent(this, GetNumbersActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showAddDialog() {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setTitle("Добавить новый факт")

        val inputLayout = LinearLayout(this)
        inputLayout.orientation = LinearLayout.VERTICAL

        val numberEditText = EditText(this)
        numberEditText.hint = "Число"

        val factEditText = EditText(this)
        factEditText.hint = "Факт"

        inputLayout.addView(numberEditText)
        inputLayout.addView(factEditText)

        alertDialogBuilder.setView(inputLayout)

        alertDialogBuilder.setPositiveButton("Добавить") { _, _ ->
            val number = numberEditText.text.toString()
            val factText = factEditText.text.toString()

            if (number.isNotBlank() && factText.isNotBlank()) {
                val newFact = Fact(text = "$number - $factText")
                GlobalScope.launch(Dispatchers.IO) {
                    database.factDao().insertFact(newFact)
                    val facts = database.factDao().getRecentFacts()
                    withContext(Dispatchers.Main) {
                        adapter.submitList(facts)
                    }
                }
            } else {
                val alertDialogB = AlertDialog.Builder(this)
                alertDialogB.setTitle("Внимание!")
                alertDialogB.setMessage("Вы должны заполнить все поля, прежде чем добавлять новый факт.")
                alertDialogB.setPositiveButton("OK") { _, _ ->
                    showAddDialog()
                }
                val alertDialog = alertDialogB.create()
                alertDialog.show()
            }
        }

        alertDialogBuilder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun showEditDialog(fact: Fact) {
        val alertDialogBuilder = AlertDialog.Builder(this)

        val text = fact.text
        val number = text.substringBefore(" - ").toIntOrNull()

        if (number != null) alertDialogBuilder.setTitle("Редактировать факт об числе: '$number'")
        else alertDialogBuilder.setTitle("Редактировать факт об числе")

        val editText = EditText(this)
        val textToEdit = text.substringAfter(" - ").trim()
        editText.setText(textToEdit)

        alertDialogBuilder.setView(editText)

        alertDialogBuilder.setPositiveButton("Сохранить") { _, _ ->
            val newText = editText.text.toString()
            val updateFact = fact.copy(text = "$number - $newText")
            GlobalScope.launch(Dispatchers.IO) {
                database.factDao().updateFact(updateFact)
                val facts = database.factDao().getRecentFacts()
                withContext(Dispatchers.Main) {
                    adapter.submitList(facts)
                }
            }
        }

        alertDialogBuilder.setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}