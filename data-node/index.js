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

var Q = require('q');
var mongoose = require('mongoose');

var DataLayer = function() {
  var self = this;

  self.AccessToken = require('./model/accessToken');
  self.App = require('./model/app');
  self.Project = require('./model/project');
  self.RefreshToken = require('./model/refreshToken');
  self.User = require('./model/user');
  self.Message = require('./model/message');
  self.Profile = require('./model/profile');
  self.ObjectId = mongoose.Types.ObjectId;

  self.connect = Q.nbind(mongoose.connect, mongoose);
  self.disconnect = Q.nbind(mongoose.disconnect, mongoose);

  self.initDatabase = function() {
    var appInit = self.App.findOne({ name: 'testApp' })
      .then(function (result) {
        return result || self.App.createQ({
          name: 'testApp',
          secret: 'yolo123',
          allowedGrants: ['authorization_code', 'password', 'refresh_token', 'client_credentials']
        });
      });

    var userInit = self.User.findOne({ username: 'admin' })
      .then(function (result) {
        return result || self.User.createQ({
            username: 'admin',
            email: 'francescopontillo@gmail.com',
            secret: 'yolo'
          });
      });

    return Q.all(appInit, userInit);
  };
};

module.exports = DataLayer;