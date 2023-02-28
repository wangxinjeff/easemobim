package com.hyphenate.easemob.im.officeautomation.db_with_room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FileEntity::class], version = 1, exportSchema = false)

abstract class FileDataBase : RoomDatabase() {

    abstract fun getFileDao(): FileDao

    companion object {

        var INSTANCE: FileDataBase? = null

        fun getFileDataBase(context: Context): FileDataBase {

            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, FileDataBase::class.java, "file_list_room.db").fallbackToDestructiveMigration().addCallback(callback).build()
            }

            return INSTANCE as FileDataBase
        }

        val callback = object : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
            }

            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }


        fun destroyInstance() {
            INSTANCE = null
        }
    }
}