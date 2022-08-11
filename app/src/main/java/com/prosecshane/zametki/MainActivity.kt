package com.prosecshane.zametki

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.children
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
import com.prosecshane.zametki.databinding.ActivityMainBinding
import com.prosecshane.zametki.notes.*
import com.prosecshane.zametki.ui.MainFragment


class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    lateinit var binding: ActivityMainBinding
    var deleteMode = false
    var toDelete: Long? = null

    fun paintDeleteButton(color: Int) {
        this.findViewById<Button>(R.id.delete_button).backgroundTintList =
            ContextCompat.getColorStateList(this, color)
    }

    fun editNote(noteToEdit: Note) {
        val intent = Intent(this@MainActivity, NoteActivity::class.java)
        val bundle = Bundle()
        bundle.putString("note", noteToEdit.toJson())
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    private fun getAllIds(): MutableList<Long> {
        val res: MutableList<Long> = MutableList(0) { 0.toLong() }
        for (fileName in this.fileList()) {
            res.add(fileName.toString().toLong())
        }
        return res
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarMain.createTextNote.setOnClickListener {
            val note = Note(title = "Новая заметка")
            note.generateExclusiveId(getAllIds())
            this.editNote(note)
        }
        binding.appBarMain.createCheckNote.setOnClickListener {
            val note = CheckNote(title = "Новая заметка")
            note.generateExclusiveId(getAllIds())
            this.editNote(note)
        }
        binding.appBarMain.createAlarmNote.setOnClickListener {
            val note = AlarmNote(title = "Новая заметка")
            note.generateExclusiveId(getAllIds())
            this.editNote(note)
        }
        binding.appBarMain.createImageNote.setOnClickListener {
            val note = ImageNote(title = "Новая заметка")
            note.generateExclusiveId(getAllIds())
            this.editNote(note)
        }
        val deleteDialogBuilder = AlertDialog.Builder(this)
        deleteDialogBuilder.setTitle("Удаление заметки")
        deleteDialogBuilder.setMessage(
            "Подтвердите удаление заметки. После удаления её нельзя будет вернуть!"
        )
        deleteDialogBuilder.setPositiveButton("Удалить") { _, _ ->
            deleteFile(toDelete.toString())
            (supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main
            )?.childFragmentManager?.fragments?.get(0) as MainFragment).updateNoteColumn()
        }
        deleteDialogBuilder.setNegativeButton("Отмена") { _, _ ->
            toDelete = null
        }
        binding.appBarMain.deleteButton.setOnClickListener {
            deleteMode = if (deleteMode) {
                paintDeleteButton(R.color.orange_main)
                for (child in findViewById<LinearLayout>(R.id.notesColumn).children) {
                    child.setBackgroundResource(R.drawable.rounded_outside)
                    child.setOnClickListener { editNote((child as NoteView).note) }
                }
                false
            } else {
                paintDeleteButton(R.color.red_main)
                for (child in findViewById<LinearLayout>(R.id.notesColumn).children) {
                    child.setBackgroundResource(R.drawable.rounded_outside_scary)
                    child.setOnClickListener {
                        toDelete = (child as NoteView).note.id
                        deleteDialogBuilder.create().show()
                    }
                }
                true
            }
        }

        // Creates action bar
        setSupportActionBar(binding.appBarMain.toolbar)
        // Find views
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        // Add hamburger button to top left on action bar
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_main), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        // Make drawer items functional
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}