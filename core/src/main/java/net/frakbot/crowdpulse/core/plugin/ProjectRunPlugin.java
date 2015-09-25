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

package net.frakbot.crowdpulse.core.plugin;

import net.frakbot.crowdpulse.common.util.CrowdLogger;
import net.frakbot.crowdpulse.common.util.spi.IPlugin;
import net.frakbot.crowdpulse.data.entity.Project;
import net.frakbot.crowdpulse.data.entity.ProjectRun;
import net.frakbot.crowdpulse.data.repository.ProjectRunRepository;
import org.apache.logging.log4j.Logger;
import org.bson.types.ObjectId;
import rx.Observable;
import rx.Subscriber;
import rx.observers.SafeSubscriber;

/**
 * @author Francesco Pontillo
 */
public abstract class ProjectRunPlugin extends IPlugin<Void, Void, ProjectRunOptions> {
    private ProjectRunRepository projectRunRepository;
    private final Logger logger = CrowdLogger.getLogger(ProjectRunPlugin.class);

    @Override
    public ProjectRunOptions getNewParameter() {
        return new ProjectRunOptions();
    }

    @Override
    protected Observable.Operator<Void, Void> getOperator(ProjectRunOptions params) {
        return subscriber -> new SafeSubscriber<>(new Subscriber<Object>() {
            @Override
            public void onCompleted() {
                reportCompletion(true);
                subscriber.onCompleted();
            }

            @Override
            public void onError(Throwable e) {
                reportCompletion(false);
                e.printStackTrace();
                subscriber.onError(e);
            }

            @Override
            public void onNext(Object o) {
                // do nothing
            }

            private void reportCompletion(boolean success) {
                projectRunRepository = new ProjectRunRepository(params.getDb());
                ProjectRun run = projectRunRepository.get(new ObjectId(params.getProjectRunId()));
                if (run == null) {
                    logger.warn("No project run was found for ID {}, won't set anything.", params.getProjectRunId());
                    return;
                }
                handleWake(run, params, success);
                projectRunRepository.save(run);
            }
        });
    }

    protected abstract void handleWake(ProjectRun projectRun, ProjectRunOptions parameters, boolean success);
}
