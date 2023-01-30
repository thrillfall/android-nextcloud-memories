package com.example.nextcloudmemories.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.example.nextcloudmemories.R
import com.example.nextcloudmemories.dto.RemoteImage

class NextcloudRemoteImages(
    context: Context,
    remoteImages: ArrayList<RemoteImage>,
    resource: Int,
) : ArrayAdapter<RemoteImage>(context, resource, remoteImages) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var listItemView = convertView
        if (listItemView == null) {
            listItemView = LayoutInflater.from(context).inflate(R.layout.gallery_item, null)
        }

        val remoteImage: RemoteImage? = getItem(position)
        val remoteImageView = listItemView?.findViewById<ImageView>(R.id.idRemoteImage)

        remoteImageView!!.setImageBitmap(remoteImage?.bitmap)

        return listItemView!!
    }

}