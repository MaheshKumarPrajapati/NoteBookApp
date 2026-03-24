package com.maheshprajapati.notebook.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.maheshprajapati.notebook.R
import com.maheshprajapati.notebook.database.Note
import com.maheshprajapati.notebook.databinding.NoteListItemsBinding
import com.maheshprajapati.notebook.fragments.AddNoteFragment
import com.maheshprajapati.notebook.utility.AppConstants


class NoteListAdapter :
    RecyclerView.Adapter<NoteListAdapter.NotesViewHolder>() {
    private var notes: List<Note>? = null
    private lateinit var context: Context
    lateinit var refreshAfterDetailsPageInterface: AppConstants.OnBackFromDetailsScreen
    
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): NotesViewHolder {
        val noteListItemBinding: NoteListItemsBinding = NoteListItemsBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup, false
        )
        return NotesViewHolder(noteListItemBinding)
    }

    override fun onBindViewHolder(
        noteViewHolder: NotesViewHolder,
        i: Int
    ) {
        val currentNote: Note = notes!![i]
        setRandomeBackground( noteViewHolder.noteListItemBinding.clNote,i)
        if(currentNote!!.isPinned!!) {
            noteViewHolder.noteListItemBinding.pinIcon.visibility= View.VISIBLE
        }else {
            noteViewHolder.noteListItemBinding.pinIcon.visibility= View.GONE
        }
        noteViewHolder.noteListItemBinding.noteDate = currentNote
        noteViewHolder.noteListItemBinding.executePendingBindings()
    }
    
    var count=0
    private fun setRandomeBackground(clNote: ConstraintLayout, i: Int) {
        if(count==0){
            clNote.setBackgroundResource(R.color.notes_tiles_1)
            count++
        } else if(count==1){
            clNote.setBackgroundResource(R.color.notes_tiles_2)
            count++
        } else if(count==2){
            clNote.setBackgroundResource(R.color.notes_tiles_3)
            count++
        }else {
            clNote.setBackgroundResource(R.color.notes_tiles_4)
            count=0

        }
    }

    override fun getItemCount(): Int {
        return if (notes != null) {
            notes!!.size
        } else {
            0
        }
    }

    fun setNoteList(
        context: Context,
        noteList: List<Note>?,
        refreshAfterDetailsPageInterface: AppConstants.OnBackFromDetailsScreen
    ) {
        this.notes = noteList
        this.context=context
        this.refreshAfterDetailsPageInterface=refreshAfterDetailsPageInterface
        notifyDataSetChanged()
    }



    inner class NotesViewHolder(
        val noteListItemBinding: NoteListItemsBinding
    ) :
        RecyclerView.ViewHolder(noteListItemBinding.root) {

        init {
            noteListItemBinding.root.setOnClickListener {
                val note= Gson().toJson(noteListItemBinding.noteDate)
                var fragment: AddNoteFragment = AddNoteFragment.newInstance(true,note)
                fragment.intialiseRefreshInterface(refreshAfterDetailsPageInterface)
                val activity = context as FragmentActivity
                val ft = activity.supportFragmentManager.beginTransaction()
                ft.replace(R.id.frame_container, fragment)
                ft.addToBackStack(null)
                ft.commitAllowingStateLoss()
            }
        }
    }
}
