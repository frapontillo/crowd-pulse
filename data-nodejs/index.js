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

var Q = require('q');
var mongoose = require('mongoose');

// make mongoose Promise/A+ compliant
require('mongoose-q')(mongoose);

var DataLayer = function() {

  var getCallbackAsPromiseFn = function(promise, resolveThis) {
    return function(err) {
      if (err) {
        promise.error(err);
      } else {
        promise.resolve(resolveThis);
      }
    };
  };

  var self = this;

  self.AccessTokenModel = require('./model/accessToken')(mongoose);
  self.AppModel = require('./model/app')(mongoose);
  self.JobModel = require('./model/job')(mongoose);
  self.ProjectModel = require('./model/project')(mongoose);
  self.RefreshTokenModel = require('./model/refreshToken')(mongoose);
  self.StepModel = require('./model/step')(mongoose);
  self.UserModel = require('./model/user')(mongoose);
  self.ObjectId = mongoose.Types.ObjectId;

  self.connect = function(uri, options) {
    var connectionPromise = Q.defer();
    mongoose.connect(uri, getCallbackAsPromiseFn(connectionPromise, self));
    return connectionPromise.promise;
  };

  self.disconnect = function() {
    var disconnectionPromise = Q.defer();

    mongoose.disconnect(getCallbackAsPromiseFn(disconnectionPromise, self));
    return disconnectionPromise.promise;
  };

  self.initDatabase = function() {
    var appInit = self.AppModel.findOneQ({ name: 'testApp' })
      .then(function (result) {
        return result || self.AppModel.createQ({
          name: 'testApp',
          secret: 'yolo123',
          allowedGrants: ['authorization_code', 'password', 'refresh_token', 'client_credentials']
        });
      });

    var userInit = self.UserModel.findOneQ({ username: 'admin' })
      .then(function (result) {
        return result || self.UserModel.createQ({
            username: 'admin',
            email: "francescopontillo@gmail.com",
            secret: 'yolo'
          });
      });

    return Q.all(appInit, userInit);
  };
};

module.exports = DataLayer;