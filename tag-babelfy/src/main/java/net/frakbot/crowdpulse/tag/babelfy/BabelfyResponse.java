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

package net.frakbot.crowdpulse.tag.babelfy;

import com.google.gson.annotations.SerializedName;
import net.frakbot.crowdpulse.common.util.StringUtil;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class BabelfyResponse {

    @SerializedName("taggedText")
    private List<BabelfyTaggedText> taggedTexts;
    private List<BabelfyAnnotation> annotations;

    public List<BabelfyTaggedText> getTaggedTexts() {
        return taggedTexts;
    }

    public void setTaggedTexts(List<BabelfyTaggedText> tags) {
        this.taggedTexts = tags;
    }

    public List<BabelfyAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<BabelfyAnnotation> annotations) {
        this.annotations = annotations;
    }

    public List<String> getTags() {
        List<String> tags = new ArrayList<String>(getAnnotations().size());
        for (BabelfyAnnotation annotation : getAnnotations()) {
            tags.add(getTag(annotation.getStart(), annotation.getEnd()));
        }
        return tags;
    }

    private String getTag(int start, int end) {
        List<BabelfyTaggedText> taggedTexts = getTaggedTexts().subList(start, end);
        List<String> texts = new ArrayList<String>(taggedTexts.size());
        for (BabelfyTaggedText taggedText : taggedTexts) {
            texts.add(taggedText.getLemma());
        }
        return StringUtil.join(texts, " ");
    }

    public class BabelfyTaggedText {
        private String lemma;
        private String tag;
        private String word;

        private final String encoding = "UTF-8";

        public String getLemma() {
            return decode(lemma);
        }

        public void setLemma(String lemma) {
            this.lemma = lemma;
        }

        public String getTag() {
            return decode(tag);
        }

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getWord() {
            return decode(word);
        }

        public void setWord(String word) {
            this.word = word;
        }

        private String decode(String string) {
            try {
                return URLDecoder.decode(string, encoding);
            } catch (UnsupportedEncodingException e) {
                return string;
            }
        }
    }

    public class BabelfyAnnotation {
        private String id;
        private int start;
        private int end;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }
    }

}
