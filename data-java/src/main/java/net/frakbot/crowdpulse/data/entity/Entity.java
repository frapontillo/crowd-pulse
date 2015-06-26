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

import org.bson.types.ObjectId;

/**
 * Specific implementation of {@link GenericEntity} where {@code id}s are {@link ObjectId}s.
 *
 * @author Francesco Pontillo
 */
public class Entity extends GenericEntity<ObjectId> {
    /**
     * Constructor of the Entity, sets an newly created {@link #id}.
     */
    public Entity() {
        this.setId(new ObjectId());
    }
}
