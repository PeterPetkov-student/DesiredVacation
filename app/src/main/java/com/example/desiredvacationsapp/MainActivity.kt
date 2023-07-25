package com.example.desiredvacationsapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelStoreOwner
import com.example.desiredvacationsapp.interfaces.NavigationListener
import com.example.desiredvacationsapp.ui.AddVacationFragment
import com.example.desiredvacationsapp.ui.VacationDetailFragment
import com.example.desiredvacationsapp.ui.VacationFragmentList

const val ARG_VACATION_ID = "item_id"
const val ARG_VACATION_NAME = "fragment_title"

class MainActivity : AppCompatActivity(R.layout.activity_main), NavigationListener,
    ViewModelStoreOwner {

    private lateinit var fragmentManager: FragmentManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentManager = supportFragmentManager
        if (savedInstanceState == null) {
            // If there is no saved state, add the VacationFragmentList as the initial fragment
            val vacationListFragment = VacationFragmentList()
            replaceFragment(vacationListFragment, false)
        }
    }

    fun replaceFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    override fun navigateToVacationFragmentList() {
        val vacationListFragment = VacationFragmentList()
        replaceFragment(vacationListFragment)
    }

    override fun navigateToAddVacationFragment(name: String) {
        val fragment = AddVacationFragment()
        val bundle = Bundle()
        bundle.putString(ARG_VACATION_NAME, name)
        fragment.arguments = bundle

        replaceFragment(fragment, true)
    }

    override fun navigateToVacationDetailFragment(id: Int) {
        val fragment = VacationDetailFragment()
        val bundle = Bundle()
        bundle.putInt(ARG_VACATION_ID, id)
        fragment.arguments = bundle

        replaceFragment(fragment, true)
    }
}