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

package net.frakbot.crowdpulse.social.test.extraction;

import net.frakbot.crowdpulse.social.extraction.GeoLocationBox;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Francesco Pontillo
 */
public class GeoLocationBoxTest {
    private final double lng = 16.6001289;
    private final double lat = 40.6646065;
    private final double dis = 10;

    private final double lng1 = 16.48156907003985;
    private final double lat1 = 40.57467448056653;
    private final double lng2 = 16.718688729960153;
    private final double lat2 = 40.75453851943347;

    @Test
    public void testLngLatDis2BoundingBox() {
        GeoLocationBox lngLatDisBox = new GeoLocationBox(lng, lat, dis);
        Assert.assertEquals(lng1, lngLatDisBox.getBoundingBox()[0][0], 0);
        Assert.assertEquals(lat1, lngLatDisBox.getBoundingBox()[0][1], 0);
        Assert.assertEquals(lng2, lngLatDisBox.getBoundingBox()[1][0], 0);
        Assert.assertEquals(lat2, lngLatDisBox.getBoundingBox()[1][1], 0);
    }

    @Test
    public void testBoundingBox2LngLatDisBox() {
        GeoLocationBox lngLatDisBox = new GeoLocationBox(
                lng1, lat1, lng2, lat2);
        Assert.assertEquals(lng, lngLatDisBox.getLongitude(), 0);
        Assert.assertEquals(lat, lngLatDisBox.getLatitude(), 0);
        Assert.assertEquals(dis, lngLatDisBox.getDistance(), 10E-5);
    }

    @Test
    public void testGoogleMapsAPI() {
        GeoLocationBox lngLatDisBox = new GeoLocationBox("Matera, Italia");
        Assert.assertEquals(lng, lngLatDisBox.getLongitude(), 0);
        Assert.assertEquals(lat, lngLatDisBox.getLatitude(), 0);
        Assert.assertTrue(lngLatDisBox.getDistance() <= dis);
    }
}
