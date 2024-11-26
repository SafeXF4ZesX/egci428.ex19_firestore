package com.ecgi428.ex19_firestore

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

class MessageAdapter(val mContext : Context, val layoutResID: Int, val messageList : List<Message>) : ArrayAdapter<Message>(mContext, layoutResID, messageList) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(layoutResID, null)
        val msgTextView = view.findViewById<TextView>(R.id.msgView)
        val ratingTextView = view.findViewById<TextView>(R.id.ratingView)
        val idTextView = view.findViewById<TextView>(R.id.idTextView)
        ratingTextView.text = "Rating: " + messageList[position].rating.toString()
        msgTextView.text = "Message: " + messageList[position].message
        idTextView.text = "ID: " + messageList[position].id
        return view

    }

}