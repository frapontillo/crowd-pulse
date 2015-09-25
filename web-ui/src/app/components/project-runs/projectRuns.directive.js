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
    .directive('projectRuns', projectRuns);

  /** @ngInject */
  function projectRuns() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/project-runs/project-runs.html',
      controller: ProjectRunsController,
      controllerAs: 'projectRunsVm',
      bindToController: true,
      replace: true,
      scope: {
        config: '='
      }
    };

    return directive;

    /** @ngInject */
    function ProjectRunsController($scope, $mdToast, $mdDialog, $filter, Project) {
      var projectRunsVm = this;

      var showToast = function(message) {
        var toast = $mdToast.simple()
          .content(message)
          .position('bottom right');
        return $mdToast.show(toast);
      };

      $scope.$watchCollection('projectRunsVm.config.runs', function(newValue) {
        var runs = newValue || [];
        projectRunsVm.stopped = runs.filter(function(run) {
          return angular.isDefined(run.date_end);
        });

        projectRunsVm.running = runs.filter(function(run) {
          return angular.isUndefined(run.date_end);
        });
      }, true);

      projectRunsVm.showRunning = function() {
        return projectRunsVm.running.length > 0;
      };

      projectRunsVm.showStopped = function() {
        return projectRunsVm.stopped.length > 0;
      };

      projectRunsVm.showDivider = function() {
        return (projectRunsVm.showRunning() && projectRunsVm.showStopped());
      };

      projectRunsVm.stop = function(run, event) {
        var formattedDate = $filter('date')(run.date_start, 'short');
        var confirm = $mdDialog.confirm()
          .title('Stop run')
          .content('Do you really want to stop the run started at' + formattedDate + '?')
          .ariaLabel('Stop run')
          .targetEvent(event)
          .ok('Yes, stop it')
          .cancel('Don\'t stop it');
        return $mdDialog.show(confirm)
          .then(function() {
            return _stop(run);
          })
          .then(function() {
            showToast('Run stopped.');
          });
      };

      var _stop = function(run) {
        return projectRunsVm.config.one('runs', run._id).remove()
          .then(function() {
            return projectRunsVm.config.customGET();
          })
          .then(function(updatedProject) {
            projectRunsVm.config = updatedProject;
            Project.cache.updateWithProject(updatedProject);
          })
      };
    }
  }

})();
