package com.example.desiredvacationsapp.interfaces

interface NavigationListener {
    fun navigateToVacationFragmentList()
    fun navigateToAddVacationFragment(name: String)
    fun navigateToVacationDetailFragment(id: Int)
}