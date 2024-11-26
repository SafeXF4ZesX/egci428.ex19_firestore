package com.ecgi428.ex19_firestore

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.RatingBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import java.util.UUID

class MainActivity : AppCompatActivity() {

    lateinit var userUUID : String

    lateinit var editText: EditText
    lateinit var submitBtn: Button
    lateinit var ratingBar: RatingBar
    lateinit var listView: ListView

    lateinit var msgList: MutableList<Message>
    lateinit var adapter: MessageAdapter
    lateinit var dataReference: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val sharedPreferences: SharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)
        userUUID = sharedPreferences.getString("UUID", null) ?: generateAndSaveUUID(sharedPreferences)


        editText = findViewById(R.id.editText)
        submitBtn = findViewById(R.id.submitBtn)
        ratingBar = findViewById(R.id.ratingBar)
        listView = findViewById(R.id.listView)

        msgList = mutableListOf()
        dataReference = FirebaseFirestore.getInstance()

        submitBtn.setOnClickListener{
            submitData()
            readFirestoreData()

        }
        readFirestoreData()
    }

    private fun submitData() {
        val msg = editText.text.toString()
        if (msg.isEmpty()) {
            editText.error = "Please submit a message"
            return
        }

        val userCollection = dataReference.collection("users").document(userUUID).collection("dataMessage")
        val messageId = userCollection.document().id
        val messageData = Message(messageId, msg, ratingBar.rating.toFloat(), System.currentTimeMillis().toString())

        userCollection.add(messageData).addOnSuccessListener {
            Toast.makeText(applicationContext, "Message saved successfully", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(applicationContext, "Failed to save message", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readFirestoreData() {
        val userCollection = dataReference.collection("users").document(userUUID).collection("dataMessage")
        userCollection.orderBy("timeStamp").get()
            .addOnSuccessListener { snapshot ->
                if (snapshot != null) {
                    msgList.clear()
                    val messages = snapshot.toObjects(Message::class.java)
                    msgList.addAll(messages)
                    adapter = MessageAdapter(applicationContext, R.layout.messages, msgList)
                    listView.adapter = adapter
                }
            }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "Failed to read messages", Toast.LENGTH_SHORT).show()
            }
    }



//    private fun submitData() {
//        val msg  = editText.text.toString()
//        if (msg.isEmpty()){
//            editText.error = "Please submit a message"
//            return
//        }
//
//        var db = dataReference.collection("dataMessage")
//        val messageId = db.document().id
//        val messageData = Message(messageId, msg, ratingBar.rating.toFloat(), System.currentTimeMillis().toString())
//
//        db.add(messageData).addOnSuccessListener {
//            Toast.makeText(applicationContext, "Message is saved successfully", Toast.LENGTH_SHORT)
//                .show()
//        }.addOnFailureListener{
//            Toast.makeText(applicationContext, "Message is failed to save", Toast.LENGTH_SHORT)
//                .show()
//        }
//    }

//    private fun readFirestoreData() {
//        var db = dataReference.collection("dataMessage")
//        db.orderBy("timeStamp").get()
//            .addOnSuccessListener { snapshot ->
//                if(snapshot != null){
//                    msgList.clear()
//                    val messages = snapshot.toObjects(Message::class.java)
//                    for (message in messages){
//                        msgList.add(message)
//                    }
//
//                    adapter = MessageAdapter(applicationContext, R.layout.messages, msgList)
//
//                    listView.adapter = adapter
//                }
//
//        }
//            .addOnFailureListener {
//                Toast.makeText(applicationContext, "Message is failed to read from Firestore", Toast.LENGTH_SHORT)
//                    .show()
//            }
//    }

    private fun generateAndSaveUUID(sharedPreferences: SharedPreferences): String {
        val uuid = UUID.randomUUID().toString()
        val editor = sharedPreferences.edit()
        editor.putString("UUID", uuid)
        editor.apply()
        return uuid
    }
}