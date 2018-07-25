package com.cypir.healthrelay.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.cypir.healthrelay.pojo.HRContact
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import com.cypir.healthrelay.R
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter(var context: Context, var contacts: List<HRContact>, var listener: OnItemClickListener) : RecyclerView.Adapter<ContactAdapter.ContactHolder>() {

    interface OnItemClickListener {
        fun onItemClick(item: HRContact)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactHolder {
        val layoutInflater = LayoutInflater.from(context)

        return ContactHolder(
                v = layoutInflater.inflate(R.layout.item_contact, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return contacts.size
    }

    override fun onBindViewHolder(holder: ContactHolder, position: Int) {
        holder.name.text = contacts[position].displayName
        if(contacts[position].thumbnailUri != null){
            holder.thumbnail.setImageURI(contacts[position].thumbnailUri)
        }

        holder.bind(contacts[position], listener)
    }

    //Holds the item information
    class ContactHolder(v: View) : RecyclerView.ViewHolder(v){
        val name = v.text_name
        val thumbnail = v.image_contact


        fun bind(item : HRContact, listener : OnItemClickListener) {
            itemView.setOnClickListener {
                listener.onItemClick(item)
            }
        }
    }
}