package com.example.pm26sactivityapp
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var btnGoogleLogin: Button
    private lateinit var tvEmail: TextView

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvEmail = findViewById(R.id.tvEmail)
        btnGoogleLogin = findViewById(R.id.btnGoogleLogin)
        btnGoogleLogin.setOnClickListener {
            tratarLogin()
        }

        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(false)
            .build()

        auth = Firebase.auth
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.

        var currentUser = auth.getCurrentUser()
        //updateUI(currentUser);
    }

    private fun tratarLogin(){
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener (this) { result ->
                startIntentSenderForResult(result.pendingIntent.intentSender, 2, null, 0,0,0,null)
            }
            .addOnFailureListener(this) {e ->
                Log.d("LOGIN", e.localizedMessage)
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val googleCredential = oneTapClient.getSignInCredentialFromIntent(data)
        val idToken = googleCredential.googleIdToken
        when {
            idToken != null -> {
                // Got an ID token from Google. Use it to authenticate
                // with Firebase.
                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                auth.signInWithCredential(firebaseCredential)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LOGIN", "signInWithCredential:success")
                            val user = auth.currentUser
                            println(user?.email)
                            tvEmail.text = user?.email
                            //updateUI(user)
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LOGIN", "signInWithCredential:failure", task.exception)
                            //updateUI(null)
                        }
                    }
            }
            else -> {
                // Shouldn't happen.
                Log.d("LOGIN", "No ID token!")
            }
        }
    }
}