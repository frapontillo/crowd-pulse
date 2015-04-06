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

package net.frakbot.crowdpulse.tag.tagme;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class TagMeResponse {
    private List<TagMeAnnotation> annotations;

    public List<TagMeAnnotation> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<TagMeAnnotation> annotations) {
        this.annotations = annotations;
    }

    public class TagMeAnnotation {
        private String title;

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
