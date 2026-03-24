package com.maheshprajapati.notebook.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maheshprajapati.notebook.database.AppDatabase
import com.maheshprajapati.notebook.database.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

class NotesViewModel : ViewModel() {
    val getAddedNotes = MutableLiveData<Note>()
    val noteEditStatues = MutableLiveData<Boolean>()
    val getAllNotes = MutableLiveData<List<Note>>()
    val getFilterNotes = MutableLiveData<ArrayList<Note>>()

    fun addNotestoDB(
        context: Context,
        date: String,
        title: String,
        comment: String
    ) {
        viewModelScope.launch {
            val noteId = Date().time
            val note = Note(noteId, title, comment, date, false)
            val noteDataBase = AppDatabase.getInstance(context)
            withContext(Dispatchers.IO) {
                noteDataBase!!.dao!!.insertNotes(note)
            }
            getAddedNotes.value = note
        }
    }


    fun getNotesFromDB(
        context: Context
    ) {
        viewModelScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            val notes = withContext(Dispatchers.IO) {
                noteDataBase!!.dao!!.getAll()
            }
            getAllNotes.value = notes
        }
    }


    fun deleteNote(
        context: Context,
        note: Note
    ) {
        viewModelScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            withContext(Dispatchers.IO) {
                noteDataBase!!.dao!!.delete(note)
            }
            noteEditStatues.value = true
        }
    }


    fun updatePinStatus(
        context: Context,
        note: Note,
        isPinned: Boolean
    ) {

        viewModelScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            withContext(Dispatchers.IO) {
                noteDataBase!!.dao!!.updateNotes(
                    note.noteTitle!!,
                    note.noteComment,
                    note.noteDate,
                    isPinned,
                    note.noteId
                )
            }
            noteEditStatues.value = true
        }
    }


    fun updateNotes(
        context: Context,
        note: Note
    ) {
        viewModelScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            withContext(Dispatchers.IO) {
                noteDataBase!!.dao!!.updateNotes(
                    note.noteTitle!!,
                    note.noteComment,
                    note.noteDate,
                    note.isPinned!!,
                    note.noteId
                )
            }
            noteEditStatues.value = true
        }
    }


    fun getFilterList(
        context: Context,
        query: String
    ) {
        viewModelScope.launch {
            val noteFilter = ArrayList<Note>()
            val noteDataBase = AppDatabase.getInstance(context)
            val noteMain = withContext(Dispatchers.IO) {
                noteDataBase!!.dao!!.getAll()
            }
            for (item in noteMain) {
                if (item.noteTitle?.contains(query, true) == true || 
                    item.noteComment?.contains(query, true) == true || 
                    item.noteDate?.contains(query, true) == true
                ) {
                    noteFilter.add(item)
                }
            }
            getFilterNotes.value = noteFilter
        }
    }

}