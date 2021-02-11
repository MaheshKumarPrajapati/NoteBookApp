package com.maheshprajapati.myapplication.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.adaptors.NoteListAdapter
import com.maheshprajapati.myapplication.database.Note
import com.maheshprajapati.myapplication.databinding.FragmentSearchBinding
import com.maheshprajapati.myapplication.utility.AppConstants
import com.maheshprajapati.myapplication.utility.CommontMethods
import com.maheshprajapati.myapplication.viewmodels.NotesViewModel
import java.lang.Exception


class SearchFragment : Fragment(), AppConstants.OnBackFromDetailsScreen {

    private val viewModel: NotesViewModel by lazy {
        ViewModelProviders.of(this).get(NotesViewModel::class.java)
    }

    lateinit var viewBinding: FragmentSearchBinding
    private var noteList: ArrayList<Note>? = null
    private var noteListFilter: ArrayList<Note>? = null
    lateinit var adapterSearch: NoteListAdapter
    private lateinit var onBackFromDetailsScreenMain: AppConstants.OnBackFromDetailsScreen
    lateinit var onBackFromDetailsScreenTwo: AppConstants.OnBackFromDetailsScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val note = it.getString(AppConstants.BundleConstants.NOTE_STRING)
            noteList = Gson().fromJson(
                note,
                object : TypeToken<List<Note?>?>() {}.type
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        viewBinding = FragmentSearchBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setUpToolBar()
        adapterSearch = NoteListAdapter()
        onBackFromDetailsScreenTwo = this
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        viewBinding.recyclerViewSearch.layoutManager = staggeredGridLayoutManager;
        viewBinding.recyclerViewSearch.adapter = adapterSearch
        checkFilteredNotes()
    }

    private fun checkFilteredNotes() {
        viewBinding.etSerchNote.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                getFilterList()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    private fun getFilterList() {
        try {
            if (viewBinding.etSerchNote.text.toString().isEmpty()) {
                adapterSearch.setNoteList(activity!!, ArrayList<Note>(), onBackFromDetailsScreenTwo)
                viewBinding.tvSearchNoResult.visibility = View.VISIBLE
            } else {
                viewModel.getFilterList(activity!!, viewBinding.etSerchNote.text.toString())
                viewModel.getFilterNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
                    noteListFilter = it
                    adapterSearch.setNoteList(
                        activity!!,
                        noteListFilter,
                        onBackFromDetailsScreenTwo
                    )
                    viewBinding.tvSearchNoResult.visibility = View.GONE
                })
            }
        } catch (e: Exception) {
        }

    }

    private fun setUpToolBar() {
        val back: Drawable = getResources().getDrawable(R.drawable.ic_arrow);
        (activity as AppCompatActivity).setSupportActionBar(viewBinding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setHomeAsUpIndicator(back);
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        viewBinding.toolbar.setNavigationOnClickListener(View.OnClickListener {
            closeFragment()
        })
    }

    private fun closeFragment() {
        CommontMethods().hideKeyboard(activity!!)
        (activity as AppCompatActivity).onBackPressed()
        onBackFromDetailsScreenMain.onBackFromDetails(true)
    }

    fun intialiseRefreshInterface(onBackFromDetailsScreen: AppConstants.OnBackFromDetailsScreen) {
        this.onBackFromDetailsScreenMain = onBackFromDetailsScreen
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(AppConstants.BundleConstants.NOTE_STRING, param1)
                }
            }
    }

    override fun onBackFromDetails(isSuccess: Boolean) {
        getFilterList()
        onBackFromDetailsScreenMain.onBackFromDetails(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.search_fragment_menu, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_clear) {
            viewBinding.etSerchNote.setText("")
            adapterSearch.setNoteList(activity!!, ArrayList<Note>(), onBackFromDetailsScreenTwo)
            viewBinding.tvSearchNoResult.visibility = View.VISIBLE
        }
        return super.onOptionsItemSelected(item)
    }
}