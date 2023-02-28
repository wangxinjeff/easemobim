package com.hyphenate.easemob.im.officeautomation.db_with_room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey


@Entity(tableName = "fileEntity", indices = [Index(value = ["filePath"], unique = true)])
class FileEntity {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "filePath")
    var filePath: String? = null

    @ColumnInfo(name = "fileSize")
    var fileSize: Long = 0
}