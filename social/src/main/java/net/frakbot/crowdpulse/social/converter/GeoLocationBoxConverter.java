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

package net.frakbot.crowdpulse.social.converter;

import com.beust.jcommander.IStringConverter;
import net.frakbot.crowdpulse.social.extraction.GeoLocationBox;

/**
 * @author Francesco Pontillo
 */
public class GeoLocationBoxConverter implements IStringConverter<GeoLocationBox> {
    @Override public GeoLocationBox convert(String value) {
        if (value == null) {
            return null;
        }
        try {
            String[] components = value.split(",");
            return new GeoLocationBox(Double.parseDouble(components[1]), Double.parseDouble(components[0]), Double
                    .parseDouble(components[2]));
        } catch (Exception ignored) {}
        return new GeoLocationBox(value);
    }
}
