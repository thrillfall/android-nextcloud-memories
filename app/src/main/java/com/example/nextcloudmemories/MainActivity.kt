package com.example.nextcloudmemories

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.nextcloudmemories.databinding.ActivityMainBinding
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
import com.smarteist.autoimageslider.SliderView
import kotlinx.coroutines.*
import java.io.BufferedReader


class MainActivity : AppCompatActivity() {

    private lateinit var ssoAccount: SingleSignOnAccount
    private lateinit var binding: ActivityMainBinding

    // on below line we are creating a variable
    // for our array list for storing our images.
    lateinit var imageUrls: ArrayList<String>

    // on below line we are creating
    // a variable for our slider view.
    lateinit var sliderView: SliderView

    // on below line we are creating
    // a variable for our slider adapter.
    lateinit var sliderAdapter: SliderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        binding.fab.setOnClickListener { view ->
                try {
                    AccountImporter.pickNewAccount(this)
                } catch (e: NextcloudFilesAppNotInstalledException) {
                UiExceptionManager.showDialogForException(this, e);
            }
        }

        // on below line we are initializing our slier view.
        sliderView = findViewById(R.id.slider)

        // on below line we are initializing
        // our image url array list.
        imageUrls = ArrayList()

        val daysIds =
            "19016,19017,19018,19019,19020,19021,19022,18651,18652,18653,18654,18655,18656,18657,18285,18286,18287,18288,18289,18290,18291,17920,17921,17922,17923,17924,17925,17926,17555,17556,17557,17558,17559,17560,17561,17190,17191,17192,17193,17194,17195"
        try {
            ssoAccount = SingleAccountHelper.getCurrentSingleSignOnAccount(this)

            val nextcloudAPI = NextcloudAPI(this, ssoAccount, GsonBuilder().create())

            val nextcloudRequestBuilder = NextcloudRequest.Builder()
            val parameters: MutableList<QueryParam> = ArrayList()
            parameters.add(QueryParam("body_ids",
                daysIds
            ))
            val thisTimeLastYearsRequest = nextcloudRequestBuilder
                .setMethod("POST")
                .setParameter(parameters)
                .setUrl(Uri.encode("/index.php/apps/memories/api/days", "/"))
                .build()

            lifecycleScope.launch(Dispatchers.IO)
            {
                Log.d("Track", "Launch Fetch Started")
                try {
                    val responseStream = nextcloudAPI.performNetworkRequestV2(thisTimeLastYearsRequest)
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
                    launch(Dispatchers.Main) {

                    }
                } catch (e: NextcloudHttpRequestFailedException) {
                    val message = e.getMessage(baseContext)
                    Log.d("FETCH", message)
                }
            }

        } catch (e: NoCurrentAccountSelectedException) {
            // on below line we are adding data to our image url array list.
            imageUrls.add("https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Fdsa-self-paced-thumbnail.png&w=1920&q=75")
            imageUrls.add("https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Fdata-science-live-thumbnail.png&w=1920&q=75")
            imageUrls.add("https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Ffull-stack-node-thumbnail.png&w=1920&q=75")
        }

        // on below line we are initializing our
        // slider adapter and adding our list to it.
        sliderAdapter = SliderAdapter( imageUrls)

        // on below line we are setting auto cycle direction
        // for our slider view from left to right.
        sliderView.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR

        // on below line we are setting adapter for our slider.
        sliderView.setSliderAdapter(sliderAdapter)

        // on below line we are setting scroll time
        // in seconds for our slider view.
        sliderView.scrollTimeInSec = 3

        // on below line we are setting auto cycle
        // to true to auto slide our items.
        sliderView.isAutoCycle = true

        // on below line we are calling start
        // auto cycle to start our cycle.
        sliderView.startAutoCycle()
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
//            val nextcloudAPI = NextcloudAPI(context, ssoAccount!!, GsonBuilder().create())

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

    private fun getHeaderWithUserAgent(): HashMap<String, List<String>> {
        val header: HashMap<String, List<String>> = HashMap()
        header["User-Agent"] = listOf("Nextcloud_Memories_App");
        return header
    }

}