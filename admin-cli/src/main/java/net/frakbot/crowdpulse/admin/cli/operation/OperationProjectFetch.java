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

import net.frakbot.crowdpulse.admin.cli.command.CommandProjectFetch;
import net.frakbot.crowdpulse.admin.cli.json.PulseGson;
import net.frakbot.crowdpulse.data.entity.Project;
import net.frakbot.crowdpulse.data.repository.ProjectRepository;
import org.bson.types.ObjectId;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author Francesco Pontillo
 */
public class OperationProjectFetch extends Operation<CommandProjectFetch> {
    public OperationProjectFetch(CommandProjectFetch command) {
        super(command);
    }

    @Override public void run() {
        // get the project
        ProjectRepository projectRepository = new ProjectRepository();
        Project project = projectRepository.get(new ObjectId(command.getId()));
        if (project == null) {
            System.out.println(String.format(NO_PROJECT_WITH_ID, command.getId()));
            return;
        }
        // serialize and save the project to disk
        String jsonProject = PulseGson.getGson().toJson(project);
        try {
            FileOutputStream outputStream = new FileOutputStream(command.getOutput());
            outputStream.write(jsonProject.getBytes());
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Project fetched.");
    }
}
