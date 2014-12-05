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

var mongoose = require('mongoose');
var AppSchema = require('./app');
var UserSchema = require('./user');

var UserSessionSchema = new mongoose.Schema({
  id: mongoose.Schema.ObjectId,
  userId: { type: Schema.Types.ObjectId, ref: UserSchema.statics.getSchemaName() },
  appId: { type: Schema.Types.ObjectId, ref: AppSchema.statics.getSchemaName() },
  token: String,
  issueDate: Date,
  lockedSession: Boolean
});

var SCHEMA_NAME = 'UserSession';

UserSessionSchema.statics.getSchemaName = function() {
  return SCHEMA_NAME;
};

UserSessionSchema.statics.findByToken = function (token) {
  return this.model(SCHEMA_NAME).find({ token: token }).exec();
};

module.exports = UserSessionSchema;