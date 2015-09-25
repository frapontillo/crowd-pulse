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
var router = express.Router();

module.exports = function(crowdPulse) {

  var autoFill = function(project) {
    var config = JSON.parse(project.config);
    project.name = config.process.name;
    project.creationDate = (new Date()).toISOString();
  };

  var hasActiveRuns = function(project) {
    return (project.runs || []).some(function(run) {
        return (typeof run.date_end === 'undefined');
      });
  };

  var checkForActiveRuns = function(project) {
    var q = Q.defer();
    if (hasActiveRuns(project)) {
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
      crowdPulse.Project.find()
        .populate('runs')
        .exec()
        .then(function(projects) {
          res.send(projects);
        });
    })
    .post(function(req, res) {
      var project = new crowdPulse.Project(req.body);
      autoFill(project);
      // TODO: add project.creationUser
      project.save()
        .then(function(saved) {
          res.send(saved);
        });
    });

  router.route('/projects/:projectId')
    .get(function(req, res) {
      crowdPulse.Project.findById(req.params.projectId)
        .populate('runs')
        .exec()
        .then(function(project) {
          res.send(project);
        });
    })
    .put(function(req, res) {
      var project = req.body;
      autoFill(project);
      crowdPulse.Project
        .findByIdAndUpdate(crowdPulse.ObjectId(req.params.projectId), {$set: project})
        .populate('runs')
        .exec()
        .then(function() {
          res.send(project);
        });
    })
    .delete(_delete);

  // POST to start a run
  router.route('/projects/:projectId/runs')
    .post(function(req, res) {
      // retrieve the project
      Q(crowdPulse.Project.findById(req.params.projectId).exec())
        .then(function(project) {
          var newRun = new crowdPulse.ProjectRun({
            date_start: new Date(),
            project: req.params.projectId
          });
          return [project, newRun.save()];
        })
        .spread(function(project, run) {
          project.runs.push(run);
          return [project.save(), run];
        })
        .spread(function(project, run) {
          // TODO: start the run here
          res.send(run);
        });
    });

  // DELETE to stop a run
  router.route('/projects/:projectId/runs/:runId')
    .delete(function(req, res) {
      // TODO: stop the run here
      res.send('stopped');
    });

  return router;
};