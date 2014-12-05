/*
 * Copyright 2014 Francesco Pontillo
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

/*
private long id;
private String name;
private Step[] steps;
private User creationUser;
private Date creationDate;*/

var mongoose = require('mongoose');
var UserSchema = require('./user');
var StepSchema = require('./step');

var ProjectSchema = new mongoose.Schema({
  id: mongoose.Schema.ObjectId,
  name: String,
  steps: [{
    type: Schema.Types.ObjectId, ref: StepSchema.statics.getSchemaName()
  }],
  creationUser: { type: Schema.Types.ObjectId, ref: UserSchema.statics.getSchemaName() },
  creationDate: Date
});

var SCHEMA_NAME = 'Project';

ProjectSchema.statics.getSchemaName = function() {
  return SCHEMA_NAME;
};

module.exports = ProjectSchema;
