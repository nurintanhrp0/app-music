package com.example.musicapps.util

import android.os.Environment
import java.io.File

object Config {
        private const val DIRECTORY_ROOT = 1
        private const val DIRECTORY_CACHE = 2
        const val DIRECTORY_DATABASE = 3

        fun getDirectory(directory: Int): String {
            return when (directory) {
                DIRECTORY_ROOT -> Environment.getExternalStorageDirectory().toString() + "/" + GlobalVariable.DIRECTORY_ROOT_NAME
                DIRECTORY_CACHE -> Environment.getExternalStorageDirectory().toString() + "/" + GlobalVariable.DIRECTORY_ROOT_NAME + "/Caches"
                DIRECTORY_DATABASE -> Environment.getExternalStorageDirectory().toString() + "/" + GlobalVariable.DIRECTORY_ROOT_NAME + "/Databases"
                else -> ""
            }
        }

        fun createDirectories() {
            val mainDirectory = File(getDirectory(DIRECTORY_ROOT))
            val cacheDirectory = File(getDirectory(DIRECTORY_CACHE))
            val databaseDirectory = File(getDirectory(DIRECTORY_DATABASE))

            if (!mainDirectory.exists()) {
                mainDirectory.mkdir()
            }
            if (!cacheDirectory.exists()) {
                cacheDirectory.mkdir()
            }
            if (!databaseDirectory.exists()) {
                databaseDirectory.mkdir()
            }

        }
}