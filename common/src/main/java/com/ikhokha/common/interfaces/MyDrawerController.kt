package com.ikhokha.common.interfaces

import androidx.navigation.NavController
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.models.Product

interface MyDrawerController {
    var currentFragment: BaseFragment?
    var navController: NavController

    fun initBottomNavBar()
    fun onBackNav()
    fun popAll()
    fun popBack()
    fun exitApp()
    fun showBottomNav()
    fun hideBottomNav()
    //navigation
    fun navigateFromPreviewnerToPreview(product: Product)
}