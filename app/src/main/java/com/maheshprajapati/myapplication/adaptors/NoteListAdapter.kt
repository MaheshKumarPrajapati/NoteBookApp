package com.maheshprajapati.myapplication.adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.database.Note
import com.maheshprajapati.myapplication.databinding.NoteListItemsBinding
import com.maheshprajapati.myapplication.fragments.AddNoteFragment
import com.maheshprajapati.myapplication.utility.AppConstants


class NoteListAdapter :
    RecyclerView.Adapter<NoteListAdapter.NotesViewHolder>() {
    private var notes: List<Note>? = null
    private lateinit var context: Context
    lateinit var refreshAfterDetailsPageInterface: AppConstants.OnBackFromDetailsScreen
    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        i: Int
    ): NotesViewHolder {
        val noteListItemBinding: NoteListItemsBinding = DataBindingUtil.inflate(
            LayoutInflater.from(viewGroup.context),
            R.layout.note_list_items, viewGroup, false
        )
        return NotesViewHolder(noteListItemBinding,i)
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
        //noteViewHolder.noteListItemBinding.etNoteComment.text=currentNote.noteComment
        noteViewHolder.noteListItemBinding.noteDate=currentNote
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
        noteListItemBinding: NoteListItemsBinding,
        position: Int
    ) :
        RecyclerView.ViewHolder(noteListItemBinding.getRoot()) {
        internal var noteListItemBinding: NoteListItemsBinding = noteListItemBinding

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