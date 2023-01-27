package com.example.nextcloudmemories

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import android.view.Menu
import android.view.MenuItem
import com.example.nextcloudmemories.databinding.ActivityMainBinding
import com.smarteist.autoimageslider.SliderView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    // on below line we are creating a variable
    // for our array list for storing our images.
    lateinit var imageUrl: ArrayList<String>

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
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAnchorView(R.id.fab)
                    .setAction("Action", null).show()
        }

        // on below line we are initializing our slier view.
        sliderView = findViewById(R.id.slider)

        // on below line we are initializing
        // our image url array list.
        imageUrl = ArrayList()

        // on below line we are adding data to our image url array list.
        imageUrl.add("https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Fdsa-self-paced-thumbnail.png&w=1920&q=75")
        imageUrl.add("https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Fdata-science-live-thumbnail.png&w=1920&q=75")
        imageUrl.add("https://practice.geeksforgeeks.org/_next/image?url=https%3A%2F%2Fmedia.geeksforgeeks.org%2Fimg-practice%2Fbanner%2Ffull-stack-node-thumbnail.png&w=1920&q=75")

        // on below line we are initializing our
        // slider adapter and adding our list to it.
        sliderAdapter = SliderAdapter( imageUrl)

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

}