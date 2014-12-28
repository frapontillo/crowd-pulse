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

var OAuthAccessToken = new mongoose.Schema({
  id: mongoose.Schema.ObjectId,
  accessToken: { type: String },
  appId: { type: mongoose.Schema.ObjectId, ref: AppSchema.statics.getSchemaName() },
  userId: { type: mongoose.Schema.ObjectId, ref: UserSchema.statics.getSchemaName() },
  expires: { type: Date }
});

var SCHEMA_NAME = 'OAuthAccessToken';

OAuthAccessToken.statics.getSchemaName = function() {
  return SCHEMA_NAME;
};

OAuthAccessToken.statics.findOneByToken = function (token, callback) {
  return this.model(SCHEMA_NAME).findOne({ accessToken: token }).exec(callback);
};

module.exports = OAuthAccessToken;