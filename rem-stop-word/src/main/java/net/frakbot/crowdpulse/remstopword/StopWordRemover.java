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

package net.frakbot.crowdpulse.remstopword;

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.rx.CrowdSubscriber;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Category;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.entity.Token;
import rx.Observable;

import java.util.List;
import java.util.Set;

/**
 * Abstract stop word remover class, handles conversion of the {@link StopWordConfig} from a {@link JsonElement}.
 *
 * @author Francesco Pontillo
 */
public abstract class StopWordRemover extends IPlugin<Message, Message, StopWordConfig> {

    protected abstract boolean isTokenStopWord(String token, String language, StopWordConfig stopWordConfig);

    protected abstract boolean isTagStopWord(String tag, String language, StopWordConfig stopWordConfig);

    protected abstract boolean isCategoryStopWord(String category, String language, StopWordConfig stopWordConfig);

    @Override public StopWordConfig getNewParameter() {
        return new StopWordConfig();
    }

    @Override protected Observable.Operator<Message, Message> getOperator(StopWordConfig parameters) {
        return subscriber -> new CrowdSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                processMessage(message, parameters, parameters);
                subscriber.onNext(message);
            }
        };
    }

    private void processMessage(Message message, StopWordConfig parameters, StopWordConfig stopWordConfig) {
        String language = message.getLanguage();
        // for each element, reset the "stop word" property by looking up the word in the proper dictionary

        // mark tokens
        if (parameters.mustStopTokens()) {
            List<Token> tokens = message.getTokens();
            if (tokens != null) {
                tokens.forEach(token -> token.setStopWord(isTokenStopWord(token.getText(), language, stopWordConfig)));
            }
        }

        // mark tags
        if (parameters.mustStopTags()) {
            Set<Tag> tags = message.getTags();
            if (tags != null) {
                tags.forEach(tag -> {
                    tag.setStopWord(isTagStopWord(tag.getText(), language, stopWordConfig));

                    // for each tag, mark its categories
                    if (parameters.mustStopCategories()) {
                        Set<Category> categories = tag.getCategories();
                        if (categories != null) {
                            categories.forEach(cat -> cat
                                    .setStopWord(isCategoryStopWord(cat.getText(), language, stopWordConfig)));
                        }
                    }
                });
            }
        }
    }
}
