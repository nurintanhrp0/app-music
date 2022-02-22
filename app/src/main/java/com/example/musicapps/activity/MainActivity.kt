package com.example.musicapps.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.musicapps.R
import com.example.musicapps.fragment.FavoriteFragment
import com.example.musicapps.fragment.LibraryFragment
import kotlinx.android.synthetic.main.activity_main.*
import androidx.fragment.app.FragmentTransaction
import com.example.musicapps.GlobalApp
import com.example.musicapps.database.DaoMaster
import com.example.musicapps.util.Config
import com.example.musicapps.util.GlobalVariable


class MainActivity : AppCompatActivity() {
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar as Toolbar?)
        supportActionBar!!.setDisplayHomeAsUpEnabled(false)
        supportActionBar!!.setDisplayShowHomeEnabled(false)
        supportActionBar!!.setDisplayShowTitleEnabled(true)
        supportActionBar!!.title = getString(R.string.menu_library)

        checkStoragePermission()
    }

    private fun initView() {

        //fragments
        val libraryFragment = LibraryFragment()
        val favoriteFragment = FavoriteFragment()

        //default fragment
        currentFragment = libraryFragment
        setBundle("")

        bottomToolbar.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.home -> onBackPressed()
                R.id.library -> {
                    supportActionBar!!.title = getString(R.string.menu_library)
                    currentFragment = libraryFragment
                    setBundle("")
                }
                R.id.favorite -> {
                    supportActionBar!!.title = getString(R.string.menu_favorite)
                    currentFragment = favoriteFragment
                    setBundle("")
                }

            }
            true
        }

        //search music
        txtSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) { setBundle(txtSearch.text.toString())}
        })

        txtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                setBundle(txtSearch.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }

    }

    private fun setBundle(qArtist: String) {
        val bundle = Bundle()
        bundle.putString("q_artist", qArtist)
        currentFragment.arguments = bundle

        setCurrentFragment(currentFragment)
    }

    private fun setCurrentFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        if (currentFragment == fragment) {
            supportFragmentManager.popBackStack()
        }
        transaction.replace(R.id.frameContent, fragment)
        transaction.addToBackStack(null)
        transaction.commit()

    }

    private fun checkStoragePermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Config.createDirectories()
                initDaoSession()
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Storage permission is needed to write external storage.", Toast.LENGTH_SHORT).show()
                }
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), GlobalVariable.REQUEST_CODE_WRITE_EXTERNAL_STORAGE)
                return false
            }
        } else {
            Config.createDirectories()
        }

        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == GlobalVariable.REQUEST_CODE_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Config.createDirectories()
                initDaoSession()
            } else {
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun initDaoSession() {
        if ((application as GlobalApp).daoSession == null) {
            val database = Config.getDirectory(Config.DIRECTORY_DATABASE) + "/" + GlobalVariable.DATABASE_NAME
            val helper = DaoMaster.DevOpenHelper(this, database)
            val db = helper.writableDb
            (application as GlobalApp).daoSession = DaoMaster(db).newSession()
        }

        initView()
    }
}