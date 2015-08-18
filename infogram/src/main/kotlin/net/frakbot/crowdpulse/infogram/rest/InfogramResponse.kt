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
 * Data class mapping a response of the Infogr.am API.
 *
 * @param id            The ID of the generated graph.
 * @param title         The title of the graph.
 * @param theme_id      The theme ID of the graph.
 * @param published     {@code true} if the graph was published, {@code false} otherwise.
 * @param thumbnail_url The URL of the thumbnail for the graph.
 * @param date_modified The date of the last edit to the graph.
 * @param user_profile  The link to the user profile of the graph author.
 * @param publish_mode  The mode with which the graph has been published.
 * @param url           The URL of the graph.
 *
 * @author Francesco Pontillo
 */
public data class InfogramResponse(var id: String, var title: String, var theme_id: Int,
                                   var published: Boolean, var thumbnail_url: String, var date_modified: String,
                                   var user_profile: String, var publish_mode: String, var url: String)