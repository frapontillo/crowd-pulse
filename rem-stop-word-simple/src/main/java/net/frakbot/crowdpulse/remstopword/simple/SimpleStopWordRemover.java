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

package net.frakbot.crowdpulse.remstopword.simple;

import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.remstopword.IStopWordRemoverOperator;
import org.apache.commons.io.IOUtils;
import rx.Observable;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class SimpleStopWordRemover extends IPlugin<Message> {
    private final static String STOPWORDREMOVER_IMPL = "simple";
    private final HashMap<String, HashSet<String>> dictionaries;
    private final List<String> punctuation = Arrays.asList(".",",",":",";","?","!","(",")","[","]","{","}");

    public SimpleStopWordRemover() {
        dictionaries = new HashMap<>();
    }

    @Override public String getName() {
        return STOPWORDREMOVER_IMPL;
    }

    @Override public Observable.Operator<Message, Message> getOperator() {
        return new IStopWordRemoverOperator() {
            @Override public List<Token> stopWordRemoveMessageTokens(Message message) {
                List<Token> tokens = message.getTokens();
                if (tokens != null) {
                    // for each token, reset the "stop word" property by looking up the word in the proper dictionary
                    tokens.forEach(token -> token.setStopWord(isStopWord(token.getText(), message.getLanguage())));
                }
                return tokens;
            }
        };
    }

    /**
     * Return the language-specific dictionary, building it if it wasn't built sooner.
     * If the dictionary cannot be built (maybe because files were missing or because files were empty), no error is
     * thrown, and the returned dictionary will simply be empty, thus removing no words at all.
     *
     * @param language The language referencing the dictionary.
     * @return A {@link HashSet<String>} containing all of the dictionary terms.
     */
    private HashSet<String> getDictionary(String language) {
        if (!dictionaries.containsKey(language)) {
            HashSet<String> newDictionary = new HashSet<>();
            newDictionary.addAll(punctuation);

            try {
                // TODO: consider checking for invalid lines
                List<String> lines = IOUtils.readLines(
                        getClass().getClassLoader().getResourceAsStream("stop-words-" + language));
                newDictionary.addAll(lines);
            } catch (IOException e) {
                e.printStackTrace();
            }

            dictionaries.put(language, newDictionary);
        }
        return dictionaries.get(language);
    }

    /**
     * Check if a word is considered as a stop-word in the given language.
     * @param word The {@link String} to check.
     * @param language Specifies the dictionary to be used to check for stop words.
     * @return true if the word is considered a stop word, false otherwise.
     */
    private boolean isStopWord(String word, String language) {
        HashSet<String> dict = getDictionary(language);
        return dict.contains(word.toLowerCase());
    }
}
