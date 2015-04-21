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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class Tag extends GenericEntity<String> {

    private List<String> sources;

    public String getText() {
        return getId();
    }

    public void setText(String text) {
        setId(text);
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public void addSource(String source) {
        if (this.sources == null) {
            this.sources = new ArrayList<String>();
        }
        this.sources.add(source);
    }
}
