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

var getAddAppMaybeFn = function(crowdPulse) {
  return function(app) {
    console.log('Bootstrapping application', app.name + '...');
    return crowdPulse.App.findOne({name: app.name}).exec()
      .then(function(existingApp) {
        console.log('Application', app.name,
          (existingApp ? 'already existed.' : 'doesn\'t exist, creating it now....'));
        return existingApp || crowdPulse.App.create(app);
      })
      .then(function(app) {
        console.log('Bootstrapped application', app.name + '.');
      });
  };
};

var getAddUserMaybeFn = function(crowdPulse) {
  return function(user) {
    return crowdPulse.User.findOne({username: user.username}).exec()
      .then(function(existingUser) {
        console.log('User', user.username,
          (existingUser ? 'already existed.' : 'doesn\'t exist, creating it now....'));
        return existingUser || crowdPulse.User.create(user);
      })
      .then(function(user) {
        console.log('Bootstrapped user', user.username + '.');
      });
  }
};

var bootstrapMaybe = function(crowdPulse, config) {
  return function() {
    var appPromises = (config.apps || []).map(getAddAppMaybeFn(crowdPulse));
    var userPromises = (config.users || []).map(getAddUserMaybeFn(crowdPulse));
    return Q.all(appPromises.concat(userPromises))
      .then(function() {
        console.log('Bootstrap completed.');
        return true;
      });
  }
};

module.exports = bootstrapMaybe;