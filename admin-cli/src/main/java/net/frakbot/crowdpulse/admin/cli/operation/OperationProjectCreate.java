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

import com.google.gson.Gson;
import net.frakbot.crowdpulse.admin.cli.command.CommandProjectCreate;
import net.frakbot.crowdpulse.admin.cli.json.PulseGson;
import net.frakbot.crowdpulse.data.entity.Project;
import net.frakbot.crowdpulse.data.repository.ProjectRepository;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * @author Francesco Pontillo
 */
public class OperationProjectCreate extends Operation<CommandProjectCreate> {

    public OperationProjectCreate(CommandProjectCreate command) {
        super(command);
    }

    @Override public void run() {
        ProjectRepository projectRepository = new ProjectRepository();
        Gson gson = PulseGson.getGson();
        Project project = null;
        try {
            FileReader fileReader = new FileReader(command.getFile());
            project = gson.fromJson(fileReader, Project.class);
            fileReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        projectRepository.save(project);
        return;
    }
}
