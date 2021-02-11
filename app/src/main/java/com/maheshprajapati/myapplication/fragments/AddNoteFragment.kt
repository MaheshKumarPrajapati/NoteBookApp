package com.maheshprajapati.myapplication.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.database.Note
import com.maheshprajapati.myapplication.databinding.FragmentAddNoteBinding
import com.maheshprajapati.myapplication.utility.AppConstants
import com.maheshprajapati.myapplication.utility.CommontMethods
import com.maheshprajapati.myapplication.viewmodels.NotesViewModel
import kotlinx.android.synthetic.main.fragment_add_note.*
import java.util.*

class AddNoteFragment : Fragment() {

    private val viewModel: NotesViewModel by lazy {
        ViewModelProviders.of(this).get(NotesViewModel::class.java)
    }

    lateinit var viewBinding: FragmentAddNoteBinding
    var date: String = "";
    var note: Note? = null
    var menuItem: Menu? = null

    lateinit var onBackFromDetailsScreen: AppConstants.OnBackFromDetailsScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(AppConstants.BundleConstants.NOTE_STRING)?.let {
            if (!it.isEmpty()) {
                note = Gson().fromJson(it, Note::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolBar()

        date = CommontMethods().getFormattedDateString(Date())
        tv_note_date.text = date
        if (arguments?.getBoolean(AppConstants.BundleConstants.IS_NOTES_EDITABLE)!!) {
            viewBinding.tvNoteDate.text = note!!.noteDate
            viewBinding.etNoteTitle.setText(note!!.noteTitle)
            viewBinding.etNoteComment.setText(note!!.noteComment)

        }

    }

    private fun setUpToolBar() {
        val upArrow: Drawable = resources.getDrawable(R.drawable.ic_arrow);
        (activity as AppCompatActivity).setSupportActionBar(viewBinding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setHomeAsUpIndicator(upArrow);
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        viewBinding.toolbar.setNavigationOnClickListener(View.OnClickListener {
            closeFragment()
        })
    }

    private fun addNotes(
        context: Context,
        date: String,
        title: String,
        comment: String
    ) {
        viewModel.addNotestoDB(context, date, title, comment)
        viewModel.getAddedNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            closeFragment()
            onBackFromDetailsScreen.onBackFromDetails(true)
        })
    }

    private fun closeFragment() {
        CommontMethods().hideKeyboard(activity!!)
        (activity as AppCompatActivity).onBackPressed()
        onBackFromDetailsScreen.onBackFromDetails(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (arguments?.getBoolean(AppConstants.BundleConstants.IS_NOTES_EDITABLE)!!) {
            inflater.inflate(R.menu.update_notes_menu, menu)
            menuItem = menu
            if (note!!.isPinned!!) {
                menu.findItem(R.id.action_to_pin).isVisible = false
                menu.findItem(R.id.action_to_unpin).isVisible = true
            } else {
                menu.findItem(R.id.action_to_pin).isVisible = true
                menu.findItem(R.id.action_to_unpin).isVisible = false
            }
        } else {
            inflater.inflate(R.menu.create_notes_menu, menu)
        }

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_save) {
            val title = et_note_title.text.toString()
            val comment = et_note_comment.text.toString()
            if (title.isEmpty()) {
                et_note_title.error = getString(R.string.add_note_title_error)
                et_note_title.requestFocus()
            } else {
                addNotes(activity!!, date, title, comment)
            }
        }

        if (item.itemId == R.id.action_to_unpin) {
            updatePintStatus(false)
        }

        if (item.itemId == R.id.action_to_pin) {
            updatePintStatus(true)
        }

        if (item.itemId == R.id.action_delete) {
            CommontMethods().showTwoButtonDialogWithCallBack(activity!!,
                getString(R.string.note_delete_message),
                getString(R.string.note_delete_cancel),
                getString(R.string.note_delete_delete),
                true, object : CommontMethods.OnTwoButtonDialogClickListener {
                    override fun onDialogPositiveButtonClick(dialog: Dialog?) {
                        deleteNote()
                    }

                    override fun onDialogNegativeButtonClick(dialog: Dialog?) {

                    }
                })
        }

        if (item.itemId == R.id.action_update) {
            val title = et_note_title.text.toString()
            val comment = et_note_comment.text.toString()
            if (title.isNullOrEmpty()) {
                et_note_title.setError(getString(R.string.add_note_title_error))
                et_note_title.requestFocus()
            } else {
                var noteUpdate: Note =
                    Note(note!!.noteId, title, comment, note!!.noteDate, note!!.isPinned)
                updateNote(noteUpdate)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateNote(updateNotes: Note) {
        viewModel.updateNotes(activity!!, updateNotes!!)
        viewModel.noteEditStatues.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            closeFragment()
            onBackFromDetailsScreen.onBackFromDetails(true)
        })
    }


    private fun updatePintStatus(isPinned: Boolean) {
        viewModel.updatePinStatus(activity!!, note!!, isPinned)
        viewModel.noteEditStatues.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (isPinned) {
                menuItem!!.findItem(R.id.action_to_pin).isVisible = false
                menuItem!!.findItem(R.id.action_to_unpin).isVisible = true
                CommontMethods().customToast(activity, getString(R.string.note_pinned))
            } else {
                menuItem!!.findItem(R.id.action_to_pin).isVisible = true
                menuItem!!.findItem(R.id.action_to_unpin).isVisible = false
                CommontMethods().customToast(activity, getString(R.string.note_unpinned))
            }
            onBackFromDetailsScreen.onBackFromDetails(true)
        })
    }



    private fun deleteNote() {
        viewModel.deleteNote(activity!!, note!!)
        viewModel.noteEditStatues.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            CommontMethods().customToast(activity, getString(R.string.note_deleted_successfully))
            closeFragment()
            onBackFromDetailsScreen.onBackFromDetails(true)
        })
    }


    fun intialiseRefreshInterface(onBackFromDetailsScreen: AppConstants.OnBackFromDetailsScreen) {
        this.onBackFromDetailsScreen = onBackFromDetailsScreen
    }


    companion object {
        fun newInstance(
            isEditable: Boolean, note
            : String
        ): AddNoteFragment {
            var fragment = AddNoteFragment()
            var bundle: Bundle = Bundle()
            bundle.putBoolean(AppConstants.BundleConstants.IS_NOTES_EDITABLE, isEditable)
            bundle.putString(AppConstants.BundleConstants.NOTE_STRING, note)
            fragment.arguments = bundle
            return fragment
        }
    }
}