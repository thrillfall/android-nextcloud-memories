package com.example.nextcloudmemories

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.nextcloudmemories.adapter.NextcloudRemoteImages
import com.example.nextcloudmemories.databinding.ActivityMainBinding
import com.example.nextcloudmemories.dto.RemoteImage
import com.google.gson.GsonBuilder
import com.nextcloud.android.sso.AccountImporter
import com.nextcloud.android.sso.QueryParam
import com.nextcloud.android.sso.aidl.NextcloudRequest
import com.nextcloud.android.sso.api.NextcloudAPI
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppAccountNotFoundException
import com.nextcloud.android.sso.exceptions.NextcloudFilesAppNotInstalledException
import com.nextcloud.android.sso.exceptions.NextcloudHttpRequestFailedException
import com.nextcloud.android.sso.exceptions.NoCurrentAccountSelectedException
import com.nextcloud.android.sso.helper.SingleAccountHelper
import com.nextcloud.android.sso.model.SingleSignOnAccount
import com.nextcloud.android.sso.ui.UiExceptionManager
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONTokener
import java.io.BufferedInputStream
import java.io.BufferedReader
import java.io.InputStream
import java.lang.Math.floor


class MainActivity : AppCompatActivity() {

    private lateinit var remoteImageGallery: GridView
    private var remoteImages: ArrayList<RemoteImage> = ArrayList()
    private lateinit var nextcloudAPI: NextcloudAPI
    private lateinit var ssoAccount: SingleSignOnAccount
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        remoteImageGallery = findViewById(R.id.gridViewRemoteImages)

        binding.fab.setOnClickListener { view ->
            try {
                AccountImporter.pickNewAccount(this)
            } catch (e: NextcloudFilesAppNotInstalledException) {
                UiExceptionManager.showDialogForException(this, e);
            }
        }

        val todayId = floor(((System.currentTimeMillis() / 1000 / 86400).toDouble()))
        val dayIds: List<String> = generateDayIds(todayId)

        try {
            ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(this)

            nextcloudAPI = NextcloudAPI(this, ssoAccount, GsonBuilder().create())

            val nextcloudRemoteImagesAdapter = NextcloudRemoteImages(this, remoteImages, R.layout.gallery_item)
            remoteImageGallery.adapter = nextcloudRemoteImagesAdapter

            val nextcloudRequestBuilder = NextcloudRequest.Builder()
            val parameters: MutableList<QueryParam> = ArrayList()
            parameters.add(
                QueryParam(
                    "body_ids",
                    dayIds.joinToString()
                )
            )
            val thisTimeLastYearsRequest = nextcloudRequestBuilder
                .setMethod("POST")
                .setParameter(parameters)
                .setUrl(Uri.encode("/index.php/apps/memories/api/days", "/"))
                .build()

            lifecycleScope.launch(Dispatchers.IO)
            {
                Log.d("Track", "Launch Fetch days")
                try {
                    val responseStream =
                        nextcloudAPI.performNetworkRequestV2(thisTimeLastYearsRequest)
                    val reader = BufferedReader(responseStream.body.reader())
                    val content = StringBuilder()
                    try {
                        var line = reader.readLine()
                        while (line != null) {
                            content.append(line)
                            line = reader.readLine()
                        }
                    } finally {
                        reader.close()
                    }
                    Log.d("FETCH", content.toString())
                    val jsonArray = JSONTokener(content.toString()).nextValue() as JSONArray
                    for (i in 0 until jsonArray.length()) {
                        // ID
                        val fileId = jsonArray.getJSONObject(i).getInt("fileid")
                        Log.i("ID: ", fileId.toString())

                        // Employee Name
                        val eTag = jsonArray.getJSONObject(i).getString("etag")
                        Log.i("ETag: ", eTag)

                        // Employee Salary
                        val filename = jsonArray.getJSONObject(i).getString("filename")
                        Log.i("FileName", filename)

                        val bitmap = getRemotePreviewBitmap(eTag, fileId)
                        if (bitmap !== null) {
                            val taken = jsonArray.getJSONObject(i).getInt("dayid") * 86400
                            remoteImages.add(
                                RemoteImage(
                                    fileId,
                                    eTag,
                                    filename,
                                    bitmap,
                                    taken
                                )
                            )
                            launch(Dispatchers.Main) { nextcloudRemoteImagesAdapter.notifyDataSetChanged() }
                        }

                    }

                } catch (e: NextcloudHttpRequestFailedException) {
                    val message = e.getMessage(baseContext)
                    Log.d("FETCH", message)
                }

            }

        } catch (e: NoCurrentAccountSelectedException) {
            // on below line we are adding data to our image url array list.
        }

    }

    private fun generateDayIds(todayId: Double): List<String> {
        val dayIds: ArrayList<Double> = ArrayList()
        dayIds.add(todayId)
        dayIds.add(todayId-1)
        dayIds.add(todayId-2)

        for (i in 1..5) {
            val todayNYearsAgo = todayId - (365 * i)
            dayIds.add(todayNYearsAgo+2)
            dayIds.add(todayNYearsAgo+1)
            dayIds.add(todayNYearsAgo)
            dayIds.add(todayNYearsAgo-1)
            dayIds.add(todayNYearsAgo-2)
        }

        return dayIds.map { it.toString() }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        AccountImporter.onActivityResult(
            requestCode, resultCode, data, this
        ) { account ->
            val context = applicationContext

            // As this library supports multiple accounts we created some helper methods if you only want to use one.
            // The following line stores the selected account as the "default" account which can be queried by using
            // the SingleAccountHelper.getCurrentSingleSignOnAccount(context) method
            SingleAccountHelper.setCurrentAccount(context, account.name)

            // Get the "default" account
            var ssoAccount: SingleSignOnAccount? = null
            try {
                ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(context)
            } catch (e: NextcloudFilesAppAccountNotFoundException) {
                UiExceptionManager.showDialogForException(context, e)
            } catch (e: NoCurrentAccountSelectedException) {
                UiExceptionManager.showDialogForException(context, e)
            }

        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        AccountImporter.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    private fun getRemotePreviewBitmap(eTag: String, fileId: Int): Bitmap? {
        try {
            val nextcloudRequestBuilder = NextcloudRequest.Builder()
            val parameters: MutableList<QueryParam> = ArrayList()
            parameters.add(QueryParam("c", eTag))
            parameters.add(QueryParam("a", "1"))
            parameters.add(QueryParam("x", "512"))
            parameters.add(QueryParam("y", "512"))

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

