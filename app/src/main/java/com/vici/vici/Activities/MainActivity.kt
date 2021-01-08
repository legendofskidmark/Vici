package com.vici.vici.Activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vici.vici.Constants.IntegerConstants
import com.vici.vici.Constants.StringConstants
import com.vici.vici.R
import com.vici.vici.Util.SharedPreferencesUtility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity: AppCompatActivity() {

    lateinit var gso: GoogleSignInOptions
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var auth: FirebaseAuth
    lateinit var sharedPref: SharedPreferences
    lateinit var spEditor: SharedPreferences.Editor

    var shouldDisplaySignIn = true

    companion object {
        var userEmailID: String = ""
        lateinit var db: FirebaseFirestore
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        configureSharedPreferences()
        initUI()

        db = Firebase.firestore
    }


    private fun initUI() {
        configureGoogleLoginSetup()
        configureOnClickListeners()
        if (shouldDisplaySignIn) {

        } else if (!sharedPref.contains(StringConstants.FILLED_THE_FORM)) {
            val intent = Intent(this, NewUserActiviy::class.java)
            startActivity(intent)
        } else {
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun configureGoogleLoginSetup() {
        auth = FirebaseAuth.getInstance()
        gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun configureOnClickListeners() {
        gSignInButton.setOnClickListener {
            val signInIntent: Intent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, IntegerConstants.RC_SIGN_IN)
        }

        signOutBtn.setOnClickListener { view: View? ->  mGoogleSignInClient.signOut().addOnCompleteListener {
                task: Task<Void> -> if (task.isSuccessful) {
            Toast.makeText(this, "Logged Out Successfully !!", Toast.LENGTH_SHORT).show()
        }
        } }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IntegerConstants.RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)

            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e)
                // ...
            }

//            handleResult (task)
        }else {
            Toast.makeText(this, "Problem in execution order :(", Toast.LENGTH_LONG).show()
        }
    }

    private fun handleResult (completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                updateUI (account)
            }
        } catch (e: ApiException) {
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show()
        }
    }

    private fun updateUI (account: GoogleSignInAccount) {
//        Toast.makeText(this, account.displayName, Toast.LENGTH_LONG).show()

    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val new = task.getResult()?.additionalUserInfo?.isNewUser
                    var user = auth.currentUser

                    spEditor.putString(StringConstants.USER_EMAIL_ID, user?.email.toString())
                    spEditor.apply()

                    userEmailID = user?.email.toString()

                    if (new == false) {
                        val intent = Intent(this, MapsActivity::class.java)
                        startActivity(intent)
                    } else {
                        val intent = Intent(this, NewUserActiviy::class.java)
                        startActivity(intent)
                    }
//                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
//                    Log.w(TAG, "signInWithCredential:failure", task.exception)
                    // ...
//                    Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
//                    updateUI(null)
                }

                // ...
            }
    }

    private fun configureSharedPreferences() {
        sharedPref = SharedPreferencesUtility.openSharedPreferencesWith(applicationContext, StringConstants.SHARED_PREF_FILE_NAME)
        spEditor= sharedPref.edit()

        if(sharedPref.contains(StringConstants.IS_FRESH_INSTALL)) {
            // already installed
            shouldDisplaySignIn = false
        } else {
            //new installation
            spEditor.putBoolean(StringConstants.IS_FRESH_INSTALL, false)
            shouldDisplaySignIn = true
        }
        spEditor.apply()
    }
}