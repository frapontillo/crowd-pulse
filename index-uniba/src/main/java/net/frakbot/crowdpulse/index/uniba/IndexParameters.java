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

import com.google.gson.JsonElement;
import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.spi.IPluginConfig;
import net.frakbot.crowdpulse.common.util.spi.PluginConfigHelper;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

/**
 * Indexing parameters class for the Uniba Indexing Web Service.
 *
 * @author Francesco Pontillo
 */
public class IndexParameters implements IPluginConfig<IndexParameters> {
    private String schema;

    private final static List<String> SCHEMAS = Arrays.asList("TFIDF", "BM25", "TF");
    private final static Logger logger = CrowdLogger.getLogger(IndexParameters.class);

    /**
     * Get the schema to use for indexing.
     *
     * @return The indexing schema.
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Set the schema to use for indexing.
     *
     * @param schema The indexing schema, can be one of "TFIDF", "BM25", "TF".
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    @Override public IndexParameters buildFromJsonElement(JsonElement json) {
        IndexParameters parameters = PluginConfigHelper.buildFromJson(json, IndexParameters.class);
        String candidateSchema = parameters.getSchema();
        if (!SCHEMAS.contains(candidateSchema)) {
            logger.warn("The indexing schema you specified ({}) is not supported. Defaulting to TFIDF.",
                    candidateSchema);
            candidateSchema = "TFIDF";
        }
        parameters.setSchema(candidateSchema);
        return parameters;
    }
}
