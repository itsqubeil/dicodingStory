package test.dapuk.dicodingstory.ui.main

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import test.dapuk.dicodingstory.R
import test.dapuk.dicodingstory.data.repository.StoryRepository
import test.dapuk.dicodingstory.data.response.ListStoryItem
import test.dapuk.dicodingstory.data.retrofit.ApiConfig
import test.dapuk.dicodingstory.data.sharedpref.SharedPreferenceManager
import test.dapuk.dicodingstory.databinding.ActivityMainBinding
import test.dapuk.dicodingstory.ui.ListStoriesAdapter
import test.dapuk.dicodingstory.ui.addstory.AddStoryActivity
import test.dapuk.dicodingstory.ui.login.LoginActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var sharedPreferencesManager: SharedPreferenceManager
    private lateinit var mainViewModel: MainViewModel
    private lateinit var sharedPreferences: SharedPreferences

    private val listStoriesAdapter = ListStoriesAdapter(arrayListOf())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        sharedPreferences = getSharedPreferences("SESSION", Context.MODE_PRIVATE)
        val apiService = ApiConfig.getApiService()
        val storyRepository = StoryRepository(apiService, sharedPreferences)
        val viewModelFactory = ViewModelFactory(storyRepository)
        mainViewModel = ViewModelProvider(this, viewModelFactory).get(MainViewModel::class.java)
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        sharedPreferencesManager = SharedPreferenceManager(this)
        binding.btnLogout.setOnClickListener {
            sharedPreferencesManager.clearSession()
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }

        mainViewModel.listStories.observe(this) { listStories ->
            setStoriesList(listStories)
        }

        mainViewModel.isErr.observe(this) {

            if (sharedPreferencesManager.getSession() != null) {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }

        mainViewModel.isLoading.observe(this) {
            loading(it)
        }

        binding.rvStories.layoutManager = LinearLayoutManager(this)
        binding.rvStories.adapter = listStoriesAdapter
        binding.floatingActionButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
        }
        setContentView(binding.root)
    }

    private fun setStoriesList(storiesList: List<ListStoryItem>) {
        listStoriesAdapter.apply {
            listStories.clear()
            listStories.addAll(storiesList)
            notifyDataSetChanged()
        }

    }

    fun loading(isLoading: Boolean) {
        if (isLoading != false) {
            binding.progressBar2.visibility = View.VISIBLE
        } else {
            binding.progressBar2.visibility = View.GONE
        }
    }

    override fun onStart() {
        super.onStart()
        sharedPreferencesManager = SharedPreferenceManager(this)
        val session = sharedPreferencesManager.getSession()
        if (session != null) {
            val userId = session.userId
            val name = session.name
            val token = session.token
            Log.d("viewmodel response", "UserId: $userId, Name: $name, Token: $token")
        } else {
            Log.d("session:", "null")
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }


    override fun onResume() {
        super.onResume()
        mainViewModel.getStories()

    }
}
