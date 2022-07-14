package com.ikhokha.techcheck

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationItemView
import com.ikhokha.common.base.fragment.TopNavigationFragment
import com.google.firebase.FirebaseApp
import com.ikhokha.common.base.fragment.BaseFragment
import com.ikhokha.common.extensions.setupWithCustomAnimNavController
import com.ikhokha.common.interfaces.MyDrawerController
import com.ikhokha.common.models.NavMenuItem
import com.ikhokha.features.cart.CartFragmentDirections
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
        initBottomNavBar()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        currentFragment?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun initBottomNavBar() {
        val navMenuItems = arrayListOf(
            NavMenuItem(0, R.id.scanFragment),
            NavMenuItem(1, R.id.cartFragment)
        )
        this.navMenuItems  = navMenuItems

        bnBottomNav.setupWithCustomAnimNavController(this, navController, navMenuItems)
    }

    override fun onBackNav() {
        onBackPressed()
    }

    override fun popBack() {
        navController.popBackStack()
    }

    override fun topNavBackClicked() {
        onBackPressed()
    }

    override fun exitApp() {
        finish()
    }

    override fun showBadge(value: String) {
        val itemId = R.id.cartFragment
        bnBottomNav?.findViewById<BottomNavigationItemView?>(itemId)?.let { itemView ->
            val badge: View = LayoutInflater.from(this)
                .inflate(com.ikhokha.features.common.R.layout.badge_layout, bnBottomNav, false)
            val text: TextView = badge.findViewById(com.ikhokha.features.common.R.id.badge_text_view)
            text.text = value
            itemView.addView(badge)
        }
    }

    override fun removeBadge() {
        val itemId = R.id.cartFragment
        bnBottomNav?.findViewById<BottomNavigationItemView?>(itemId)?.let { itemView ->
            if (itemView.childCount == 3) {
                itemView.removeViewAt(2)
            }
        }
    }

    override fun showBottomNav() {
        bnBottomNav?.let {
            it.visibility = View.VISIBLE

            it.animate()
                .translationY(0f)
                .setDuration(0)
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
        navMenuItems?.first()?.let { home ->
            when (bnBottomNav.selectedItemId) {
                home.fragment -> finish()
                else -> bnBottomNav.selectedItemId = home.fragment
            }
        }
    }

    override fun navigateFromScannerToPreview(productId: String) {
        val action = ScanFragmentDirections.actionScanFragmentToPreviewFragment(productId)
        navController.navigate(action)
    }

    override fun navigateFromCartToSummary() {
        val action = CartFragmentDirections.actionCartFragmentToSummaryFragment()
        navController.navigate(action)
    }

    override fun navigateFromCartToPreview(productId: String) {
        val action = CartFragmentDirections.actionCartFragmentToPreviewFragment(productId)
        navController.navigate(action)
    }

    override fun lastViewDestroyed(baseFragment: BaseFragment) {
        currentFragment?.onTransitionAnimationComplete(baseFragment)
    }

    override fun onAttachFragment(fragment: Fragment?) {
        super.onAttachFragment(fragment)
        currentFragment?.onTransitionAnimationComplete(null)
    }

    override fun onBackPressed() {
        when (currentFragment is TopNavigationFragment) {
            true -> handleTopNavigation()
            false -> currentFragment?.onBackPressed()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            currentFragment?.onHardwareBackPressed()

            when (currentFragment is TopNavigationFragment) {
                true -> handleTopNavigation()
                else -> { /* No opp */ }
            }
        }

        return false
    }
}