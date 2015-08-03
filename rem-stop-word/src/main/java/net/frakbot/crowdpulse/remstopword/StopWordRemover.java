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
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.data.entity.Message;
import rx.Observable;

/**
 * Abstract stop word remover class, handles conversion of the {@link IPluginConfig} from a {@link JsonElement}.
 *
 * @author Francesco Pontillo
 */
public abstract class StopWordRemover<Config extends IPluginConfig<Config>> extends IPlugin<Message, Message, Config> {

    protected abstract boolean isTokenStopWord(String token, String language, Config stopWordConfig);

    protected abstract boolean isTagStopWord(String tag, String language, Config stopWordConfig);

    protected abstract boolean isCategoryStopWord(String category, String language, Config stopWordConfig);

    @Override protected Observable.Operator<Message, Message> getOperator(Config parameters) {
        return subscriber -> new CrowdSubscriber<Message>(subscriber) {
            @Override public void onNext(Message message) {
                processMessage(message, parameters);
                subscriber.onNext(message);
            }
        };
    }

    protected abstract void processMessage(Message message, Config stopWordConfig);
}
