package com.example.desiredvacationsapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.desiredvacationsapp.*
import com.example.desiredvacationsapp.adapters.VacationAdapter
import com.example.desiredvacationsapp.appDatabase.VacationDatabase
import com.example.desiredvacationsapp.databinding.FragmentVacationListBinding
import com.example.desiredvacationsapp.notifications.NotificationSetupFragment
import com.example.desiredvacationsapp.viewmodel.VacationViewModel
import com.example.desiredvacationsapp.viewmodel.VacationViewModelFactory

class VacationFragmentList : Fragment() {

    private lateinit var viewModel: VacationViewModel
    private var _binding: FragmentVacationListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVacationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up the view model for this fragment.
        setupViewModel()
        // Set up the recycler view for this fragment.
        setupRecyclerView()
        // Observe the vacations to update the recycler view.
        observeAllVacations()
        // Set up the notification button.
        setupNotificationButton(view)
        // Set up the floating action button.
        setupFloatingActionButton()
    }

    // Set up the view model.Instantiate the view model for this fragment.
    private fun setupViewModel() {
        val db = VacationDatabase.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), VacationViewModelFactory(db.vacationDao()))[VacationViewModel::class.java]
    }

    //Set up the recycler view, including setting the layout manager and the adapter.
    private fun setupRecyclerView() {
        val adapter = VacationAdapter { vacation ->
            // When a vacation item is clicked, navigate to the detail view for that vacation.
            navigateToVacationDetailFragment(vacation.id)
        }
        binding.recyclerViewVacations.layoutManager = LinearLayoutManager(this.context)
        binding.recyclerViewVacations.adapter = adapter
    }

    //Navigating to the VacationDetailFragment when a vacation item is clicked.
    private fun navigateToVacationDetailFragment(id: Int) {
        val fragment = VacationDetailFragment()
        val bundle = Bundle()
        bundle.putInt(ARG_VACATION_ID, id)
        fragment.arguments = bundle

        (activity as MainActivity).replaceFragment(fragment)
    }

    //Observing the data of all vacations and updating the list in the recycler view.
    private fun observeAllVacations() {
        viewModel.allVacations.observe(this.viewLifecycleOwner) { items ->
            items.let {
                (binding.recyclerViewVacations.adapter as VacationAdapter).submitList(it)
            }
        }
    }

    //Set up the notification button, including setting the click listener.
    private fun setupNotificationButton(view: View) {
        val clockIcon = view.findViewById<ImageButton>(R.id.notification_button)

        // Set a click listener for the clock icon
        clockIcon.setOnClickListener {
            // When the clock icon is clicked, navigate to the NotificationSetupFragment
            navigateToNotificationSetupFragment()
        }
    }

    //Navigating to the NotificationSetupFragment when the notification button is clicked.
    private fun navigateToNotificationSetupFragment() {
        val notificationSetupFragment = NotificationSetupFragment()
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, notificationSetupFragment)
            .addToBackStack(null)
            .commit()
    }

    //Set up the floating action button, including setting the click listener.
    private fun setupFloatingActionButton() {
        binding.floatingActionButton.setOnClickListener {
            // When the floating action button is clicked, navigate to the AddVacationFragment
            navigateToAddVacationFragment()
        }
    }

    //Navigating to the AddVacationFragment when the floating action button is clicked.
    private fun navigateToAddVacationFragment() {
        val fragment = AddVacationFragment()
        val bundle = Bundle()
        bundle.putString(ARG_VACATION_NAME, getString(R.string.add_vacation_name))
        fragment.arguments = bundle
        (activity as? MainActivity)?.replaceFragment(fragment)
    }

    // Clean up when the view is destroyed, to avoid memory leaks.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}