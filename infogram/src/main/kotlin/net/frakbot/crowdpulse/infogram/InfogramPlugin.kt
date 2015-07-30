/*
 * Copyright 2015 Francesco Pontillo
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.frakbot.crowdpulse.infogram

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import net.frakbot.crowdpulse.common.util.ConfigUtil
import net.frakbot.crowdpulse.common.util.CrowdLogger
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber
import net.frakbot.crowdpulse.common.util.spi.IPlugin
import net.frakbot.crowdpulse.common.util.spi.VoidConfig
import net.frakbot.crowdpulse.data.entity.Message
import net.frakbot.crowdpulse.infogram.rest.*
import net.infogram.api.InfogramAPI
import retrofit.RestAdapter
import retrofit.converter.GsonConverter
import rx.Observable
import rx.Subscriber
import rx.observers.SafeSubscriber
import java.io.*
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardOpenOption
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.*
import kotlin.text.Regex

/**
 * A plugin for generating Infogr.am graphs about the results of the pipeline processing.
 *
 * @author Francesco Pontillo
 */
public class InfogramPlugin : IPlugin<Message, Message, InfogramConfig>() {

    val PLUGIN_NAME = "infogram"
    var props: Properties? = null
    val logger = CrowdLogger.getLogger(javaClass<InfogramPlugin>())
    var gson = GsonBuilder().create()

    override fun getName(): String? {
        return PLUGIN_NAME
    }

    override fun buildConfiguration(configurationMap: MutableMap<String, String>?): InfogramConfig? {
        return InfogramConfig().buildFromMap(configurationMap)
    }

    override fun getOperator(parameters: InfogramConfig?): Observable.Operator<Message, Message>? {
        return InfoGraphOperator(parameters)
    }

    private inner class InfoGraphOperator : Observable.Operator<Message, Message> {
        var infogramApi: InfogramAPI
        var parameters: InfogramConfig?

        constructor(parameters: InfogramConfig?) {
            this.parameters = parameters
            if (props == null) {
                props = ConfigUtil.getPropertyFile(javaClass<InfogramPlugin>(), "infogram.properties")
            }
            val API_KEY: String = props!!.getProperty("infogram.apikey")
            val API_SECRET: String = props!!.getProperty("infogram.secret")
            infogramApi = InfogramAPI(API_KEY, API_SECRET)
        }

        override fun call(t: Subscriber<in Message>?): Subscriber<in Message>? {
            return object : SafeSubscriber<Message>(t) {
                var tagMap: MutableMap<String, Double> = HashMap()
                var categoryMap: MutableMap<String, Double> = HashMap()
                var lemmaMap: MutableMap<String, Double> = HashMap()
                var tagCount: Long = 0
                var categoryCount: Long = 0
                var lemmaCount: Long = 0

                override fun onNext(message: Message) {
                    // make calculations for tags and categories
                    if (message.getTags() != null) {
                        var categories: MutableList<String> = ArrayList<String>()
                        tagCount += message.getTags().size()
                        for (tag in message.getTags()) {
                            var key = tag.getText()
                            val value: Double = tagMap[key] ?: 0.0
                            tagMap.put(key, value + 1)
                            if (tag.getCategories() != null) {
                                categories.addAll(tag.getCategories())
                            }
                        }
                        categoryCount += categories.size()
                        for (cat in categories) {
                            val value: Double = categoryMap[cat] ?: 0.0
                            categoryMap.put(cat, value + 1)
                        }
                    }

                    // make calculations for lemmas
                    if (message.getTokens() != null) {
                        for (tok in message.getTokens()) {
                            if (tok.isStopWord() || tok.getLemma() == null) {
                                continue
                            }
                            lemmaCount += 1
                            val value: Double = lemmaMap[tok.getLemma()] ?: 0.0
                            lemmaMap.put(tok.getLemma(), value + 1)
                        }
                    }
                    t?.onNext(message)
                }

                override fun onError(e: Throwable) {
                    e.printStackTrace()
                    t?.onError(e)
                }

                override fun onCompleted() {
                    // generate infogram
                    val tagChart = buildChart(arrayOf(tagMap), arrayOf("Tags"))
                    val categoryChart = buildChart(arrayOf(categoryMap), arrayOf("Categories"))
                    val lemmaChart = buildChart(arrayOf(lemmaMap), arrayOf("Lemmas"))

                    // save the charts
                    val tagRes = post(infogramApi, arrayOf(tagChart), "Crowd Pulse Tags", true, "public")
                    val categoryRes = post(infogramApi, arrayOf(categoryChart), "Crowd Pulse Categories", true, "public")
                    val lemmaRes = post(infogramApi, arrayOf(lemmaChart), "Crowd Pulse Lemmas", true, "public")

                    // get the resulting images
                    val tagImage = getPNG(infogramApi, tagRes?.id)
                    val categoryImage = getPNG(infogramApi, categoryRes?.id)
                    val lemmaImage = getPNG(infogramApi, lemmaRes?.id)

                    // save the images on the file system
                    writePNGs(parameters?.path,
                            Pair(tagImage, "tags"), Pair(categoryImage, "categories"), Pair(lemmaImage, "lemmas"))

                    t?.onCompleted()
                }

                fun buildChart(maps: Array<MutableMap<String, Double>>, sheetsNames: Array<String>): InfogramChart {
                    var chart = InfogramChart("chart", "wordcloud")
                    val QTY = "#"
                    val MAX_SHEET_SIZE = 100;

                    var sheets: Array<InfogramChartSheet?> = arrayOfNulls(maps.size())

                    for (i in 0..(maps.size() - 1)) {
                        sheets[i] = InfogramChartSheet()
                        if (maps.size() > 1) {
                            sheets[i]!!.header = arrayOf(sheetsNames[i], QTY)
                        }
                        var sheetList : MutableList<InfogramChartSheetRow> = arrayListOf()
                        val map = maps[i]
                        for ((key, value) in map) {
                            val row: Array<Any?>? = arrayOf(key, value)
                            sheetList.add(InfogramChartSheetRow(row))
                        }
                        var orderedSheetList = sheetList.sortDescendingBy {
                            it.data!![1] as Double
                        }.take(MAX_SHEET_SIZE)
                        sheets[i]?.rows = orderedSheetList.toTypedArray()
                    }

                    chart.data = InfogramChartData(sheets)
                    return chart;
                }
            }
        }
    }

