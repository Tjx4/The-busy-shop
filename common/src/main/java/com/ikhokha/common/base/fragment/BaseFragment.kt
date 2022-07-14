package com.ikhokha.common.base.fragment

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ikhokha.common.R
import com.ikhokha.common.helpers.areAllPermissionsGranted
import com.ikhokha.common.helpers.requestNotGrantedPermissions
import com.ikhokha.common.helpers.showConfirmDialog
import com.ikhokha.common.interfaces.MyDrawerController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

abstract class BaseFragment : Fragment() {
    protected lateinit var drawerController: MyDrawerController
    protected open var PERMISSION_REQUEST_CODE: Int = 0
    protected open var PERMISSIONS: Array<String>? = null

    protected val allPermissionsGranted: Boolean
        get() = PERMISSIONS?.let { areAllPermissionsGranted(requireContext(), it) } == true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        drawerController = context as MyDrawerController
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        drawerController.hideBottomNav()

        PERMISSIONS?.let {
            checkPermissions()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        drawerController.currentFragment = this
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

    open fun onTransitionAnimationComplete(oldFragment: BaseFragment?) {
        /* No op */
    }

    protected fun requestPermissions() {
        PERMISSIONS?.let {
            requestNotGrantedPermissions(
                requireActivity() as AppCompatActivity,
                it,
                PERMISSION_REQUEST_CODE
            )
        }
    }

    protected fun checkPermissions() {
        when (allPermissionsGranted) {
            true -> onPermissionsGranted()
            else -> requestPermissions()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode == PERMISSION_REQUEST_CODE && allPermissionsGranted) {
            true -> onPermissionsGranted()
            else -> onPermissionDeclined()
        }
    }

    protected fun showPermissionDialog(heading: String, message: String, permissions: String) {
        showConfirmDialog(
            requireContext(),
            heading,
            message,
            getString(R.string.request),
            getString(R.string.close),
            {
                requestPermissionsFromSettings(permissions)
            },
            {
                requireActivity().finish()
            }
        )
    }

    protected fun requestPermissionsFromSettings(permissions: String) {
        val i = Intent()
        i.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        i.addCategory(Intent.CATEGORY_DEFAULT)
        i.data = Uri.parse("package:" + requireContext().packageName)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS)
        requireContext().startActivity(i)
    }

    open fun onPermissionsGranted() {
        /* No op */
    }

    open fun onPermissionDeclined() {
        /* No op */
    }

}