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
var path = require('path');
var sanitize = require('sanitize-filename');
var mongoose = require('mongoose');
var builder = require('./schemaBuilder');
var schemas = require('./schemaName');

var ProjectSchema = builder(schemas.project, {
  id: mongoose.Schema.ObjectId,
  name: String,
  creationUser: {type: mongoose.Schema.ObjectId, ref: schemas.user},
  creationDate: Date,
  config: String,
  runs: [{type: mongoose.Schema.ObjectId, ref: schemas.projectRun}]
});

// Model methods

ProjectSchema.statics.newFromObject = function(object) {
  var project = new this(object);
  project.updateNameFromConfig();
  project.creationDate = new Date();
  return project;
};

ProjectSchema.statics.fromPreObject = function(object) {
  var project = new this(object);
  project.updateNameFromConfig();
  return project;
};

ProjectSchema.statics.getAll = function() {
  return Q(this.find().populate('runs').exec());
};

ProjectSchema.statics.getById = function(id) {
  return Q(this.findById(id).populate('runs').exec());
};

ProjectSchema.statics.safeDelete = function(id) {
  var model = this;
  return model.getById(id)
    .then(function(project) {
      if (project.hasActiveRuns()) {
        throw new Error('The project has pending jobs and can\'t be deleted.');
      }
      return Q(model.findByIdAndRemove(id).exec());
    });
};

// Instance methods

ProjectSchema.methods.updateNameFromConfig = function() {
  var config = JSON.parse(this.config);
  this.name = config.process.name;
  return this;
};

ProjectSchema.methods.updateElement = function() {
  return Q(this.model(schemas.project)
    .findByIdAndUpdate(this._id, {$set: this}, {new: true})
    .populate('runs').exec());
};

ProjectSchema.methods.hasActiveRuns = function() {
  return (this.runs || []).some(function(run) {
    return (typeof run.dateEnd === 'undefined');
  });
};

ProjectSchema.methods.createNewRun = function(logsPath) {
  var project = this;
  var dateStart = new Date();
  var log = path.join(logsPath,
    sanitize(project.name + '-' + dateStart.toISOString().replace(':', '')) + ".log");
  var run = project.model(schemas.projectRun)({
    dateStart: new Date(),
    log: log
  });
  return Q(run.save())
    .then(function(newRun) {
      project.runs.push(newRun);
      return [Q(project.save()), newRun];
    })
    .spread(function(project, run) {
      return [project, run];
    });
};

module.exports = ProjectSchema;
