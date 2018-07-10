package com.cypir.healthrelay.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cypir.healthrelay.entity.Contact
import android.view.View
import com.cypir.healthrelay.R
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter(var context: Context, var contacts: List<Contact>) : RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater = LayoutInflater.from(context)

        return ContactHolder(
                layoutInflater.inflate(R.layout.item_contact, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.name.text = contacts[position].name
    }

    //Holds the item information
    class ContactHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name = v.text_name
    }
}