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

package net.frakbot.crowdpulse.index.uniba.rest;

import java.util.HashMap;

/**
 * Element to index, containing an ID and a textual content.
 *
 * @author Francesco Pontillo
 */
public class TextContent {
    private String id;
    private String content;
    private HashMap<String, Integer> targets;

    public TextContent(String id, String content) {
        this.id = id;
        this.content = content;
        this.targets = new HashMap<>();
        this.targets.put(id, 1);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public HashMap<String, Integer> getTargets() {
        return targets;
    }

    public void setTargets(HashMap<String, Integer> targets) {
        this.targets = targets;
    }
}
