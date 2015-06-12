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

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.model.GeocodingResult;
import net.frakbot.crowdpulse.common.util.CrowdLogger;

import java.io.InputStream;
import java.util.Properties;

/**
 * @author Francesco Pontillo
 */
public class GeoLocationBox {
    private static final String PROP_GEOCODING_APIKEY = "geocoding.apiKey";
    private static final double EARTH_RADIUS = 6371.01; // earth radius in km
    private static GeoApiContext context;

    private String location;
    private double latitude;
    private double longitude;
    private double distance;
    private double[][] boundingBox;

    public GeoLocationBox(String location) {
        setLocation(location);
    }

    /**
     * Construct a geo box, given its center coordinates and the distance from it in kilometers.
     *
     * @param longitude The longitude of the box center.
     * @param latitude  The latitude of the box center.
     * @param distance  The distance from the box center, in kilometers.
     */
    public GeoLocationBox(double longitude, double latitude, double distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
        onChangedLngLatDist();
    }

    public GeoLocationBox(double swLng, double swLat, double neLng, double neLat) {
        setBoundingBox(new double[][]{
                {swLng, swLat}, {neLng, neLat}
        });
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
        onChangedLngLatDist();
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
        onChangedLngLatDist();
    }

    /**
     * Get the distance in kilometers.
     *
     * @return the distance from the box center, in km.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Set the distance from the box center in kilometers.
     *
     * @param distance the distance from the box center, in km.
     */
    public void setDistance(double distance) {
        this.distance = distance;
        onChangedLngLatDist();
    }

    /**
     * Get the bounding box as a double[][].
     *
     * @return The bounding box of the geo component.
     */
    public double[][] getBoundingBox() {
        return boundingBox;
    }

    public void setBoundingBox(double[][] boundingBox) {
        this.boundingBox = boundingBox;
        onChangedBoundingBox();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        // retrieve the bounding box from the Google Maps API
        boundingBox = new double[2][2];
        try {
            GeocodingResult[] results = GeocodingApi.newRequest(getGeoApiContext()).address(location).await();
            if (results != null && results.length > 0) {
                // southwest
                boundingBox[0][0] = results[0].geometry.bounds.southwest.lng;
                boundingBox[0][1] = results[0].geometry.bounds.southwest.lat;
                // northeast
                boundingBox[1][0] = results[0].geometry.bounds.northeast.lng;
                boundingBox[1][1] = results[0].geometry.bounds.northeast.lat;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        onChangedBoundingBox();
    }

    public boolean contains(Double longitude, Double latitude) {
        return (longitude == null && latitude == null)
                || (longitude >= boundingBox[0][0] && longitude <= boundingBox[1][0]
                && latitude >= boundingBox[0][1] && latitude <= boundingBox[1][1]);
    }

    /**
     * Recalculate the bounding box according to the newest values of longitude, latitude and radius.
     */
    private void onChangedLngLatDist() {
        boundingBox = new double[2][2];
        /*
         +------------+ ... lat2
         |            |
         |            |
         |     ++     | lat
         |            |
         |            |
         +------------+ ... lat1
         .    long    .
         .            .
       long1        long2
        */

        // southwest point (long1, lat1)
        boundingBox[0][0] = longitude - Math.toDegrees(distance / EARTH_RADIUS / Math.cos(Math.toRadians(latitude)));
        boundingBox[0][1] = latitude - Math.toDegrees(distance / EARTH_RADIUS);
        // northeast point (long2, lat2)
        boundingBox[1][0] = longitude + Math.toDegrees(distance / EARTH_RADIUS / Math.cos(Math.toRadians(latitude)));
        boundingBox[1][1] = latitude + Math.toDegrees(distance / EARTH_RADIUS);
    }

    /**
     * Recalculate longitude, latitude and radius according to the latest bounding box.
     */
    private void onChangedBoundingBox() {
        double lat1 = Math.toRadians(boundingBox[0][1]);
        double lng1 = Math.toRadians(boundingBox[0][0]);
        double lat2 = Math.toRadians(boundingBox[1][1]);
        double lng2 = Math.toRadians(boundingBox[1][0]);
        latitude = Math.toDegrees(lat2 - (lat2 - lat1) / 2);
        longitude = Math.toDegrees(lng2 - (lng2 - lng1) / 2);
        // TODO: both the following solutions are approximate, find out why
        distance = (Math.toRadians(latitude) - lat1) * EARTH_RADIUS;
        /* ALTERNATIVE APPROACH
        // see http://www.movable-type.co.uk/scripts/latlong-db.html and http://janmatuschek.de/LatitudeLongitudeBoundingCoordinates#Distance
        // d = acos( sin(?1)?sin(?2) + cos(?1)?cos(?2)?cos(??) ) ? R
        // this is the geodesic distance between the two bounding box angle point (opposed points in the circumference)
        // note that the geodesic/2 does not equal the distance, but it equals 2*radius of the circumference (distance is less)
        double geodesicDistance = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lng2 - lng1)) * EARTH_RADIUS / 2;
        // the actual distance can then be calculated with Pythagoras's theorem
        distance = geodesicDistance / Math.sqrt(2);
        */
    }

    private static GeoApiContext getGeoApiContext() {
        if (context == null) {
            context = new GeoApiContext().setApiKey(readApiKey());
        }
        return context;
    }

    private static String readApiKey() {
        InputStream configInput = GeoLocationBox.class.getClassLoader().getResourceAsStream("geocoding.properties");
        Properties prop = new Properties();
        try {
            prop.load(configInput);
            return prop.getProperty(PROP_GEOCODING_APIKEY);
        } catch (Exception exception) {
            CrowdLogger.getLogger(GeoLocationBox.class)
                    .error("Error while loading Google Maps configuration", exception);
            return "";
        }
    }
}
