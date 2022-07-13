package com.ikhokha.common.base.fragment

abstract class SubNavigationFragment : BaseFragment() {

    override fun onBackPressed() {
        drawerController.popBack()
    }

    override fun onHardwareBackPressed() {
        drawerController.popBack()
    }
}