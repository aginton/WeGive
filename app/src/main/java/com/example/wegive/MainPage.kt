package com.example.wegive

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main_page.*

private const val TAG="MainPage"
class MainPage : AppCompatActivity() {

    private lateinit var mFirebaseDatabaseInstance: FirebaseFirestore
    private var userId:String?=null

    //create data source for donations
    private lateinit var donations: MutableList<Donation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_page)

        donations = mutableListOf()

        mFirebaseDatabaseInstance= FirebaseFirestore.getInstance()

        val user= FirebaseAuth.getInstance().currentUser

        if (user != null) {
            userId=user.uid
            Log.i(TAG, "Current user uid: ${userId}")
        }

        val userRef = mFirebaseDatabaseInstance.collection("users").document(userId!!)
        val donationsReference = userRef.collection("donations")


        Log.i(TAG, "Found donationsReference: ${donationsReference}")
        donationsReference.addSnapshotListener { snapshot, exception ->
            Log.i(TAG, "Inside donationsReference.addSnapshotListener")

            if (exception!= null || snapshot == null){
                Log.e(TAG, "Exception when querying donations", exception)
                return@addSnapshotListener
            }

            if (snapshot != null) {
                for (document in snapshot.documents){
                    Log.i(TAG, "Donation: ${document.id}: ${document.data}")
                }
            }
        }


        btn_settings.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@MainPage, SettingsPage::class.java)
                startActivity(intent);
            }
        })

        btn_dumbDonate_activityMainPage.setOnClickListener {
            val intent = Intent(this, DonationFormActivity::class.java)
            startActivity(intent)
        }

        btn_wallet.setOnClickListener(object: View.OnClickListener {
            override fun onClick(view: View): Unit {
                val intent = Intent(this@MainPage, WalletPage::class.java)
                startActivity(intent);
            }
        })

        getDataOnce()
    }

    private fun getDataOnce() {
        //getting the data onetime
        val docRef=mFirebaseDatabaseInstance?.collection("users")?.document(userId!!)

        docRef?.get()?.addOnSuccessListener { documentSnapshot ->
            val user=documentSnapshot.toObject(User::class.java)

//            Log.e(TAG,"user data is changed"+user?.firstName+", "+user?.email)

            //Display newly updated name and email
            tv_helloPerson.setText("Hello " + user?.firstName)
            tv_totalDonations.setText(user?.TotalDonations.toString())
            tv_weGiveCoins.setText(user?.myCoins.toString())
        }
    }
}