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

(function() {
  'use strict';

  angular
    .module('webUi')
    .provider('config', configProvider);

  /** @njInject */
  function configProvider() {
    var configPath;
    var listeners = [];

    this.useConfigPath = function(value) {
      configPath = value;
    };

    this.addConfigResolvedListener = function(fn) {
      listeners.push(fn);
    };

    this.$get = function configFactory($http, $log, $q) {
      var config = $q.defer();

      // retrieve the object
      var configObj = $http.get(configPath);

      configObj.then(function(data) {
        var resolved = data.data;
        config.resolve(resolved);
        angular.extend(config, resolved);
        // run every attached listener
        listeners.forEach(function(listener) {
          listener(resolved);
        });
        $log.debug('Configuration downloaded and stored.');
      });

      // return the promise
      return config;
    };

    return this;
  }

})();
