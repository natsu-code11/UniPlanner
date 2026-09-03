package com.uniplanner.app

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmailLogin: EditText
    private lateinit var etPasswordLogin: EditText
    private lateinit var etMatricolaLogin: EditText
    private lateinit var btnAccedi: Button
    private lateinit var btnBiometria: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etEmailLogin = findViewById(R.id.etEmailLogin)
        etPasswordLogin = findViewById(R.id.etPasswordLogin)
        etMatricolaLogin = findViewById(R.id.etMatricolaLogin)
        btnAccedi = findViewById(R.id.btnAccedi)
        btnBiometria = findViewById(R.id.btnBiometria)

        btnAccedi.setOnClickListener {
            loginConCredenziali()
        }

        btnBiometria.setOnClickListener {
            loginConBiometria()
        }
    }

    private fun loginConCredenziali() {
        val email = etEmailLogin.text.toString().trim()
        val password = etPasswordLogin.text.toString().trim()
        val matricola = etMatricolaLogin.text.toString().trim()

        if (email.isEmpty()) {
            etEmailLogin.error = "Inserisci l'email"
            return
        }

        if (!email.contains("@")) {
            etEmailLogin.error = "Email non valida"
            return
        }

        if (password.isEmpty()) {
            etPasswordLogin.error = "Inserisci la password"
            return
        }

        if (password.length < 6) {
            etPasswordLogin.error = "La password deve avere almeno 6 caratteri"
            return
        }

        if (matricola.isEmpty()) {
            etMatricolaLogin.error = "Inserisci la matricola"
            return
        }

        val prefs = getSharedPreferences("uniplanner_prefs", MODE_PRIVATE)

        prefs.edit()
            .putBoolean("utente_autenticato", true)
            .putString("email", email)
            .putString("matricola", matricola)
            .apply()

        Toast.makeText(this, "Accesso effettuato", Toast.LENGTH_SHORT).show()

        vaiAllaProssimaSchermata()
    }

    private fun loginConBiometria() {
        val prefs = getSharedPreferences("uniplanner_prefs", MODE_PRIVATE)

        val emailSalvata = prefs.getString("email", "") ?: ""
        val matricolaSalvata = prefs.getString("matricola", "") ?: ""

        if (emailSalvata.isEmpty() || matricolaSalvata.isEmpty()) {
            Toast.makeText(
                this,
                "Prima accedi almeno una volta con email, password e matricola",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val biometricManager = BiometricManager.from(this)

        val autenticazioneDisponibile = biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_WEAK or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )

        if (autenticazioneDisponibile != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(
                this,
                "Autenticazione biometrica non disponibile su questo dispositivo",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val executor = ContextCompat.getMainExecutor(this)

        val biometricPrompt = BiometricPrompt(
            this,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    prefs.edit()
                        .putBoolean("utente_autenticato", true)
                        .apply()

                    Toast.makeText(
                        this@LoginActivity,
                        "Accesso biometrico riuscito",
                        Toast.LENGTH_SHORT
                    ).show()

                    vaiAllaProssimaSchermata()
                }

                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)

                    Toast.makeText(
                        this@LoginActivity,
                        "Autenticazione annullata o non riuscita",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()

                    Toast.makeText(
                        this@LoginActivity,
                        "Biometria non riconosciuta",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Accesso sicuro")
            .setSubtitle("Usa impronta, volto o codice del dispositivo")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_WEAK or
                        BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
            .build()

        biometricPrompt.authenticate(promptInfo)
    }

    private fun vaiAllaProssimaSchermata() {
        val prefs = getSharedPreferences("uniplanner_prefs", MODE_PRIVATE)

        val primoAvvio = prefs.getBoolean("primo_avvio", true)

        if (primoAvvio) {
            startActivity(Intent(this, ConfigurazioneActivity::class.java))
        } else {
            startActivity(Intent(this, MainActivity::class.java))
        }

        finish()
    }
}