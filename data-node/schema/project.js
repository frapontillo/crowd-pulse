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

var mongoose = require('mongoose');
var builder = require('./schemaBuilder');
var schemas = require('./schemaName');

var ProjectSchema = builder(schemas.project, {
  id: mongoose.Schema.ObjectId,
  name: String,
  creationUser: { type: mongoose.Schema.ObjectId, ref: schemas.user },
  creationDate: Date,
  config: String,
  runs: [{ type: mongoose.Schema.ObjectId, ref: schemas.projectRun }]
});

module.exports = ProjectSchema;
