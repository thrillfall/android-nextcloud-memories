package com.example.nextcloudmemories.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.nextcloudmemories.R
import com.example.nextcloudmemories.dto.RemoteImage
import java.text.SimpleDateFormat
import java.util.*

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
        remoteImage!!
        val remoteImageView = listItemView?.findViewById<ImageView>(R.id.idRemoteImage)
        val remoteImageTextView = listItemView?.findViewById<TextView>(R.id.imageTextView)

        remoteImageView!!.setImageBitmap(remoteImage.bitmap)
        val sdf = SimpleDateFormat("yyyy")
        val netDate = derieveDateFromUnixTimestamp(remoteImage.taken)
        remoteImageTextView?.text = sdf.format(netDate)

        return listItemView!!
    }

    private fun derieveDateFromUnixTimestamp(takenInSeconds: Int) =
        Date(takenInSeconds.toLong() * 1000)

}