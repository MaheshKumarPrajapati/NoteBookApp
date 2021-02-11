package com.maheshprajapati.myapplication.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import java.util.*

@Dao
interface NotesDeo {
    @Query("SELECT * FROM note")
    fun getAll(): List<Note>

    @Query("SELECT * FROM note WHERE noteId IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Note>

    @Insert(onConflict = REPLACE)
    fun insertNotes(vararg notes: Note)

    @Delete
    fun delete(user: Note)

    @Query(
        "UPDATE note SET noteTitle=:noteTitle,noteComment = :noteComment ,noteDate= :noteDate,isPinned= :isPinned WHERE noteId= :noteId"
    )
    fun updateNotes(
        noteTitle: String,
        noteComment: String?,
        noteDate: String?,
        isPinned: Boolean,
        noteId: Long
    )

}