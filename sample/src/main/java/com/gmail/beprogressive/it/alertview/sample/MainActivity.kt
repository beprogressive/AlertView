package com.gmail.beprogressive.it.alertview.sample

import android.os.Bundle
import android.view.Gravity
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.transition.Slide
import com.gmail.beprogressive.it.alertview.log
import com.gmail.beprogressive.it.alertview.sample.databinding.ActivityMainBinding
import com.gmail.beprogressive.it.slidingactivity.SlidingActivity

class MainActivity :
//    AppCompatActivity() {
    SlidingActivity() {

    private lateinit var binding: ActivityMainBinding

    private var alertTouch = false
//    var mSlidingView: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this, MainViewModelFactory()).get(
            MainViewModel::class.java
        )

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.activity = this

        binding.alertView.setAlertTouchListener {
            alertTouch = it
        }

//        binding.alertView.setAlertClickListener { alertView, message, collapsed ->
//            Toast.makeText(this, "Alert click!", Toast.LENGTH_SHORT).show()
//        }

        subscribeToModel()
    }

    override fun getSlidingContainer(): View {
        return binding.slidingRoot
    }

    override fun onSlidingFinished() {
    }

    override fun onSlidingStarted() {
    }

    override fun canSlideRight(): Boolean {
        return !alertTouch
    }

    private fun subscribeToModel() {
        binding.viewModel?.enableButton?.observe(this) {
            if (it)
                binding.alertView.setButtonClickListener { alertView, _ ->

                    alertView.switchVisibilityAlertView(
                        false, Slide(
                            Gravity.END
                        )
                    )
                }
            else
                binding.alertView.setButtonClickListener(null)
        }
    }

    fun onButtonClick() {
        log("onButtonClick")
        binding.viewModel!!.setAlertMessage("Test Alert")
    }

    fun onLongMessageButtonClick() {
        log("onButtonClick")
        binding.viewModel!!.setAlertMessage(
            "Test Alert with really looooooooooong message. Test Alert with really long message." +
                    " Test Alert with really long message. Test Alert with really long message." +
                    " Test Alert with really long message. Test Alert with really long message." +
                    " Test Alert with really long message. Test Alert with really long message." +
                    " Test Alert with really long message. Test Alert with really long message." +
                    " Test Alert with really long message. Test Alert with really long message." +
                    " Test Alert with really long message. "
        )
    }

    fun onStartFragmentClick() {
        startFragment()
    }

    private fun startFragment() {
        supportFragmentManager.beginTransaction()
            //                .setMaxLifecycle(fragment, Lifecycle.State.RESUMED)
            .setCustomAnimations(
                R.animator.slide_in_from_right, 0, 0,
                R.animator.slide_out_to_right
            ).add(R.id.container, FragmentMain(), "Fragment")
            .addToBackStack("Fragment").commit()
    }
}

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor().newInstance()
    }
}