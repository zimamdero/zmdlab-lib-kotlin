package com.zmd.lab.search.img

import org.json.JSONException
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import com.zmd.lab.search.img.model.ImgInfo

/**
 * image searching by google image search request
 * using jsoup : https://jsoup.org/
 */
class GoogleImgSearch {
    fun startSearch(name: String, callback: (list: ArrayList<ImgInfo>) -> Unit) {
        Thread(Runnable {
            val result = readDocImageSearch(name)
            callback(result)
        }).start()
    }

    private fun readDocImageSearch(name: String): ArrayList<ImgInfo> {
        val url = "https://www.google.co.kr/search?q=${name}}&tbm=isch"
        val doc = Jsoup.connect(url).get()
        val dataInfo = readData(doc)
        val divs = doc.select("div[class=isv-r PNCib MSM1fd BUooTd]")
        var imgInfoList = ArrayList<ImgInfo>()

        for (div in divs) {
            val dataId = div.attr("data-id")
            val img = div.select("img[class=rg_i Q4LuWd]").first()
            var thumb = ""
            if (img.hasAttr("data-src")) {
                thumb = img.attr("data-src")
            }
            if (img.hasAttr("src")) {
                // it is base64 jpeg image.
                // parsing by jsoup, not loaded. (1x1 gif image base64 in html)
                // so it is empty (tumb = "")
            }
            val a = div.select("a[class=VFACy kGQAp sMi44c lNHeqe WGvvNb]").first()
            val link = a.attr("href")
            val title = a.attr("title")
            val dmdiv = a.select("div[class=fxgdke]").first()
            val dm = dmdiv.text()
            val origin = dataInfo[dataId] ?: ""
            imgInfoList.add(ImgInfo(dataId, title, link, thumb, dm, origin))
        }

        return imgInfoList
    }

    private fun readData(doc: Document): HashMap<String, String> {
        val result = HashMap<String, String>()
        val scripts = doc.select("script")

        for (s in scripts) {
            for (node in s.dataNodes()) {
                val otherString = "AF_initDataCallback("
                val contains = node.wholeData.contains(otherString)
                if (contains) {
                    val wholeData = node.wholeData
                    var start = otherString.length
                    var end = wholeData.length - 2
                    val dataJsonString = wholeData.substring(start, end)
                    val dataJson = JSONObject(dataJsonString)
                    val data = dataJson.getJSONArray("data")

                    if (data.length() > 0) {
                        val divDataList = data.getJSONArray(31).getJSONArray(0).getJSONArray(12).getJSONArray(2)

                        for (i in 0 until divDataList.length()) {
                            try {
                                val divData = divDataList.getJSONArray(i).getJSONArray(1)
                                val id = divData.getString(1)
                                val originImg = divData.getJSONArray(3).getString(0)
                                result[id] = originImg
                            } catch (e: JSONException) {

                            }
                        }
                    }
                }
            }
        }

        return result
    }
}
