package com.example.desiredvacationsapp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

object Utils {

    fun commitFragment(
        fragmentManager: FragmentManager,
        containerViewId: Int,
        fragment: Fragment,
        addToBackStack: Boolean = false,
        backStackName: String? = null
    ) {
        val transaction = fragmentManager.beginTransaction()

        transaction.replace(containerViewId, fragment)

        if (addToBackStack) {
            transaction.addToBackStack(backStackName)
        }

        transaction.commit()
    }
}




