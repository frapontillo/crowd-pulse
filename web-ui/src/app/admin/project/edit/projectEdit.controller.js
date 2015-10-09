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
    .controller('AdminProjectEditController', AdminProjectEditController);

  /** @ngInject */
  function AdminProjectEditController($stateParams, $mdDialog, $mdToast, Project) {
    var vm = this;

    Project.one($stateParams.projectId).get()
      .then(function(model) {
        vm.project = model;
      });

    vm.start = function(event) {
      var confirm = $mdDialog.confirm()
        .title('Start new run')
        .content('Do you really want to start a new run for the project ' + vm.project.name + '?')
        .ariaLabel('Start new run')
        .targetEvent(event)
        .ok('Yes, start it')
        .cancel('Don\'t start it');
      return $mdDialog.show(confirm)
        .then(function() {
          return _start();
        })
        .then(function() {
          showToast('Run started.');
        });
    };

    var _start = function() {
      // create a new run, then refresh the project
      return vm.project.customPOST({}, 'runs')
        .then(function() {
          return vm.project.customGET();
        })
        .then(function(updatedProject) {
          vm.project = updatedProject;
          Project.cache.updateWithProject(updatedProject);
        });
    };

    var showToast = function(message) {
      var toast = $mdToast.simple()
        .content(message)
        .position('bottom right');
      return $mdToast.show(toast);
    };

  }
})();
