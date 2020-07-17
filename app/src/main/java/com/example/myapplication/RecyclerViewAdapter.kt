package com.example.myapplication

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class RecyclerViewAdapter(context: Context, data: ArrayList<Photo>) : RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {

    private var mData: ArrayList<Photo> = data
    private var mInflater: LayoutInflater = LayoutInflater.from(context)
    private var mClickListener: ItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = mInflater.inflate(R.layout.recyclerview_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = mData[position].title
        Glide.with(holder.imageView.context).load(getUrlFromPosition(position)).placeholder(R.drawable.pic1)
                .error(R.drawable.ic_launcher_background).into(holder.imageView)
    }

    fun update(data: ArrayList<Photo>) {
        mData = data
        notifyDataSetChanged()
    }

    fun getUrlFromPosition(position: Int): String {
        return "https://farm" + mData[position].farm + ".static.flickr.com/" + mData[position].server +
                "/" + mData[position].id + "_" + mData[position].secret + ".jpg"
    }

    fun setClickListener(itemClickListener: ItemClickListener?) {
        mClickListener = itemClickListener
    }

    interface ItemClickListener {
        fun onItemClick(view: View?, position: Int)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        var imageView: ImageView = itemView.findViewById(R.id.grid_item_image)
        var textView: TextView = itemView.findViewById(R.id.text_view)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View?) {
            mClickListener?.onItemClick(view, adapterPosition)
        }
    }

}