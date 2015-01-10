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

package net.frakbot.crowdpulse.admin.cli.operation;

import net.frakbot.crowdpulse.admin.cli.command.CommandProjectList;
import net.frakbot.crowdpulse.data.entity.Project;
import net.frakbot.crowdpulse.data.repository.ProjectRepository;

import java.util.List;

/**
 * @author Francesco Pontillo
 */
public class OperationProjectList extends Operation<CommandProjectList> {
    public OperationProjectList(CommandProjectList command) {
        super(command);
    }

    @Override public void run() {
        ProjectRepository projectRepository = new ProjectRepository();
        List<Project> projects = projectRepository.find().asList();
        System.out.println("Found projects: ");
        if (projects.size() <= 0) {
            System.out.println("NONE.");
            return;
        }
        String projectFormat = " - %s | %s";
        for (Project p : projects) {
            System.out.println(String.format(projectFormat, p.getId().toString(), p.getName()));
        }
    }
}
