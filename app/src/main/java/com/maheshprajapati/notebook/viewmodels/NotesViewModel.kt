package com.maheshprajapati.myapplication.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.maheshprajapati.myapplication.database.AppDatabase
import com.maheshprajapati.myapplication.database.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
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
        GlobalScope.launch {
            val noteId = Date().time
            val note = Note(noteId, title, comment, date, false)
            val noteDataBase = AppDatabase.getInstance(context)
            noteDataBase!!.dao!!.insertNotes(note)
            GlobalScope.launch(Dispatchers.Main) {
                getAddedNotes.value = note
            }
        }
    }


    fun getNotesFromDB(
        context: Context
    ) {
        GlobalScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            val notes = noteDataBase!!.dao!!.getAll()
            GlobalScope.launch(Dispatchers.Main) {
                getAllNotes.value = notes
            }
        }
    }


    fun deleteNote(
        context: Context,
        note: Note
    ) {
        GlobalScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            noteDataBase!!.dao!!.delete(note)
            GlobalScope.launch(Dispatchers.Main) {
                noteEditStatues.value = true
            }
        }
    }


    fun updatePinStatus(
        context: Context,
        note: Note,
        isPinned: Boolean
    ) {

        GlobalScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            noteDataBase!!.dao!!.updateNotes(
                note.noteTitle!!,
                note.noteComment,
                note.noteDate,
                isPinned,
                note.noteId
            )
            GlobalScope.launch(Dispatchers.Main) {
                noteEditStatues.value = true
            }
        }
    }


    fun updateNotes(
        context: Context,
        note: Note
    ) {
        GlobalScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            noteDataBase!!.dao!!.updateNotes(
                note.noteTitle!!,
                note.noteComment,
                note.noteDate,
                note.isPinned!!,
                note.noteId
            )
            GlobalScope.launch(Dispatchers.Main) {
                noteEditStatues.value = true
            }
        }
    }


    fun getFilterList(
        context: Context,
        query: String
    ) {

        val noteFilter = ArrayList<Note>()
        GlobalScope.launch {
            val noteDataBase = AppDatabase.getInstance(context)
            val noteMain = noteDataBase!!.dao!!.getAll()
            for (item in noteMain) {
                if (item.noteTitle!!.contains(query, true) || item.noteComment!!.contains(
                        query,
                        true
                    ) || item.noteDate!!.contains(query, true)
                ) {
                    noteFilter.add(item)
                }
            }
            GlobalScope.launch(Dispatchers.Main) {
                getFilterNotes.value = noteFilter
            }
        }
    }

}