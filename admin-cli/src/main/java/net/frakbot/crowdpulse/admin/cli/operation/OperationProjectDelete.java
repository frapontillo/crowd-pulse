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

import com.mongodb.WriteResult;
import net.frakbot.crowdpulse.admin.cli.command.CommandProjectDelete;
import net.frakbot.crowdpulse.data.repository.ProjectRepository;
import org.bson.types.ObjectId;

/**
 * @author Francesco Pontillo
 */
public class OperationProjectDelete extends Operation<CommandProjectDelete> {
    public OperationProjectDelete(CommandProjectDelete command) {
        super(command);
    }

    @Override public void run() {
        // attempt a deletion
        ProjectRepository projectRepository = new ProjectRepository();
        WriteResult result = projectRepository.deleteById(new ObjectId(command.getId()));

        if (result.getN() > 0) {
            System.out.println(String.format("Project with ID %s deleted.", command.getId()));
        } else {
            System.out.println(String.format(NO_PROJECT_WITH_ID, command.getId()));
        }
    }
}
