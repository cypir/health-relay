package com.cypir.healthrelay.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.cypir.healthrelay.R
import com.cypir.healthrelay.entity.HRContactData
import kotlinx.android.synthetic.main.item_add_contact.view.*

/**
 * Adapter for a list of contact info for a particular medium. For example, HRContactData could be
 * a list of email addresses or a list of phone numbers
 */
class ContactDataAdapter(var context: Context, var HRContactData: List<HRContactData>, var listener : OnDataEnabled) : RecyclerView.Adapter<ContactDataAdapter.ContactHolder>() {

    //called when a user attempts to click on the checkbox next to a contact method
    interface OnDataEnabled {

        //to check whether or not the data permission has been granted or not.
        fun dataPermEnabled(mimetype : String) : Boolean
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater = LayoutInflater.from(context)

        return ContactHolder(
            layoutInflater.inflate(R.layout.item_add_contact, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return HRContactData.size
    }

    //bind it
    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.contact_info.text = HRContactData[position].data
        holder.contact_info.isChecked = HRContactData[position].isEnabled
        holder.contact_info.setOnCheckedChangeListener { buttonView, isChecked ->
            //on check, we have to determine what permissions we need.
            val dataPermEnabled = listener.dataPermEnabled(HRContactData[position].mimetype)

            //only if we have the permissions for this datatype (phone, email, etc)
            if(dataPermEnabled) {
                HRContactData[position].isEnabled = isChecked
                Log.d("HealthRelay", "$HRContactData")
            }else{
                //otherwise uncheck the box
                holder.contact_info.isChecked = false
            }
        }
    }

    //Holds the item information
    class ContactHolder(v: View) : RecyclerView.ViewHolder(v) {
        val contact_info = v.checkbox_contact_info
    }
}