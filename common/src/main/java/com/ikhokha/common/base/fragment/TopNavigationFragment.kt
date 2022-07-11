package com.ikhokha.common.base.fragment

abstract class TopNavigationFragment : BaseFragment() {
    override fun onBackPressed() {
        drawerController.topNavBackClicked()
    }
}