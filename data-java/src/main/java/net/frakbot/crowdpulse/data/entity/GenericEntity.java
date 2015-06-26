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

package net.frakbot.crowdpulse.data.entity;

import org.mongodb.morphia.annotations.Id;

/**
 * Generic entities that will be stored in a Mongo database.
 * Every {@link GenericEntity} will have at least a {@code id} of some kind.
 *
 * @author Francesco Pontillo
 */
public class GenericEntity<T> {
    @Id private T id;

    /**
     * Get the ID of the GenericEntity.
     *
     * @return The ID of the object.
     */
    public T getId() {
        return id;
    }

    /**
     * Set the ID of the GenericEntity. If the object already has an ID, an {@link UnsupportedOperationException} will
     * be thrown.
     *
     * @param id The ID of the object.
     * @throws UnsupportedOperationException if the ID was already set.
     */
    public void setId(T id) {
        if (this.id != null) {
            throw new UnsupportedOperationException("Can't change the ID of an Entity.");
        }
        this.id = id;
    }
}
