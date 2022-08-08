package com.prosecshane.zametki

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.prosecshane.zametki.databinding.ActivityMainBinding
import com.prosecshane.zametki.notes.*

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    fun editNote(noteToEdit: Note) {
        val intent = Intent(this@MainActivity, NoteActivity::class.java)
        val bundle = Bundle()
        bundle.putString("note", noteToEdit.toJson())
        intent.putExtra("bundle", bundle)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.appBarMain.createTextNote.setOnClickListener {
            this.editNote(Note("Новая заметка"))
        }
        binding.appBarMain.createCheckNote.setOnClickListener {
            this.editNote(CheckNote("Новая заметка"))
        }
        binding.appBarMain.createAlarmNote.setOnClickListener {
            this.editNote(AlarmNote("Новая заметка"))
        }
        binding.appBarMain.createImageNote.setOnClickListener {
            this.editNote(ImageNote("Новая заметка"))
        }

        setSupportActionBar(binding.appBarMain.toolbar)
        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.nav_main), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
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