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

package net.frakbot.crowdpulse.fixgeomessage;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;

/**
 * @author Francesco Pontillo
 */
public abstract class IMessageGeoFixer implements IPlugin {
    public abstract Double[] getCoordinates(Message message);

    public Message geoFixMessage(Message message) {
        Double[] coordinates = getCoordinates(message);
        if (coordinates != null && coordinates.length == 2) {
            message.setLatitude(coordinates[0]);
            message.setLongitude(coordinates[1]);
        }
        return message;
    }
}