    fun post(infogram: InfogramAPI, content: Array<InfogramChart>, title: String? = null,
             publish: Boolean? = null, publishMode: String? = null, copyright: String? = null,
             width: Double? = null, themeId: Int? = 45, password: String? = null): InfogramResponse? {

        var chartContent: String = GsonBuilder().create().toJson(content)
        val parameters: MutableMap<String, String?> = HashMap()
        parameters.set("content", chartContent)
        if (themeId != null) {
            parameters.set("theme_id", themeId.toString())
        }
        if (!title.isNullOrEmpty()) {
            parameters.set("title", title)
        }
        if (publish != null) {
            parameters.set("publish", publish.toString())
        }
        if (!publishMode.isNullOrEmpty()) {
            parameters.set("publish_mode", publishMode)
        }
        if (!password.isNullOrEmpty()) {
            parameters.set("password", password)
        }
        if (width != null) {
            parameters.set("width", width.toString())
        }
        if (!copyright.isNullOrEmpty()) {
            parameters.set("copyright", copyright)
        }

        val res = infogram.sendRequest("POST", "infographics", parameters)
        val id = res.getHeaders()["X-Infogram-Id"]?.firstOrNull()
        if (res.getHttpStatusCode() == 201) {
            logger.info("Created infogram at ${id}.")
            var reader = BufferedReader(InputStreamReader(res.getResponseBody()))
            return gson.fromJson(reader, javaClass<InfogramResponse>());
        }
        logger.error("Couldn't create infogram at ${id}.\n" +
                "Error ${res.getHttpStatusCode()}:\n" +
                "${res.getHttpStatusMessage()}")
        return null
    }

    fun getPNG(infogram: InfogramAPI, id: String?): ByteArray? {
        val res = infogram.sendRequest("GET", "infographics/${id}", mapOf(Pair("format","png")))
        if (res.getHttpStatusCode() == 200) {
            logger.info("Fetched infogram PNG at ${id}.")
            return res.getResponseBody().readBytes()
        }
        logger.error("Couldn't get infogram PNG at: ${id}.\n" +
                "Error: ${res.getHttpStatusCode()}:\n" +
                "${res.getHttpStatusMessage()}")
        return null;
    }

    fun writePNGs(path: String?, vararg files : Pair<ByteArray?, String>) : Array<String?> {
        // get a valid directory and replace ~ with the user dir
        val directory = (path ?: System.getProperty("java.io.tmpdir"))
                .replaceFirst(Regex("^~"), System.getProperty("user.home"));
        var resolved = Paths.get(directory, "crowd-pulse-infogram");
        Files.createDirectories(resolved);

        var date = ZonedDateTime.now()
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_INSTANT)
                .replace(Regex(":"), "-")

        // for every file, save it if it's not empty
        val pngs : Array<String?> = arrayOfNulls(files.size())
        for (f in files.indices) {
            var file = files[f]
            if (file.first == null) {
                continue
            }
            var filePath = resolved.resolve("${date}-${file.second}.png")
            try {
                Files.write(filePath, file.first, StandardOpenOption.CREATE_NEW)
                logger.info("Infogram written at path: ${filePath}.")
                pngs[f] = filePath.toString()
            } catch (e : IOException) {
                logger.error("Couldn't write infogram at path: ${filePath}.")
            }
        }
        return pngs
    }

}