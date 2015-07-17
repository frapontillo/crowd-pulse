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
 * @author Francesco Pontillo
 */
public data class InfogramChart(var type: String? = null, var chart_type: String? = null, var text: String? = null,
                                var title: String? = null, var author: String? = null,
                                var data: InfogramChartData? = null, var colors: Array<String>? = null)

public data class InfogramChartData(var sheets: Array<InfogramChartSheet?>? = null)

public data class InfogramChartSheet(var header: Array<String?>? = null, var rows: Array<InfogramChartSheetRow?>? = null)

public data class InfogramChartSheetRow(var data: Array<Any?>? = null, var header: String? = null, var color: String? = null)