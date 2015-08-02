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

import net.frakbot.crowdpulse.data.entity.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * Models a request of indexing by specifying the ID of the index to use, the language of the elements and a list of
 * contents to be indexed.
 *
 * @author Francesco Pontillo
 */
public class IndexRequest {
    private String id;
    private String lang;
    private List<TextContent> content;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public List<TextContent> getContent() {
        return content;
    }

    public void setContent(List<TextContent> content) {
        this.content = content;
    }

    public void setContents(List<Message> messages) {
        this.content = new ArrayList<>(messages.size());
        messages.forEach(message -> this.content.add(new TextContent(message.getoId(), message.getText())));
    }
}
