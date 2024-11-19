package test.dapuk.dicodingstory.ui.login

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import test.dapuk.dicodingstory.R
import test.dapuk.dicodingstory.data.sharedpref.SharedPreferenceManager
import test.dapuk.dicodingstory.databinding.ActivityLoginBinding
import test.dapuk.dicodingstory.ui.main.MainActivity
import test.dapuk.dicodingstory.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel
    private lateinit var sharedPreferenceManager: SharedPreferenceManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)
        sharedPreferenceManager = SharedPreferenceManager(this)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        playAnimation()

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            var email = binding.edLoginEmail.text.toString()
            var password = binding.edLoginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty()) {
                Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
                loginViewModel.login(email, password)
            } else {
                when {
                    email.isEmpty() && password.isEmpty() -> Toast.makeText(
                        this,
                        "Email & Password tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()

                    email.isEmpty() -> Toast.makeText(
                        this,
                        "Email tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()

                    password.isEmpty() -> Toast.makeText(
                        this,
                        "Password tidak boleh kosong",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }


        loginViewModel.isLoading.observe(this) { isLoading ->
            loginLoading(isLoading)
        }

        loginViewModel.loginUserId.observe(this) { userId ->
            val name = loginViewModel.loginName.value
            val token = loginViewModel.loginToken.value
            Log.d("viewmodel response", "UserId: $userId, Name: $name, Token: $token")

            if (userId != null && name != null && token != null) {
                saveSessionPref(userId, name, token)
            }
        }

        loginViewModel.loginSuccess.observe(this) {
            successLoginIntent(it)
        }

        loginViewModel.loginErr.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }


    }

    fun saveSessionPref(userId: String, name: String, token: String) {
        sharedPreferenceManager.saveSession(userId, name, token)
        Log.d("SessionSave", "Session saved: userId=$userId, name=$name, token=$token")
    }

    fun successLoginIntent(success: Boolean) {
        if (success.equals(true)) {
            Toast.makeText(this, "Logged in", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    fun loginLoading(isLoading: Boolean) {
        if (isLoading != false) {
            binding.pbLoading.visibility = View.VISIBLE
            binding.ivLogin.visibility = View.GONE
            binding.btnLogin.visibility = View.GONE
            binding.emailLayout.visibility = View.GONE
            binding.passwordLayout.visibility = View.GONE
            binding.tvRegister.visibility = View.GONE
        } else {
            binding.pbLoading.visibility = View.GONE
            binding.ivLogin.visibility = View.VISIBLE
            binding.btnLogin.visibility = View.VISIBLE
            binding.emailLayout.visibility = View.VISIBLE
            binding.passwordLayout.visibility = View.VISIBLE
            binding.tvRegister.visibility = View.VISIBLE
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivLogin, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(500)
        val signup = ObjectAnimator.ofFloat(binding.tvRegister, View.ALPHA, 1f).setDuration(1000)
        val name = ObjectAnimator.ofFloat(binding.emailLayout, View.ALPHA, 1f).setDuration(200)
        val password =
            ObjectAnimator.ofFloat(binding.passwordLayout, View.ALPHA, 1f).setDuration(300)
        val together = AnimatorSet().apply {
            playTogether(login, signup)
        }

        AnimatorSet().apply {
            playSequentially(name, password, together)
            start()
        }
    }

}