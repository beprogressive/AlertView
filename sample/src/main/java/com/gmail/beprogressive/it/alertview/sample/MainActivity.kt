package com.gmail.beprogressive.it.alertview.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.gmail.beprogressive.it.alertview.log
import com.gmail.beprogressive.it.alertview.sample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val viewModel = ViewModelProvider(this, MainViewModelFactory()).get(
            MainViewModel::class.java
        )

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.viewModel = viewModel
        binding.activity = this

        subscribeToModel()
    }

    private fun subscribeToModel() {
        binding.viewModel?.enableButton?.observe(this) {
            if (it)
                binding.alertView.setButtonClickListener { alertView, _ ->
                    alertView.hideAlertView(false)
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
}

class MainViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor().newInstance()
    }
}