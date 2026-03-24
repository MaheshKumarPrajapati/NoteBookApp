package com.maheshprajapati.notebook.fragments

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.maheshprajapati.notebook.R
import com.maheshprajapati.notebook.database.Note
import com.maheshprajapati.notebook.databinding.FragmentAddNoteBinding
import com.maheshprajapati.notebook.utility.AppConstants
import com.maheshprajapati.notebook.utility.CommontMethods
import com.maheshprajapati.notebook.viewmodels.NotesViewModel
import java.util.*

class AddNoteFragment : Fragment() {

    private val viewModel: NotesViewModel by lazy {
        ViewModelProvider(this).get(NotesViewModel::class.java)
    }

    private var _viewBinding: FragmentAddNoteBinding? = null
    private val viewBinding get() = _viewBinding!!
    var date: String = ""
    var note: Note? = null
    var menuItem: Menu? = null

    lateinit var onBackFromDetailsScreen: AppConstants.OnBackFromDetailsScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getString(AppConstants.BundleConstants.NOTE_STRING)?.let {
            if (it.isNotEmpty()) {
                note = Gson().fromJson(it, Note::class.java)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _viewBinding = FragmentAddNoteBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpToolBar()

        date = CommontMethods().getFormattedDateString(Date())
        viewBinding.tvNoteDate.text = date
        if (arguments?.getBoolean(AppConstants.BundleConstants.IS_NOTES_EDITABLE) == true) {
            viewBinding.tvNoteDate.text = note!!.noteDate
            viewBinding.etNoteTitle.setText(note!!.noteTitle)
            viewBinding.etNoteComment.setText(note!!.noteComment)

        }

    }

    private fun setUpToolBar() {
        val upArrow: Drawable? = androidx.core.content.res.ResourcesCompat.getDrawable(resources, R.drawable.ic_arrow, null)
        (activity as AppCompatActivity).setSupportActionBar(viewBinding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).supportActionBar?.setHomeAsUpIndicator(upArrow)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        viewBinding.toolbar.setNavigationOnClickListener {
            closeFragment()
        }
    }

    private fun addNotes(
        context: Context,
        date: String,
        title: String,
        comment: String
    ) {
        viewModel.addNotestoDB(context, date, title, comment)
        viewModel.getAddedNotes.observe(viewLifecycleOwner) {
            closeFragment()
            onBackFromDetailsScreen.onBackFromDetails(true)
        }
    }

    private fun closeFragment() {
        CommontMethods().hideKeyboard(requireActivity())
        requireActivity().onBackPressed()
        onBackFromDetailsScreen.onBackFromDetails(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        if (arguments?.getBoolean(AppConstants.BundleConstants.IS_NOTES_EDITABLE) == true) {
            inflater.inflate(R.menu.update_notes_menu, menu)
            menuItem = menu
            if (note!!.isPinned == true) {
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
            val title = viewBinding.etNoteTitle.text.toString()
            val comment = viewBinding.etNoteComment.text.toString()
            if (title.isEmpty()) {
                viewBinding.etNoteTitle.error = getString(R.string.add_note_title_error)
                viewBinding.etNoteTitle.requestFocus()
            } else {
                addNotes(requireActivity(), date, title, comment)
            }
        }

        if (item.itemId == R.id.action_to_unpin) {
            updatePinStatus(false)
        }

        if (item.itemId == R.id.action_to_pin) {
            updatePinStatus(true)
        }

        if (item.itemId == R.id.action_delete) {
            CommontMethods().showTwoButtonDialogWithCallBack(requireActivity(),
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
            val title = viewBinding.etNoteTitle.text.toString()
            val comment = viewBinding.etNoteComment.text.toString()
            if (title.isEmpty()) {
                viewBinding.etNoteTitle.error = getString(R.string.add_note_title_error)
                viewBinding.etNoteTitle.requestFocus()
            } else {
                val noteUpdate =
                    Note(note!!.noteId, title, comment, note!!.noteDate, note!!.isPinned)
                updateNote(noteUpdate)
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun updateNote(updateNotes: Note) {
        viewModel.updateNotes(requireActivity(), updateNotes)
        viewModel.noteEditStatues.observe(viewLifecycleOwner) {
            closeFragment()
            onBackFromDetailsScreen.onBackFromDetails(true)
        }
    }


    private fun updatePinStatus(isPinned: Boolean) {
        viewModel.updatePinStatus(requireActivity(), note!!, isPinned)
        viewModel.noteEditStatues.observe(viewLifecycleOwner) {
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
        }
    }



    private fun deleteNote() {
        viewModel.deleteNote(requireActivity(), note!!)
        viewModel.noteEditStatues.observe(viewLifecycleOwner) {
            CommontMethods().customToast(activity, getString(R.string.note_deleted_successfully))
            closeFragment()
            onBackFromDetailsScreen.onBackFromDetails(true)
        }
    }


    fun intialiseRefreshInterface(onBackFromDetailsScreen: AppConstants.OnBackFromDetailsScreen) {
        this.onBackFromDetailsScreen = onBackFromDetailsScreen
    }


    companion object {
        fun newInstance(
            isEditable: Boolean, note: String
        ): AddNoteFragment {
            val fragment = AddNoteFragment()
            val bundle = Bundle()
            bundle.putBoolean(AppConstants.BundleConstants.IS_NOTES_EDITABLE, isEditable)
            bundle.putString(AppConstants.BundleConstants.NOTE_STRING, note)
            fragment.arguments = bundle
            return fragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}
