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

package net.frakbot.crowdpulse.social.profile;

/**
 * @author Francesco Pontillo
 */
public class ProfileKey {
    private String source;
    private String id;

    public ProfileKey(String source, String id) {
        this.source = source;
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ProfileKey)) {
            return false;
        }
        ProfileKey ref = (ProfileKey) obj;
        return this.source.equals(ref.source) &&
                this.id.equals(ref.id);
    }

    @Override
    public int hashCode() {
        return source.hashCode() ^ id.hashCode();
    }
}
