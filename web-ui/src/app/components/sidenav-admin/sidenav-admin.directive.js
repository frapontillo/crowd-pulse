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
    .directive('sidenavAdmin', sidenavAdmin);

  /** @ngInject */
  function sidenavAdmin() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/sidenav-admin/sidenav-admin.html',
      scope: {
        dataViz: '=',
        params: '='
      },
      controller: SidenavAdminController,
      controllerAs: 'sidenavAdminVm',
      bindToController: true
    };

    return directive;

    /** @ngInject */
    function SidenavAdminController($scope, $state, $mdSidenav, Project) {
      var sidenavAdminVm = this;

      var updateProjects = function() {
        sidenavAdminVm.projects = Project.cache.projects;
      };

      Project.cache.addOnCacheChangeListener(updateProjects);

      Project.cache.getOrLoad();

      updateProjects();

      sidenavAdminVm.openProject = function(projectId) {
        $state.go('app.admin.project.edit', {projectId: projectId});
        $mdSidenav('main-sidenav').close();
      };

      sidenavAdminVm.goToNew = function() {
        $state.go('app.admin.project.new');
        $mdSidenav('main-sidenav').close();
      };

      $scope.$on('$destroy', function() {
        Project.cache.removeOnCacheChangeListener(updateProjects);
      });
    }
  }

})();
