package com.example.musicapps

import android.content.pm.PackageManager
import android.os.Build
import androidx.multidex.MultiDexApplication
import com.example.musicapps.database.DaoMaster
import com.example.musicapps.database.DaoSession
import com.example.musicapps.util.Config
import com.example.musicapps.util.GlobalVariable

class GlobalApp : MultiDexApplication() {

    var daoSession: DaoSession? = null

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Config.createDirectories()
                val database = Config.getDirectory(Config.DIRECTORY_DATABASE) + "/" + GlobalVariable.DATABASE_NAME
                val helper = DaoMaster.DevOpenHelper(this, database)
                val db = helper.writableDb
                daoSession = DaoMaster(db).newSession()
            }
        } else {
            Config.createDirectories()
            val database = Config.getDirectory(Config.DIRECTORY_DATABASE) + "/" + GlobalVariable.DATABASE_NAME
            val helper = DaoMaster.DevOpenHelper(this, database)
            val db = helper.writableDb
            daoSession = DaoMaster(db).newSession()
        }
    }
}