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

import net.frakbot.crowdpulse.data.entity.Category;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.entity.Token;
import net.frakbot.crowdpulse.remstopword.StopWordRemover;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Stop word remover plugin, marks tokens, tags and categories as stop words according to some stop word files.
 *
 * @author Francesco Pontillo
 */
public class SimpleStopWordRemover extends StopWordRemover<StopWordConfig> {
    public final static String PLUGIN_NAME = "simple";
    private final static String LANG_PLACEHOLDER = "{{LANG}}";
    private final HashMap<String, HashSet<String>> dictionaries;
    private final List<String> punctuation = Arrays.asList(".", ",", ":", ";", "?", "!", "(", ")", "[", "]", "{", "}");

    // maps of dictionaries: key is language, value is list of filenames for the specific element (or generic for all)
    private HashMap<String, List<String>> allDictionaries;
    private HashMap<String, List<String>> tokenDictionaries;
    private HashMap<String, List<String>> tagDictionaries;
    private HashMap<String, List<String>> categoryDictionaries;

    public SimpleStopWordRemover() {
        dictionaries = new HashMap<>();
        allDictionaries = new HashMap<>();
        tokenDictionaries = new HashMap<>();
        tagDictionaries = new HashMap<>();
        categoryDictionaries = new HashMap<>();
    }

    @Override public String getName() {
        return PLUGIN_NAME;
    }

    @Override public StopWordConfig getNewParameter() {
        return new StopWordConfig();
    }

    @Override protected boolean isTokenStopWord(String token, String language, StopWordConfig stopWordConfig) {
        List<String> tokenDictionariesNames =
                union(getAllDictionaries(stopWordConfig, language), getTokenDictionaries(stopWordConfig, language));
        return isStopWord(token, tokenDictionariesNames);
    }

    @Override protected boolean isTagStopWord(String tag, String language, StopWordConfig stopWordConfig) {
        List<String> tagDictionariesNames =
                union(getAllDictionaries(stopWordConfig, language), getTagDictionaries(stopWordConfig, language));
        return isStopWord(tag, tagDictionariesNames);
    }

    @Override protected boolean isCategoryStopWord(String category, String language, StopWordConfig stopWordConfig) {
        List<String> categoryDictionariesNames =
                union(getAllDictionaries(stopWordConfig, language), getCategoryDictionaries(stopWordConfig, language));
        return isStopWord(category, categoryDictionariesNames);
    }

    @Override protected void processMessage(Message message, StopWordConfig stopWordConfig) {
        String language = message.getLanguage();
        // for each element, reset the "stop word" property by looking up the word in the proper dictionary

        // mark tokens
        if (stopWordConfig.mustStopTokens()) {
            List<Token> tokens = message.getTokens();
            if (tokens != null) {
                tokens.forEach(token -> token.setStopWord(isTokenStopWord(token.getText(), language, stopWordConfig)));
            }
        }

        // mark tags
        if (stopWordConfig.mustStopTags()) {
            Set<Tag> tags = message.getTags();
            if (tags != null) {
                tags.forEach(tag -> {
                    tag.setStopWord(isTagStopWord(tag.getText(), language, stopWordConfig));

                    // for each tag, mark its categories
                    if (stopWordConfig.mustStopCategories()) {
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

    /**
     * Return the dictionary specified by a file name, building it if it wasn't built sooner.
     * If the dictionary cannot be built (maybe because files were missing or because files were empty), no error is
     * thrown, and the returned dictionary will simply be empty, thus removing no words at all.
     *
     * @param fileName The name of the file referencing the dictionary (should exist in the classpath resources).
     * @return A {@link HashSet<String>} containing all of the dictionary terms.
     */
    private HashSet<String> getDictionaryByFileName(String fileName) {
        if (!dictionaries.containsKey(fileName)) {
            HashSet<String> newDictionary = new HashSet<>();

            try {
                List<String> lines = IOUtils.readLines(
                        getClass().getClassLoader().getResourceAsStream(fileName));
                lines = lines.stream()
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                newDictionary.addAll(lines);
            } catch (Exception ignored) {
            }

            dictionaries.put(fileName, newDictionary);
        }
        return dictionaries.get(fileName);
    }

    /**
     * Return a dictionary containing all of the term included in the files whose names are included in the input list.
     *
     * @param fileNames A {@link List} of {@link String} representing the files to read.
     * @return A {@link HashSet<String>} containing all of the dictionary terms.
     */
    private HashSet<String> getDictionariesByFileNames(List<String> fileNames) {
        HashSet<String> set = new HashSet<>();
        for (String fileName : fileNames) {
            set.addAll(getDictionaryByFileName(fileName));
        }
        set.addAll(punctuation);
        return set;
    }

    /**
     * Check if a word is considered as a stop-word among the input dictionaries.
     *
     * @param word      The {@link String} to check.
     * @param fileNames Specifies the list of dictionary files to be used to check for stop words.
     * @return true if the word is considered a stop word, false otherwise.
     */
    private boolean isStopWord(String word, List<String> fileNames) {
        HashSet<String> dict = getDictionariesByFileNames(fileNames);
        return dict.contains(word.toLowerCase());
    }

    private List<String> getAllDictionaries(StopWordConfig stopWordConfig, String language) {
        if (allDictionaries.get(language) == null) {
            List<String> languageDictionaries = stopWordConfig.getDictionaries().getAll();
            allDictionaries.put(language, replaceWithLang(languageDictionaries, language));
        }
        return allDictionaries.get(language);
    }

    private List<String> getTokenDictionaries(StopWordConfig stopWordConfig, String language) {
        if (tokenDictionaries.get(language) == null) {
            List<String> languageDictionaries = stopWordConfig.getDictionaries().getTokens();
            tokenDictionaries.put(language, replaceWithLang(languageDictionaries, language));
        }
        return tokenDictionaries.get(language);
    }

    private List<String> getTagDictionaries(StopWordConfig stopWordConfig, String language) {
        if (tagDictionaries.get(language) == null) {
            List<String> languageDictionaries = stopWordConfig.getDictionaries().getTags();
            tagDictionaries.put(language, replaceWithLang(languageDictionaries, language));
        }
        return tagDictionaries.get(language);
    }

    private List<String> getCategoryDictionaries(StopWordConfig stopWordConfig, String language) {
        if (categoryDictionaries.get(language) == null) {
            List<String> languageDictionaries = stopWordConfig.getDictionaries().getCategories();
            categoryDictionaries.put(language, replaceWithLang(languageDictionaries, language));
        }
        return categoryDictionaries.get(language);
    }

    private List<String> union(List<String>... lists) {
        List<String> resulting = new ArrayList<>();
        for (List<String> list : lists) {
            resulting.addAll(list);
        }
        return resulting;
    }

    private List<String> replaceWithLang(List<String> elems, String language) {
        List<String> normalized;
        if (elems != null) {
            normalized = new ArrayList<>(elems.size());
            for (String dict : elems) {
                if (dict.contains(LANG_PLACEHOLDER)) {
                    dict = dict.replace(LANG_PLACEHOLDER, language);
                }
                normalized.add(dict);
            }
        } else {
            normalized = new ArrayList<>(0);
        }
        return normalized;
    }
}
