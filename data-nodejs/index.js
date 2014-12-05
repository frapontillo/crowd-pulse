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
var AppStore = require('./store/app')(mongoose);

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

  this.connect = function(host, database, port, options) {
    var connectionPromise = Q.defer();
    self.connection = mongoose.createConnection();

    self.connection.open(host, database, port, options, getCallbackAsPromiseFn(connectionPromise, self.connection));
    return connectionPromise.promise;
  };

  this.disconnect = function() {
    var disconnectionPromise = Q.defer();

    mongoose.disconnect(getCallbackAsPromiseFn(disconnectionPromise, self.connection));
    return disconnectionPromise.promise;
  };

  this.initDatabase = function() {
    AppStore.findOneQ({ name: 'test' })
      .then(function (result) {
        return result || AppStore.createQ({
          name: 'test',
          secret: 'yolo'
        });
      });
  };
};

var data = new DataLayer();

data.connect('mongodb://localhost/test')
  .then(function() {
    return data.initDatabase();
  })
  .then(function() {
    return data.disconnect();
  })
  .finally(function() {
    console.log('we\'re done');
  })
  .catch(function(err) {
    console.error(err);
  });

console.log('yolo');