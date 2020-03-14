package com.bombadu.sqley

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var myDB: SQLiteDatabase


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getDataFromDB()

    }

    private fun getDataFromDB() {
        val myList = mutableListOf<String>()
        val rowIdList = mutableListOf<String>()
        myList.clear()
        openDB()

        val cursor: Cursor = myDB.rawQuery("Select * FROM myTable ORDER BY city ASC", null)
        cursor.moveToFirst()


        val idColumn = cursor.getColumnIndex("id")
        val nameColumn = cursor.getColumnIndex("name")
        val ageColumn = cursor.getColumnIndex("age")
        val cityColumn = cursor.getColumnIndex("city")

        if (cursor.count > 0) {
            do {
                val ids = cursor.getString(idColumn)
                val names = cursor.getString(nameColumn)
                val ages = cursor.getString(ageColumn)
                val cities = cursor.getString(cityColumn)

                val myString = "Name: $names Age: $ages City: $cities"
                myList.add(myString)
                rowIdList.add(ids)
            } while (cursor.moveToNext())
        }

        myDB.close()
        cursor.close()

        val myAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, myList)
        val listView = findViewById<ListView>(R.id.list_view)
        listView.adapter = myAdapter
        listView.onItemLongClickListener = AdapterView.OnItemLongClickListener { parent, view, position, id ->
            val rowId = rowIdList[position]
           // Toast.makeText(this, "Row: $rowId", Toast.LENGTH_SHORT).show()
            openDB()
            myDB.delete("myTable", "id=$rowId", null)
            myDB.close()
            getDataFromDB()

           true

        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            formValidate()

        }
        return super.onOptionsItemSelected(item)
    }

    private fun formValidate() {
        if (nameEditText.text.isEmpty() || ageEditText.text.isEmpty() || cityEditText.text.isEmpty()) {
            Toast.makeText(this, "Missing Data", Toast.LENGTH_SHORT).show()
        } else {
            saveToDatabase()
        }
    }

    private fun saveToDatabase() {
        openDB()
        placeIntoDatabase()
    }

    private fun placeIntoDatabase() {
        val myName = nameEditText.text.toString()
        val myAge = ageEditText.text.toString()
        val myCity = cityEditText.text.toString()

        myDB.execSQL("INSERT INTO myTable(name, age, city) VALUES('$myName','$myAge','$myCity');")
        nameEditText.text = null
        ageEditText.text = null
        cityEditText.text = null
        Toast.makeText(this, "Saved..", Toast.LENGTH_SHORT).show()
        myDB.close()

        getDataFromDB()


    }

    private fun openDB() {
        myDB = this.openOrCreateDatabase("MyDB", MODE_PRIVATE, null)
        myDB.execSQL("CREATE TABLE IF NOT EXISTS myTable " + "(id integer primary key, name text, age text, city text);")

    }
}
