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

package net.frakbot.crowdpulse.infogram.rest

/**
 * Data class containing all of the elements of a typical Infogr.am chart.
 *
 * @param type          The type of the graph.
 * @param chart_type    The type of the chart.
 * @param text          The text to set into the graph.
 * @param title         The title of the chart.
 * @param author        The author of the chart.
 * @param data          An {@link InfogramChartData} object, containing all of the elements to render.
 * @param colors        An {@link Array} of {@link String} colors to use for rendering the chart.
 *
 * @author Francesco Pontillo
 */
public data class InfogramChart(var type: String? = null, var chart_type: String? = null, var text: String? = null,
                                var title: String? = null, var author: String? = null,
                                var data: InfogramChartData? = null, var colors: Array<String>? = null)

/**
 * Data class whose only element is an array of sheets.
 *
 * @param sheets    An {@link Array} of sheets of class {@link InfogramChartSheet}.
 *
 * @author Francesco Pontillo
 */
public data class InfogramChartData(var sheets: Array<InfogramChartSheet?>? = null)

/**
 * Data class mapping a chart sheet.
 *
 * @param header    The headers of the sheet.
 * @param rows      The rows of the sheet, as {@link Array} of {@link InfogramChartSheetRow}.
 *
 * @author Francesco Pontillo
 */
public data class InfogramChartSheet(var header: Array<String?>? = null, var rows: Array<InfogramChartSheetRow?>? = null)

/**
 * Data class for a sheet row, containing all of the actual data for the chart.
 *
 * @param data      The actual raw data for the chart.
 * @param header    The optional header of the row.
 * @param color     The color to give the row.
 *
 * @author Francesco Pontillo
 */
public data class InfogramChartSheetRow(var data: Array<Any>? = null, var header: String? = null, var color: String? = null)