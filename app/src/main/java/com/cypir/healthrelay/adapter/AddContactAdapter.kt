package com.cypir.healthrelay.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import com.cypir.healthrelay.entity.Contact
import android.view.View
import com.cypir.healthrelay.R
import kotlinx.android.synthetic.main.item_add_contact.*
import kotlinx.android.synthetic.main.item_add_contact.view.*

/**
 * Adapter for a list of contact info for a particular medium. For example, contactInfo could be
 * a list of email addresses or a list of phone numbers
 */
class AddContactAdapter(var context: Context, var contactInfo: List<String>) : RecyclerView.Adapter<AddContactAdapter.ContactHolder>() {

    var selectedInfo  = mutableListOf<Boolean>()

    fun initialize(){
        contactInfo.forEach {
            selectedInfo.add(false)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater = LayoutInflater.from(context)

        return ContactHolder(
            layoutInflater.inflate(R.layout.item_add_contact, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return contactInfo.size
    }

//    fun getSelectedContacts(): List<String> {
//        var ret = mutableListOf<String>()
//
//        selectedInfo.forEachIndexed { index, selected ->
//            if(selected){
//                //ret.add()
//            }
//        }
//    }

    //bind it
    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.contact_info.text = contactInfo[position]
        holder.contact_info.setOnCheckedChangeListener { buttonView, isChecked ->
            selectedInfo[position] = isChecked
            Log.d("HealthRelay","$position")
        }
    }

    //Holds the item information
    class ContactHolder(v: View) : RecyclerView.ViewHolder(v) {
        val contact_info = v.checkbox_contact_info
    }
}