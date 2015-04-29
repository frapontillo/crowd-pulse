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

package net.frakbot.crowdpulse.playground.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.GenericAnalysisParameters;
import net.frakbot.crowdpulse.data.entity.Message;
import net.frakbot.crowdpulse.data.entity.Profile;
import net.frakbot.crowdpulse.data.entity.Tag;
import net.frakbot.crowdpulse.data.repository.MessageRepository;
import net.frakbot.crowdpulse.data.repository.ProfileRepository;
import net.frakbot.crowdpulse.data.repository.TagRepository;
import org.apache.logging.log4j.Logger;
import rx.Observable;

/**
 * @author Francesco Pontillo
 */
public class MainHelper {
    private final static Logger logger = CrowdLogger.getLogger(MainHelper.class);

    public static Logger getLogger() {
        return logger;
    }

    public static GenericAnalysisParameters start(String[] args) {
        logger.info("Started.");

        // read parameters
        GenericAnalysisParameters params = new GenericAnalysisParameters();
        new JCommander(params, args);
        logger.info("Parameters read.");

        return params;
    }

    public static Observable<Message> getMessages(GenericAnalysisParameters params) {
        MessageRepository messageRepository = new MessageRepository();
        return messageRepository.getBetweenIdsAsObservable(
                params.getFrom(), params.getTo());
    }

    public static Observable<Message> getLanguageDetectionMessageCandidates(String fromId, String toId) {
        MessageRepository messageRepository = new MessageRepository();
        return messageRepository.getLanguageDetectionCandidatesObservable(fromId, toId);
    }

    public static Observable<Message> getGeoConsolidationMessageCandidates(String fromId, String toId) {
        MessageRepository messageRepository = new MessageRepository();
        return messageRepository.getGeoConsolidationCandidatesObservable(fromId, toId);
    }

    public static Observable<Profile> getGeoConsolidationProfileCandidates(String fromId, String toId) {
        ProfileRepository profileRepository = new ProfileRepository();
        return profileRepository.getGeoConsolidationCandidatesObservable(fromId, toId);
    }

    public static Observable<Tag> getTags(GenericAnalysisParameters params) {
        TagRepository tagRepository = new TagRepository();
        return tagRepository.getBetweenIdsAsObservable(
                params.getFrom(), params.getTo());
    }
}
