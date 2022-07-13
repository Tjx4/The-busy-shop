package com.ikhokha.common.extensions

import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.ikhokha.common.R
import com.ikhokha.common.interfaces.MyDrawerController
import com.ikhokha.common.models.NavMenuItem

fun BottomNavigationView.setupWithCustomAnimNavController(myDrawerController: MyDrawerController, navController: NavController, navMenuItems: List<NavMenuItem?>) {
    var lastItemIndex = 0

    this.setOnNavigationItemSelectedListener { item ->
        val enterLeftOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.no_transition)
            .setPopEnterAnim(R.anim.no_transition)
            .setPopExitAnim(R.anim.no_transition)
            .setPopUpTo(navController.graph.startDestination, false)
            .build()

        val enterRightOption = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setEnterAnim(R.anim.fade_in)
            .setExitAnim(R.anim.no_transition)
            .setPopEnterAnim(R.anim.no_transition)
            .setPopEnterAnim(R.anim.no_transition)
            .setPopUpTo(navController.graph.startDestination, false)
            .build()

        navMenuItems.first { it?.fragment == item?.itemId }?.let { screen ->
            val itemIndex = screen.index
            val navigationOptions =
                if (itemIndex >= lastItemIndex) enterLeftOptions else enterRightOption
            navController.navigate(screen.fragment, null, navigationOptions)
            lastItemIndex = itemIndex
        }

        true
    }

    this.setOnNavigationItemReselectedListener { item ->
        when (myDrawerController.currentFragment is TopNavigationFragment) {
            true -> { /* No op */
            }
            else -> myDrawerController.onBackNav()
        }

        return@setOnNavigationItemReselectedListener
    }

}