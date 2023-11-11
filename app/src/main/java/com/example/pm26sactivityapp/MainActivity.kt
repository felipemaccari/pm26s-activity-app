package com.example.pm26sactivityapp
import android.R.bool
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class MainActivity : AppCompatActivity() {
    private lateinit var btnGoogleLogin: Button
    private lateinit var tvEmail: TextView

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var auth: FirebaseAuth

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val db = Firebase.firestore

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

                            if(user != null){
                                listUserFromDatabase(user.displayName, user.email, user.photoUrl)
                            }
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

    private fun listUserFromDatabase(username: String?, userEmail: String?, userPhotoURL: Uri?) {
        db.collection("users")
            .whereEqualTo("userEmail", userEmail)
            .get()
            .addOnSuccessListener { result ->
                if(result.isEmpty && username != null && userEmail != null){
                    registerUserOnDatabase(username, userEmail, userPhotoURL)
                } else {
                    openIntentMainActivity()
                    Toast.makeText(this, "Login realizado com sucesso", Toast.LENGTH_LONG).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro na listagem", Toast.LENGTH_LONG).show()
                Log.w("#USER_LIST", "Error getting documents. ", e)
            }
    }

    private fun registerUserOnDatabase(username: String, userEmail: String, userPhotoURL: Uri?) {
        val registro = hashMapOf(
            "username" to username,
            "userEmail" to userEmail,
            "userPhotoURL" to userPhotoURL
        )

        db.collection("users")
            .add(registro)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(this, "Usuário criado com sucesso", Toast.LENGTH_LONG).show()
                openIntentMainActivity()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Ocorreu um erro, veja nos logs", Toast.LENGTH_LONG).show()
                Log.w("#USER_INCLUDE", "Error adding document", e)
            }
    }

    private fun openIntentMainActivity(){
        startActivity(Intent(this, MainMenuActivity::class.java).apply {})
    }
}