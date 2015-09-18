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

module.exports = function(crowdPulse) {
  var express = require('express');
  var router = express.Router();

  var autoFill = function(project) {
    var config = JSON.parse(project.config);
    project.name = config.process.name;
    project.creationDate = (new Date()).toISOString();
  };

  router.route('/projects')
    .get(function(req, res) {
      crowdPulse.Project.find().exec()
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
      crowdPulse.Project.findById(req.params.projectId).exec()
        .then(function(project) {
          res.send(project);
        });
    })
    .put(function(req, res) {
      var project = req.body;
      autoFill(project);
      crowdPulse.Project.findByIdAndUpdate(
        crowdPulse.ObjectId(req.params.projectId),
        {$set: project}).exec()
        .then(function() {
          res.send(project);
        });
    });

  return router;
};