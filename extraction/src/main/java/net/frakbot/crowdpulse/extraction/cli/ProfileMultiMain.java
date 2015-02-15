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

package net.frakbot.crowdpulse.extraction.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.extraction.util.Logger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class ProfileMultiMain {
    public static void main(String[] args) throws IOException {

        long startTime = System.nanoTime();
        Logger.getLogger().debug("Multiple profiling started.");

        GenericMultiParameters params = new GenericMultiParameters();
        new JCommander(params, args);
        Logger.getLogger().debug("Parameters read.");

        List<String> lines = Files.readAllLines(Paths.get(params.getFile().getAbsolutePath()), Charset.forName("UTF-8"));
        if (lines != null) {
            for (String line : lines) {
                ProfileMain.main(line.split(" "));
            }
        }

        long endTime = System.nanoTime();
        double durationSeconds = ((endTime - startTime) * Math.pow(10, -9));

        Logger.getLogger().info("Multiple profiling completed in {} seconds.", durationSeconds);
    }
}
