package com.example.top10downloaderapp

import android.os.AsyncTask
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    lateinit var rvTop: RecyclerView
    private var allTops = mutableListOf<Top>()
    lateinit var getFeeds: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvTop = findViewById(R.id.rvTop)
        getFeeds = findViewById(R.id.button)

        rvTop.layoutManager = LinearLayoutManager(this)

        getFeeds.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val result = readTheTops()
                withContext(Dispatchers.Main){
                    rvTop
                }
            }
            BringTop().execute()
        }
    }
    private suspend fun readTheTops(): BringTop {
        return BringTop()
    }

    private inner class BringTop : AsyncTask<Void, Void, MutableList<Top>>() {
        val parser = XMLParser()
        override fun doInBackground(vararg p0: Void?): MutableList<Top> {
            val url = URL("http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=10/xml")
            val urlConnection = url.openConnection() as HttpURLConnection
            allTops =
                urlConnection.getInputStream()?.let {
                    parser.parse(it)
                }
                        as MutableList<Top>
            return allTops
        }

        override fun onPostExecute(result: MutableList<Top>?) {
            super.onPostExecute(result)
            rvTop.adapter = result?.let { RVtop(it) }
            rvTop.adapter?.notifyDataSetChanged()
            rvTop.scrollToPosition(allTops.size - 1)

        }
    }
}