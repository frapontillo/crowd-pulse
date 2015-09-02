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

var mongoose = require('mongoose');

var UserSchema = new mongoose.Schema({
  id: mongoose.Schema.ObjectId,
  username: String,
  email: String,
  secret: String
});

var SCHEMA_NAME = 'User';

UserSchema.statics.getSchemaName = function() {
  return SCHEMA_NAME;
};

UserSchema.statics.findOneIdByNameSecret = function (username, secret, callback) {
  return this.model(SCHEMA_NAME).findOneQ({ username: username })
    .then(function(user) {
      // TODO: implement hashing system
      if (user.secret === secret) {
        return callback(user._id);
      }
      callback();
    });
};

UserSchema.set('collection', SCHEMA_NAME);

module.exports = UserSchema;