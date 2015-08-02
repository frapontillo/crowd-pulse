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

package net.frakbot.crowdpulse.index.uniba;

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Indexing parameters class for the Uniba Indexing Web Service.
 *
 * @author Francesco Pontillo
 */
public class IndexParameters implements IPluginConfig {
    private String schema;
    private final static List<String> SCHEMAS = Arrays.asList("TFIDF", "BM25", "TF");
    private final static Logger logger = CrowdLogger.getLogger(IndexParameters.class);

    @Override public IndexParameters buildFromMap(Map<String, String> mapConfig) {
        String candidateSchema = mapConfig.get("schema");
        if (!SCHEMAS.contains(candidateSchema)) {
            logger.warn("The indexing schema you specified ({}) is not supported. Defaulting to TFIDF.",
                    candidateSchema);
            candidateSchema = "TFIDF";
        }
        this.setSchema(candidateSchema);
        return this;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}
