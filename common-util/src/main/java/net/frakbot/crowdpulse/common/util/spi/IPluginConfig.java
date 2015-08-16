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

package net.frakbot.crowdpulse.common.util.spi;

import com.google.gson.JsonElement;

/**
 * Interface for {@link IPlugin} configuration classes.
 *
 * @author Francesco Pontillo
 */
public interface IPluginConfig<T> {
    T buildFromJsonElement(JsonElement json);

    /**
     * Delegate building the actual configuration to the instance {@link #buildFromJsonElement(JsonElement)} method.
     *
     * @param obj  The instance that will be used to convert from JSON.
     * @param json The root {@link JsonElement} to convert.
     * @param <T>  Generic type of the input instance.
     * @return The converted object, as the same type as the input instance.
     */
    static <T extends IPluginConfig<T>> T buildFromJson(T obj, JsonElement json) {
        return obj.buildFromJsonElement(json);
    }
}
