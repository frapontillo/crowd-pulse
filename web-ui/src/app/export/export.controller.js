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

  angular.module('webUi')
    .controller('ExportController', ExportController);

  /** @ngInject */
  function ExportController($scope, $window, $mdToast, Database, Profile, Language, config) {
    var vm = this;

    vm.params = {};

    Database.getList().then(function(dbs) {
      vm.databases = dbs;
    });

    vm.searchAuthors = function(author, database) {
      return Profile.getList({
        db: database,
        username: author
      });
    };

    $scope.$watch('vm.params.database', function(newValue) {
      Language.getList({
        db: newValue
      }).then(function(languages) {
        vm.languages = [''].concat(languages);
      });
    });

    vm.download = function() {
      if (!vm.params.database) {
        return $mdToast.show(
          $mdToast.simple()
            .content('Please select a database first')
            .position('bottom right')
            .hideDelay(3000)
        );
      }
      var url = config.api + 'databases/' + vm.params.database + '?';
      if (vm.params.language) {
        url += 'language=' + vm.params.language + '&';
      }
      if (vm.params.author) {
        url += 'author=' + vm.params.author + '&';
      }
      url = url.substr(0, url.length-1);
      $window.open(url);
    };
  }

})();
