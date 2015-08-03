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

package net.frakbot.crowdpulse.fixgeoprofile.googlemaps;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.StringUtil;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.common.util.spi.VoidConfig;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.fixgeoprofile.IProfileGeoFixerOperator;
import rx.Observable;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Francesco Pontillo
 */
public class GoogleMapsProfileGeoFixer extends IPlugin<Profile, Profile, VoidConfig> {
    public final static String PLUGIN_NAME = "googlemaps";
    private final static String PROP_GEOCODING_APIKEY = "geocoding.apiKey";
    private final GeoApiContext context;

    public GoogleMapsProfileGeoFixer() {
        context = new GeoApiContext().setApiKey(readApiKey());
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public VoidConfig getNewParameter() {
        return new VoidConfig();
    }

    @Override protected Observable.Operator<Profile, Profile> getOperator(VoidConfig parameters) {
        return new IProfileGeoFixerOperator() {
            @Override public Double[] getCoordinates(Profile profile) {
                if (StringUtil.isNullOrEmpty(profile.getLocation())) {
                    return null;
                }
                GeocodingResult[] results = null;
                Double[] coordinates = null;
                // attempt a forward geocoding (from address to lat-lng)
                try {
                    results = GeocodingApi.newRequest(context).address(profile.getLocation()).await();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // edit and notify the profile only if lat-lng coordinates were found
                if (results != null && results.length > 0) {
                    coordinates = new Double[]{results[0].geometry.location.lat, results[0].geometry.location.lng};
                }
                return coordinates;
            }
        };
    }

    private String readApiKey() {
        InputStream configInput = getClass().getClassLoader().getResourceAsStream("geocoding.properties");
        Properties prop = new Properties();
        try {
            prop.load(configInput);
            return prop.getProperty(PROP_GEOCODING_APIKEY);
        } catch (Exception exception) {
            CrowdLogger.getLogger(GoogleMapsProfileGeoFixer.class)
                    .error("Error while loading Google Maps configuration", exception);
            return "";
        }
    }
}
