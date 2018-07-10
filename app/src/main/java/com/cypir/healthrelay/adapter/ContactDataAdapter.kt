package com.cypir.healthrelay.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.cypir.healthrelay.R
import com.cypir.healthrelay.entity.ContactData
import kotlinx.android.synthetic.main.item_add_contact.view.*

/**
 * Adapter for a list of contact info for a particular medium. For example, contactData could be
 * a list of email addresses or a list of phone numbers
 */
class ContactDataAdapter(var context: Context, var contactData: List<ContactData>) : RecyclerView.Adapter<ContactDataAdapter.ContactHolder>() {

    var selectedInfo  = mutableListOf<Boolean>()

    fun initialize(){
        contactData.forEach {
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
        return contactData.size
    }

    //bind it
    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.contact_info.text = contactData[position].info
        holder.contact_info.isChecked = contactData[position].isEnabled
        holder.contact_info.setOnCheckedChangeListener { buttonView, isChecked ->
            contactData[position].isEnabled = isChecked
            Log.d("HealthRelay","$contactData")
        }
    }

    //Holds the item information
    class ContactHolder(v: View) : RecyclerView.ViewHolder(v) {
        val contact_info = v.checkbox_contact_info
    }
}