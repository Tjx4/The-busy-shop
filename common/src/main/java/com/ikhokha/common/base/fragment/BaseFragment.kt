package com.ikhokha.common.base.fragment

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ikhokha.common.interfaces.MyDrawerController

abstract class BaseFragment : Fragment() {
    protected lateinit var drawerController: MyDrawerController

    override fun onAttach(context: Context) {
        super.onAttach(context)
        drawerController = context as MyDrawerController
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawerController.hideBottomNav()
    }

    override fun onResume() {
        super.onResume()
        drawerController.currentFragment = this
    }

    open fun onHardwareBackPressed() {
        /* No op */
    }

    open fun onBackPressed() {
        drawerController.exitApp()
    }

   override fun onDestroyView() {
        super.onDestroyView()
       drawerController.lastViewDestroyed(this)
    }

    open fun onTransitionAnimationComplete(oldFragment: BaseFragment) {
        val dd = 0
        /* No op */
    }
}