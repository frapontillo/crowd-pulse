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

/**
 * @author Francesco Pontillo
 */
public class GeoLocationBox {
    private double latitude;
    private double longitude;
    private double distance;

    /**
     * Construct a geo box, given its center coordinates and the distance from it in kilometers.
     * @param latitude  The latitude of the box center.
     * @param longitude The longitude of the box center.
     * @param distance  The distance from the box center, in kilometers.
     */
    public GeoLocationBox(double latitude, double longitude, double distance) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.distance = distance;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the distance in kilometers.
     * @return the distance from the box center, in km.
     */
    public double getDistance() {
        return distance;
    }

    /**
     * Set the distance from the box center in kilometers.
     * @param distance the distance from the box center, in km.
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * Get the bounding box as a double[][].
     * @return The bounding box of the geo component.
     */
    public double[][] getBoundingBox() {
        double R = 6371; // earth radius in km
        double[][] boundingBox = new double[2][2];
        boundingBox[0][0] = longitude - Math.toDegrees(distance/R/Math.cos(Math.toRadians(latitude)));
        boundingBox[0][1] = longitude + Math.toDegrees(distance/R/Math.cos(Math.toRadians(latitude)));
        boundingBox[1][0] = latitude + Math.toDegrees(distance/R);
        boundingBox[1][1] = latitude - Math.toDegrees(distance/R);
        return boundingBox;
    }

    public boolean contains(double latitude, double longitude) {
        // TODO: implement this
        return true;
    }
}
