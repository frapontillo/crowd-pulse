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

import net.frakbot.crowdpulse.admin.cli.operation.Operation;

import java.util.HashMap;

/**
 * @author Francesco Pontillo
 */
public abstract class Command {
    private static HashMap<String, Command> commandMap = new HashMap<String, Command>();

    public static void addCommand(String commandName, Command command) {
        commandMap.put(commandName, command);
    }

    public static Command getCommandFor(String commandName) {
        return commandMap.get(commandName);
    }

    public abstract String getCommandName();

    public abstract Operation getOperation();

    public static Command getCommandByName(String commandName, Command... commands) {
        for (Command command : commands) {
            if (command.getCommandName().equals(commandName)) {
                return command;
            }
        }
        return null;
    }
}
