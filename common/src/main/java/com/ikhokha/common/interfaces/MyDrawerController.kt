package com.ikhokha.common.interfaces

import androidx.navigation.NavController
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.models.Product

interface MyDrawerController {
    var currentFragment: BaseFragment?
    var navController: NavController

    fun initBottomNavBar()
    fun onBackNav()
    fun popBack()
    fun topNavBackClicked()
    fun exitApp()
    fun showBadge(value: String)
    fun removeBadge()
    fun showBottomNav()
    fun hideBottomNav()
    fun navigateFromScannerToPreview(productId: String)
    fun navigateFromCartToSummary()
    fun navigateFromCartToPreview(productId: String)
    fun lastViewDestroyed(baseFragment: BaseFragment)
}