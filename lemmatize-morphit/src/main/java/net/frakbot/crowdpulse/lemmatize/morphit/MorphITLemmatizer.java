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

package net.frakbot.crowdpulse.lemmatize.morphit;

import net.frakbot.crowdpulse.common.util.spi.ISingleablePlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.lemmatize.ILemmatizerOperator;
import org.apache.commons.io.IOUtils;
import rx.Observable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.regex.Pattern;

/**
 * The MorphIT lemmatizer is able to execute a lemmatization process when given:
 * <ul>
 *     <li>an Italian word</li>
 *     <li>a MorphIT specific POS tag</li>
 * </ul>
 *
 * Since CrowdPulse handles Italian POS tags within the standard
 * <a href="http://medialab.di.unipi.it/wiki/Tanl_POS_Tagset">TANL tagset</a>, a conversion process is needed
 * before calling MorphIT.
 *
 * This implementation therefore uses a resources file, called "tanl-morphit" to achieve the aforementioned mapping.
 *
 * @author Francesco Pontillo
 */
public class MorphITLemmatizer extends ISingleablePlugin<Message, Void> {
    private static final Pattern spacePattern = Pattern.compile("\\s+");
    private static final String LEMMATIZER_IMPL = "lemmatizer-it";

    // <key:(tanl-tag), value:(morphit-tag-1,...)>
    private HashMap<String, HashSet<String>> tanlMorphITMap;
    // <key:(word), value:(morphit-tag,lemma)>
    private HashMap<String, List<String[]>> morphITDictionary;

    public MorphITLemmatizer() {
        // build the TANL-MorphIT dictionary as <key:(tanl-tag), value:(morphit-tag-1,...)>
        InputStream mapStream = MorphITLemmatizer.class.getClassLoader().getResourceAsStream("tanl-morphit");
        tanlMorphITMap = new HashMap<>();
        try {
            List<String> mapLines = IOUtils.readLines(mapStream, Charset.forName("UTF-8"));
            mapLines.forEach(s -> {
                // for each line, split using spaces
                String[] values = spacePattern.split(s);
                if (values.length > 0) {
                    // the first token is the key
                    String key = values[0];
                    // all subsequent tokens are possible values
                    HashSet<String> valueSet = new HashSet<>();
                    valueSet.addAll(Arrays.asList(values).subList(1, values.length));
                    tanlMorphITMap.put(key, valueSet);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        // build the MorphIT dictionary as <key:(word), value:(morphit-tag,lemma)>
        InputStream dictStream = MorphITLemmatizer.class.getClassLoader().getResourceAsStream("morphit");
        morphITDictionary = new HashMap<>();
        try {
            List<String> mapLines = IOUtils.readLines(dictStream, Charset.forName("UTF-8"));
            mapLines.forEach(s -> {
                // for each line, split using spaces
                String[] values = spacePattern.split(s);
                if (values.length == 3) {
                    // the key is made of first and third token
                    // the value is the second token
                    List<String[]> candidates = morphITDictionary.get(values[0]);
                    if (candidates == null) {
                        candidates = new ArrayList<>();
                    }
                    candidates.add(new String[] {values[2], values[1]});
                    morphITDictionary.put(values[0], candidates);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override public String getName() {
        return LEMMATIZER_IMPL;
    }

    @Override public Observable.Operator<Message, Message> getOperator() {
        MorphITLemmatizer currentLemmatizer = this;
        return new ILemmatizerOperator() {
            @Override public List<Token> lemmatizeMessageTokens(Message message) {
                return currentLemmatizer.singleProcess(message).getTokens();
            }
        };
    }

    @Override public Message singleProcess(Message message) {
        List<Token> tokens = message.getTokens();
        tokens.forEach(this::lemmatizeToken);
        return message;
    }

    private void lemmatizeToken(Token token) {
        String word = token.getText().toLowerCase();
        String pos = token.getPos();

        // lookup the POS in the tanlMorphITMap
        Set<String> morphITTags = tanlMorphITMap.get(pos);

        // get all the candidate lemmas
        List<String[]> candidateLemmas = morphITDictionary.get(word);

        // if there's no candidate or no match, return
        if (candidateLemmas == null || morphITTags == null) {
            return;
        }

        // for each candidate lemma
        String lemma = null;
        for (String[] candidateLemma : candidateLemmas) {
            // check if the candidate lemma is child to any of the morphITTags
            for (String morphITTag : morphITTags) {
                // if so, set the candidateLemma[1] as the found lemma and break the loop
                if (MorphITTag.isTagChildOfTag(candidateLemma[0], morphITTag)) {
                    lemma = candidateLemma[1];
                    break;
                }
            }
            if (lemma != null) {
                break;
            }
        }

        token.setLemma(lemma);
    }
}
