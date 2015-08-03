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

package net.frakbot.crowdpulse.social.extraction;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.StringUtil;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import net.frakbot.crowdpulse.social.converter.GeoLocationBoxConverter;
import net.frakbot.crowdpulse.social.converter.ISO8601DateConverter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
@Parameters(separators = "=")
public class ExtractionParameters implements IPluginConfig<ExtractionParameters> {

    @Parameter(names = "-source", description = "Source for extraction")
    private String source;

    @Parameter(names = "-query", description = "Text content to search for")
    private String query;

    @Parameter(names = "-location", description = "Location box or area", converter = GeoLocationBoxConverter.class)
    private GeoLocationBox geoLocationBox;

    @Parameter(names = "-from", description = "User identifier the messages must originate from")
    private String fromUser;

    @Parameter(names = "-to", description = "User identifier the messages are destined to")
    private String toUser;

    @Parameter(names = "-ref", description = "User identifiers the messages reference")
    private List<String> referenceUsers;

    @Parameter(names = "-since", description = "Date since searching must start from", converter = ISO8601DateConverter.class)
    private Date since;

    @Parameter(names = "-until", description = "Date until searching must end", converter = ISO8601DateConverter.class)
    private Date until;

    @Parameter(names = "-language", description = "Language of the messages to search for")
    private String language;

    @Parameter(names = "-locale", description = "Locale of the messages to search for")
    private String locale;

    @Parameter(names = "-tags", description = "Tags to add to extracted messages")
    private List<String> tags;

    public String getSource() {
        return source;
    }

    public String getQuery() {
        return query;
    }

    public GeoLocationBox getGeoLocationBox() {
        return geoLocationBox;
    }

    public String getFromUser() {
        return fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public List<String> getReferenceUsers() {
        return referenceUsers;
    }

    public Date getSince() {
        return since;
    }

    public Date getUntil() {
        return until;
    }

    public String getLanguage() {
        return language;
    }

    public String getLocale() {
        return locale;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public void setSince(Date since) {
        this.since = since;
    }

    public void setUntil(Date until) {
        this.until = until;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public void setGeoLocationBox(GeoLocationBox geoLocationBox) {
        this.geoLocationBox = geoLocationBox;
    }

    public void setReferenceUsers(List<String> referenceUsers) {
        this.referenceUsers = referenceUsers;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setTags(String... tags) {
        this.tags = Arrays.asList(tags);
    }

    private List<String> multipleStrings(String input) {
        if (StringUtil.isNullOrEmpty(input)) {
            return null;
        }
        return Arrays.asList(input.split(","));
    }

    @Override public ExtractionParameters buildFromJsonElement(JsonElement json) {
        ExtractionParameters extractionParameters = PluginConfigHelper.buildFromJson(json, ExtractionParameters.class);
        GeoLocationBoxConverter geoConverter = new GeoLocationBoxConverter();
        JsonElement location;
        if ((location = json.getAsJsonObject().get("location")) != null) {
            this.setGeoLocationBox(geoConverter.convert(location.getAsString()));
        }
        return extractionParameters;
    }
}
