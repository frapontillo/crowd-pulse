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

package net.frakbot.crowdpulse.admin.cli;

import com.beust.jcommander.JCommander;
import net.frakbot.crowdpulse.admin.cli.command.*;

/**
 * @author Francesco Pontillo
 */
public class Main {

    public static void main(String[] args) {
        new Main().run(args);
    }

    public void run(String[] args) {
        System.out.println("Extraction started.");

        JCommander jCommander = new JCommander();

        // all the sub-commands
        CommandProjectList projectList = new CommandProjectList();
        CommandProjectCreate projectCreate = new CommandProjectCreate();
        CommandProjectEdit projectEdit = new CommandProjectEdit();
        CommandProjectDelete projectDelete = new CommandProjectDelete();
        CommandProjectStart projectStart = new CommandProjectStart();
        CommandExecutionList executionList = new CommandExecutionList();
        CommandExecutionStop executionStop = new CommandExecutionStop();

        jCommander.addCommand(projectList);
        jCommander.addCommand(projectCreate);
        jCommander.addCommand(projectEdit);
        jCommander.addCommand(projectDelete);
        jCommander.addCommand(projectStart);
        jCommander.addCommand(executionList);
        jCommander.addCommand(executionStop);

        jCommander.parse(args);

        Command bestFit = Command.getCommandByName(jCommander.getParsedCommand(), projectList, projectCreate,
                projectEdit, projectDelete, projectStart, executionList, executionStop);
        bestFit.getOperation().run();

        System.out.println("Done.");
    }
}
