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
var mongoose = require('mongoose');

var DataLayer = function() {
  var self = this;

  self.connect = function(host, database, port, options, callback) {
    var deferred = Q.defer();

    self.connection = mongoose.createConnection();
    self.connection.open.apply(self.connection, arguments);

    self.connection.on('error', function(err) {
      deferred.reject(err);
    });

    self.connection.once('open', function() {
      // create models bound to the current connection
      self.AccessToken = require('./model/accessToken')(self.connection);
      self.App = require('./model/app')(self.connection);
      self.Project = require('./model/project')(self.connection);
      self.ProjectRun = require('./model/projectRun')(self.connection);
      self.RefreshToken = require('./model/refreshToken')(self.connection);
      self.User = require('./model/user')(self.connection);
      self.Message = require('./model/message')(self.connection);
      self.Profile = require('./model/profile')(self.connection);
      self.ObjectId = mongoose.Types.ObjectId;

      // return the whole object
      deferred.resolve(self);
    });

    return deferred.promise;
  };

  self.disconnect = function() {
    return Q.ninvoke(self.connection, 'close');
  };

  self.getDatabases = function() {
    var admin = self.connection.db.admin();
    return Q.ninvoke(admin, 'listDatabases')
      .then(function(result) {
        return result.databases;
      });
  };

  self.initDatabase = function() {
    var appInit = self.App.findOne({name: 'testApp'})
      .then(function(result) {
        return result || self.App.createQ({
            name: 'testApp',
            secret: 'yolo123',
            allowedGrants: ['authorization_code', 'password', 'refresh_token', 'client_credentials']
          });
      });

    var userInit = self.User.findOne({username: 'admin'})
      .then(function(result) {
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