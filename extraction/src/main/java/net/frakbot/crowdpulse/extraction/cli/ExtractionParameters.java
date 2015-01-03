/*
 * Copyright 2014 Francesco Pontillo
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

package net.frakbot.crowdpulse.extraction.cli;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.frakbot.crowdpulse.extraction.geo.GeoLocationBox;

import java.util.Date;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
@Parameters(separators = "=")
public class ExtractionParameters {

    @Parameter(names = "-source", description = "Source for extraction")
    private String source;

    @Parameter(names = "-query", description = "Text content to search for")
    private String query;

    @Parameter(names = "-location", description = "Location box", converter = GeoLocationBoxConverter.class)
    private GeoLocationBox geoLocationBox;

    @Parameter(names = "-area", description = "Location area")
    private String geoArea;

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

    public String getSource() {
        return source;
    }

    public String getQuery() {
        return query;
    }

    public GeoLocationBox getGeoLocationBox() {
        return geoLocationBox;
    }

    public String getGeoArea() {
        return geoArea;
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
}
