package com.hyphenate.easemob.im.officeautomation.db_with_room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun inserFile(fileEntity: FileEntity)

    @Query("SELECT * from fileEntity")
    fun getFileList(): List<FileEntity>
}