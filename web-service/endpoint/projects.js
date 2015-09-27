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

'use strict';

var Q = require('q');
var express = require('express');
var StatusHelper = require('./../statusHelper');
var cpLauncher = require('../lib/cpLauncher');
var qSend = require('../lib/expressQ').send;
var qErr = require('../lib/expressQ').error;
var router = express.Router();

module.exports = function(crowdPulse) {

  var checkForActiveRuns = function(project) {
    var q = Q.defer();
    if (project.hasActiveRuns()) {
      q.reject(new Error('The project has pending jobs and can\'t be deleted.\n' +
                         'Please stop all of its jobs, then retry.'));
    } else {
      q.resolve(project);
    }
    return q.promise;
  };

  var _delete = function(req, res) {
    crowdPulse.Project.findById(req.params.projectId).exec()
      .then(null, function(err) {
        StatusHelper.notFound(res, req.params.projectId, err);
        throw err;
      })
      .then(function(project) {
        return checkForActiveRuns(project)
          .catch(function(err) {
            StatusHelper.forbidden(res, req.params.projectId, err);
            throw err;
          });
      })
      .then(function() {
        return crowdPulse.Project.findByIdAndRemove(req.params.projectId).exec();
      })
      .then(function() {
        res.status(204);
        res.send();
      });
  };

  router.route('/projects')
    .get(function(req, res) {
      return crowdPulse.Project.getAll().then(qSend(res)).catch(qErr(res));
    })
    .post(function(req, res) {
      var project = crowdPulse.Project.newFromObject(req.body);
      return Q(project.save()).then(qSend(res)).catch(qErr(res));
    });

  router.route('/projects/:projectId')
    .get(function(req, res) {
      return crowdPulse.Project.getById(req.params.projectId).then(qSend(res)).catch(qErr(res));
    })
    .put(function(req, res) {
      var project = crowdPulse.Project.fromPreObject(req.body);
      return project.updateElement().then(qSend(res)).catch(qErr(res));
    })
    .delete(function(req, res) {
      return crowdPulse.Project.safeDelete(req.params.projectId).then(qSend(res)).catch(qErr(res));
    });

  // POST to start a run
  router.route('/projects/:projectId/runs')
    .post(function(req, res) {
      var newRun;
      return crowdPulse.Project.getById(req.params.projectId)
        .then(function(project) {
          return project.createNewRun();
        })
        .spread(function(project, run) {
          newRun = run;
          return [project, run];
        })
        // launch the run
        .spread(cpLauncher.executeProjectRun)
        // send the run information
        .then(qSend(res))
        // in case there's an error
        .catch(function(error) {
          // if the new run was saved, set it to errored and send it to the client
          if (newRun) {
            newRun.status = 1;
            newRun.dateEnd = new Date();
            return Q(newRun.save())
              .then(qSend(res))
              .catch(qErr(res));
          }
          // if the run wasn't saved, it means the error comes from previous steps, send it
          return qErr(res)(error);
        });
    });

  router.route('/projects/:projectId/runs/:runId')
    .get(function(req, res) {
      return crowdPulse.ProjectRun.getById(req.params.runId).then(qSend(res)).catch(qErr(res));
    })
    // DELETE to stop a run
    .delete(function(req, res) {
      // TODO: stop the run here
      res.send('stopped');
    });

  return router;
};