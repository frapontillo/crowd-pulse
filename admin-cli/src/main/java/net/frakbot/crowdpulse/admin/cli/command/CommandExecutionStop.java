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

/**
 * @author Francesco Pontillo
 */
@Parameters(commandNames = CommandExecutionStop.COMMAND_NAME)
public class CommandExecutionStop extends Command {
    public static final String COMMAND_NAME = "execution-stop";

    @Parameter(description = "The execution ID to stop")
    private String executionId;

    @Override public String getCommandName() {
        return COMMAND_NAME;
    }

    @Override public Operation getOperation() {
        return null;
    }
}
