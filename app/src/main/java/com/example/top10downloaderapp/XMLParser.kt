package com.example.top10downloaderapp

import android.util.Xml
import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

class XMLParser {
    private val ta: String? = null

    fun parse(inputStream: InputStream): ArrayList<Top> {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()
            return readTopRssFeed(parser)
        }
    }

    private fun readTopRssFeed(parser: XmlPullParser): ArrayList<Top> {
        val top10 = ArrayList<Top>()

        parser.require(XmlPullParser.START_TAG, ta, "feed")

        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.eventType != XmlPullParser.START_TAG) {
                continue
            }
            if (parser.name == "entry") {
                parser.require(XmlPullParser.START_TAG, ta, "entry")
                var name: String? = null
                while (parser.next() != XmlPullParser.END_TAG) {
                    if (parser.eventType != XmlPullParser.START_TAG) {
                        continue
                    }
                    when (parser.name) {
                        "im:name" -> name = readName(parser)
                        else -> skip(parser)
                    }
                }
                top10.add(Top(name))
            } else {
                skip(parser)
            }
        }
        return top10
    }

    private fun readName(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, ta, "im:name")
        val name = readText(parser)
        parser.require(XmlPullParser.END_TAG, ta, "im:name")
        return name
    }

    private fun readText(parser: XmlPullParser): String {
        var result = ""
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.text
            parser.nextTag()
        }
        return result
    }

    private fun skip(parser: XmlPullParser) {
        if (parser.eventType != XmlPullParser.START_TAG) {
            throw IllegalStateException()
        }
        var deep = 1
        while (deep != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> deep--
                XmlPullParser.START_TAG -> deep++
            }
        }
    }
}