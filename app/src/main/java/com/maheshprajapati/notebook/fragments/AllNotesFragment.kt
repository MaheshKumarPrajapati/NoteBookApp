package com.maheshprajapati.myapplication.fragments

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.maheshprajapati.myapplication.R
import com.maheshprajapati.myapplication.adaptors.NoteListAdapter
import com.maheshprajapati.myapplication.database.Note
import com.maheshprajapati.myapplication.databinding.FragmentAllNotesBinding
import com.maheshprajapati.myapplication.utility.AppConstants
import com.maheshprajapati.myapplication.utility.CommontMethods
import com.maheshprajapati.myapplication.utility.HelperClass
import com.maheshprajapati.myapplication.viewmodels.NotesViewModel


class AllNotesFragment : Fragment(), AppConstants.OnBackFromDetailsScreen {

    private val viewModel: NotesViewModel by lazy {
        ViewModelProviders.of(this).get(NotesViewModel::class.java)
    }

    private var _viewBinding: FragmentAllNotesBinding? = null
    private val viewBinding get() = _viewBinding!!
    lateinit var adapterPinned: NoteListAdapter
    lateinit var adapterUnPinned: NoteListAdapter
    lateinit var helperClass: HelperClass
    var noteList: List<Note> = ArrayList<Note>()
    lateinit var refreshAfterDetailsPageInterface: AppConstants.OnBackFromDetailsScreen


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        _viewBinding = FragmentAllNotesBinding.inflate(inflater, container, false)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        refreshAfterDetailsPageInterface = this
        helperClass = HelperClass(activity!!)
        helperClass.savePreferences(helperClass.MINUTES_TO_LOCK, "2")

        setUpToolBar()

        view.findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            var fragment: AddNoteFragment = AddNoteFragment.newInstance(false, "")
            fragment.intialiseRefreshInterface(refreshAfterDetailsPageInterface)
            val activity = context as FragmentActivity
            val ft = activity.supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame_container, fragment)
            ft.addToBackStack(null)
            ft.commitAllowingStateLoss()
        }
    }


    private fun getAllNotes() {
        viewModel.getNotesFromDB(activity!!)
        viewModel.getAllNotes.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            viewBinding.tvNoteCount.text = "" + it.size + " " + getString(R.string.note_main_notes)
            noteList = it
            filterPinList(it)
            if (it.isNotEmpty()) {
                viewBinding.tvMainNoResult.visibility = View.GONE
            } else {
                viewBinding.tvMainNoResult.visibility = View.VISIBLE
            }
        })
    }


    private fun filterPinList(it: List<Note>?) {

        adapterPinned = NoteListAdapter()
        val staggeredGridLayoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        viewBinding.recyclerViewPinned.layoutManager = staggeredGridLayoutManager;
        viewBinding.recyclerViewPinned.adapter = adapterPinned
        viewBinding.recyclerViewPinned.isNestedScrollingEnabled = false

        adapterUnPinned = NoteListAdapter()
        val staggeredGridLayoutManager2 = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        viewBinding.recyclerViewUnPinned.layoutManager = staggeredGridLayoutManager2;
        viewBinding.recyclerViewUnPinned.adapter = adapterUnPinned
        viewBinding.recyclerViewUnPinned.isNestedScrollingEnabled = false

        var pinedList = ArrayList<Note>()
        var unpinedList = ArrayList<Note>()

        for (item in it!!) {
            if (item.isPinned!!) {
                pinedList.add(item)
            } else {
                unpinedList.add(item)
            }
        }

        if (pinedList.size > 0) {
            viewBinding.recyclerViewPinned.visibility = View.VISIBLE
            viewBinding.divider.visibility = View.VISIBLE
            adapterPinned.setNoteList(activity!!, pinedList, refreshAfterDetailsPageInterface)
        } else {
            viewBinding.recyclerViewPinned.visibility = View.GONE
            viewBinding.divider.visibility = View.GONE
        }

        adapterUnPinned.setNoteList(activity!!, unpinedList, refreshAfterDetailsPageInterface)
    }

    private fun setUpToolBar() {

        val menu: Drawable = resources.getDrawable(R.drawable.ic_menu);
        (activity as AppCompatActivity).setSupportActionBar(viewBinding.toolbar)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        (activity as AppCompatActivity).getSupportActionBar()!!.setHomeAsUpIndicator(menu);
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowHomeEnabled(true)
        viewBinding.toolbar.setNavigationOnClickListener(View.OnClickListener {
            viewBinding.drawerLayout.openDrawer(viewBinding.navView)
        })

        val switch: Switch = viewBinding.navView.getHeaderView(0).findViewById(R.id.nav_switch)
        val close: ImageView = viewBinding.navView.getHeaderView(0).findViewById(R.id.nav_close)
        if (helperClass.loadBoolPreferences(helperClass.IS_APP_LOCK_ACTIVE)) {
            switch.isChecked = true
        }

        switch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isPressed) {
                helperClass.saveBoolPreferences(helperClass.IS_APP_LOCK_ACTIVE, isChecked)
                CommontMethods().hideKeyboard(activity!!)
                helperClass.savePreferences(helperClass.MINUTES_TO_LOCK, "2")
                CommontMethods().customToast(activity!!, getString(R.string.app_unlock_change))
            }
        }

        close.setOnClickListener { viewBinding.drawerLayout.closeDrawer(GravityCompat.START) }

    }

    override fun onResume() {
        super.onResume()
        getAllNotes()
    }

    override fun onBackFromDetails(isSuccess: Boolean) {
        getAllNotes()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.main_fregment, menu)

        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_search) {
            val note = Gson().toJson(noteList)
            var fragment: SearchFragment =
                SearchFragment.newInstance(
                    note
                )
            fragment.intialiseRefreshInterface(refreshAfterDetailsPageInterface)
            val activity = context as FragmentActivity
            val ft = activity.supportFragmentManager.beginTransaction()
            ft.replace(R.id.frame_container, fragment)
            ft.addToBackStack(null)
            ft.commitAllowingStateLoss()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}
