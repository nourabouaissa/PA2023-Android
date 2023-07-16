package com.example.pa_appli.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.pa_appli.R
import com.example.pa_appli.databinding.ActivityPlayerBinding
import com.example.pa_appli.network.response.Friend
import com.example.pa_appli.services.FriendsViewModelFactory
import com.example.pa_appli.services.LoginViewModelFactory
import com.example.pa_appli.view.FriendsAdapter
import com.example.pa_appli.utils.startActivity
import com.example.pa_appli.data.Result
class ActivityPlayer : AppCompatActivity() {
    private lateinit var binding: ActivityPlayerBinding
    private lateinit var friendViewModel: FriendsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        friendViewModel = ViewModelProvider(this, FriendsViewModelFactory())
            .get(FriendsViewModel::class.java)
        setContentView(binding.root)
        val adapter =FriendsAdapter()
        val sharedPref = this?.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        binding.logoutBtn.setOnClickListener {

            with (sharedPref?.edit()) {
                this?.putString(getString(R.string.user_token),null)
                this?.putString(getString(R.string.user_id),null)
                this?.apply()
            }
            startActivity(MainActivity::class.java)
        }
        val userId=sharedPref?.getString(getString(R.string.user_id),null)?.toInt()
        val accessToken=sharedPref?.getString(getString(R.string.user_token),null)
        if( userId==null ||accessToken==null){
            startActivity(LoginActivity::class.java)
        }
        binding.friendsRecyclerview.adapter=adapter
        friendViewModel.getFriends(userId!!,accessToken!!).observe(this) {
                if(it is Result.Success){
                    adapter.submitList(it.data)
                }

        }


    }
}