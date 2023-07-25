package com.example.desiredvacationsapp.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.desiredvacationsapp.*
import com.example.desiredvacationsapp.appDatabase.VacationDatabase
import com.example.desiredvacationsapp.databinding.FragmentVacationDetailsBinding
import com.example.desiredvacationsapp.interfaces.NavigationListener
import com.example.desiredvacationsapp.models.Vacation
import com.example.desiredvacationsapp.viewmodel.VacationViewModel
import com.example.desiredvacationsapp.viewmodel.VacationViewModelFactory
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.File

class VacationDetailFragment : Fragment() {

    private lateinit var viewModel: VacationViewModel
    private var _binding: FragmentVacationDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var vacation: Vacation

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVacationDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        handleArgumentsAndRetrieveVacation()
    }

    // Set up the view model for this fragment.
    private fun setupViewModel() {
        val db = VacationDatabase.getDatabase(requireActivity().applicationContext)
        viewModel = ViewModelProvider(requireActivity(), VacationViewModelFactory(db.vacationDao()))[VacationViewModel::class.java]
    }

    // Handle the arguments passed to this fragment and retrieve the corresponding vacation.
    private fun handleArgumentsAndRetrieveVacation() {
        val id = arguments?.getInt(ARG_VACATION_ID) ?: -1
        if (id > 0) {
            viewModel.retrieveVacation(id).observe(this.viewLifecycleOwner) { selectedItem ->
                vacation = selectedItem
                bind(vacation)
            }
        }
    }

    // Bind the vacation data to the views.
    private fun bind(vacation: Vacation) {
        binding.apply {
            // Update the UI elements with the vacation data.
            vacationName.text = vacation.name
            vacationHotelName.text = vacation.hotelName
            vacationDescription.text = vacation.description
            moneyNecessary.text = vacation.necessaryMoneyAmount.toString()
            vacationLocation.text = vacation.location

            // Set onClickListeners for the delete and edit buttons.
            deleteVacation.setOnClickListener { showConfirmationDialog() }
            editVacation.setOnClickListener { navigateToEditVacationFragment() }

            // Load the image into the ImageView using Glide.
            Glide.with(this@VacationDetailFragment)
                .load(vacation.imageName) // loading from a local file
                .into(hotelImage)
        }
    }

    // Navigate to the AddVacationFragment for editing the current vacation.
    private fun navigateToEditVacationFragment() {
        val fragment = AddVacationFragment()
        val bundle = Bundle()
        bundle.putInt(ARG_VACATION_ID, vacation.id)
        bundle.putString(ARG_VACATION_NAME, getString(R.string.edit_fragment_title))
        fragment.arguments = bundle
        (activity as? MainActivity)?.replaceFragment(fragment)
    }

    // Show a confirmation dialog before deleting the vacation.
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteVacation()
            }
            .show()
    }

    // Delete the vacation from the database and navigate back to the vacation list.
    private fun deleteVacation() {
        viewModel.deleteVacation(vacation)
        (activity as? NavigationListener)?.navigateToVacationFragmentList()
    }

    // Clean up when the view is destroyed.
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}