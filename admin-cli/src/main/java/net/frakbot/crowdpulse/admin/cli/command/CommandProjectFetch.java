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

package net.frakbot.crowdpulse.admin.cli.command;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import net.frakbot.crowdpulse.admin.cli.operation.Operation;
import net.frakbot.crowdpulse.admin.cli.operation.OperationProjectFetch;

import java.io.File;

/**
 * @author Francesco Pontillo
 */
@Parameters(commandNames = CommandProjectFetch.COMMAND_NAME)
public class CommandProjectFetch extends Command {
    public static final String COMMAND_NAME = "project-fetch";

    @Parameter(names = "--id", description = "The ID of the project to fetch")
    private String id;

    @Parameter(names = "--output", description = "The file to save the project to")
    private File output;

    @Override public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override public Operation getOperation() {
        return new OperationProjectFetch(this);
    }

    public String getId() {
        return id;
    }

    public File getOutput() {
        return output;
    }
}
