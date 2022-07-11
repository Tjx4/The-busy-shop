package com.ikhokha.techcheck

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.google.firebase.FirebaseApp
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.extensions.setupWithCustomAnimNavController
import com.ikhokha.common.interfaces.MyDrawerController
import com.ikhokha.common.models.NavMenuItem
import com.ikhokha.features.scan.ScanFragmentDirections
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), MyDrawerController {
    override var currentFragment: BaseFragment? = null
    override lateinit var navController: NavController
    private var navMenuItems: List<NavMenuItem?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)
        navController = findNavController(R.id.navControllerFragment)

        setSupportActionBar(toolbar)
        initBottomNavBar()
    }

    override fun initBottomNavBar() {
        val navMenuItems = arrayListOf<NavMenuItem>(
            NavMenuItem(0, R.id.scanFragment),
            NavMenuItem(1, R.id.cartFragment)
        )
        this.navMenuItems  = navMenuItems

        bnBottomNav.setupWithCustomAnimNavController(this, navController, navMenuItems)
    }

    override fun onBackNav() {
        onBackPressed()
    }

    override fun popAll() {
        navController.popBackStack()
    }

    override fun popBack() {
        navController.popBackStack()
    }

    override fun exitApp() {
        finish()
    }


    override fun showBottomNav() {
        bnBottomNav?.let {
            it.visibility = View.VISIBLE

            it.animate()
                .translationY(0f)
                //.alpha(1f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                    }
                })
        }
    }

    override fun hideBottomNav() {
        bnBottomNav?.let {
            it.animate()
                .translationY(it.height.toFloat())
                //.alpha(0.0f)
                .setDuration(200)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        it.visibility = View.GONE
                    }
                })
        }
    }

    fun handleTopNavigation() {
        navMenuItems?.get(0)?.let { home ->
            when (bnBottomNav.selectedItemId) {
                home.fragment -> finish()
                else -> bnBottomNav.selectedItemId = home.fragment
            }
        }
    }

    override fun navigateFromPreviewnerToPreview(productId: String) {
        val action = ScanFragmentDirections.actionScanFragmentToPreviewFragment(productId)
        navController.navigate(action)
    }

    override fun onBackPressed() {
        when (currentFragment is TopNavigationFragment) {
            true -> handleTopNavigation()
            false -> currentFragment?.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            when (currentFragment is TopNavigationFragment) {
                true -> handleTopNavigation()
                else -> currentFragment?.onBackPressed()
            }
        }

        return false
    }
}