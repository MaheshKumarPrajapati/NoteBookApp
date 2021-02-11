package com.maheshprajapati.myapplication.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Note(
    @PrimaryKey val noteId: Long,
    @ColumnInfo(name = "noteTitle") val noteTitle: String?,
    @ColumnInfo(name = "noteComment") val noteComment: String?,
    @ColumnInfo(name = "noteDate") val noteDate: String?,
    @ColumnInfo(name = "isPinned") val isPinned: Boolean?
){
}