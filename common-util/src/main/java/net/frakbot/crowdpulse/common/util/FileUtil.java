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

package net.frakbot.crowdpulse.common.util;

import java.io.*;

/**
 * Utility class for reading {@link File}s.
 *
 * @author Francesco Pontillo
 */
public class FileUtil {

    /**
     * Read a {@link File} from a given path or resource name. The path will be checked first, if it
     * there is not file then the {@code fileName} will be considered a resource file.
     *
     * @param fileName           The path of the file or the name of the resource to read (in this order).
     * @param resourceClassOwner The class owner of the resource.
     * @return An {@link InputStream} to read the file contents from.
     * @throws FileNotFoundException If the file can't be found in the path or among the resources.
     */
    public static InputStream readFileFromPathOrResource(String fileName, Class resourceClassOwner)
            throws FileNotFoundException {

        InputStream inputStream;
        try {
            return new FileInputStream(fileName);
        } catch (FileNotFoundException ignored) {
        }

        inputStream = resourceClassOwner.getClassLoader().getResourceAsStream(fileName);

        if (inputStream == null) {
            throw new FileNotFoundException();
        }
        return inputStream;

    }

}
