package com.example.nextcloudmemories.provider

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.nextcloud.android.sso.QueryParam
import com.nextcloud.android.sso.aidl.NextcloudRequest
import com.nextcloud.android.sso.api.NextcloudAPI
import java.io.BufferedInputStream
import java.io.InputStream

object BitmapProvider {
    fun getRemotePreviewBitmap(
        eTag: String,
        fileId: Int,
        nextcloudAPI: NextcloudAPI,
        width: Int,
        heigth: Int
    ): Bitmap? {
        try {
            val nextcloudRequestBuilder = NextcloudRequest.Builder()
            val parameters: MutableList<QueryParam> = ArrayList()
            parameters.add(QueryParam("c", eTag))
            parameters.add(QueryParam("a", "1"))
            parameters.add(QueryParam("x", width.toString()))
            parameters.add(QueryParam("y", heigth.toString()))

            val thisTimeLastYearsRequest = nextcloudRequestBuilder
                .setMethod("GET")
                .setParameter(parameters)
                .setUrl(
                    Uri.encode(
                        "/index.php/apps/memories/api/image/preview/" + fileId,
                        "/"
                    )
                )
                .build()

            Log.d("Track", "Fetch Thumbnail: " + fileId)
            try {
                val responseStream =
                    nextcloudAPI.performNetworkRequestV2(thisTimeLastYearsRequest)
                val inputStream: InputStream = responseStream.body
                val bufferedInputStream = BufferedInputStream(inputStream)
                return BitmapFactory.decodeStream(bufferedInputStream)
            } catch (e: Exception) {
                Log.d("Nextcloud fetch", e.message!!)
            }

        } catch (e: Exception) {
            Log.e("FETCH Thumbnail", e.message!!)
        }


        return null
    }
}