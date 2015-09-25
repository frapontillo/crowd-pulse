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

var UserSchema = builder(schemas.user, {
  id: mongoose.Schema.ObjectId,
  username: String,
  email: String,
  secret: String
});

UserSchema.statics.findOneIdByNameSecret = function (username, secret, callback) {
  return this.model(schemas.user).findOne({ username: username }).exec()
    .then(function(user) {
      var foundUserId;
      // TODO: implement hashing system
      if (user.secret === secret) {
        foundUserId = user._id;
      }
      if (callback) {
        return callback(undefined, foundUserId);
      }
      return foundUserId;
    });
};

module.exports = UserSchema;