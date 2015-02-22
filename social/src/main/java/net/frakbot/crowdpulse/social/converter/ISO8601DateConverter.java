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
import org.joda.time.DateTime;

import java.util.Date;

/**
 * @author Francesco Pontillo
 */
public class ISO8601DateConverter implements IStringConverter<Date> {
    @Override public Date convert(String value) {
        if (value == null) {
            return null;
        }
        return new DateTime(value).toDate();
    }
}