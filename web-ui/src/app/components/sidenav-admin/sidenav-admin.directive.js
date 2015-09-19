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

/**
 <md-sidenav md-component-id="right-sidenav" md-is-locked-open="$mdMedia('gt-md')"
 layout="column" flex
 ng-class="{ 'md-whiteframe-z2': sidenavAdminVm.evalMdMedia('gt-md'), 'md-whiteframe-z5': sidenavAdminVm.evalMdMedia('md') }">

 <md-content>
 <md-list>
 <md-list-item class="md-3-line" ng-repeat="project in sidenavAdminVm.projects">
 <div class="md-list-item-text">
 <h3>{{project.name}}</h3>
 </div>
 <md-divider ng-if="!$last"></md-divider>
 </md-list-item>
 </md-list>
 </md-content>

 </md-sidenav>
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

      sidenavAdminVm.openProject = function(projectId) {
        $state.go('app.admin.project.edit', {projectId: projectId});
        $mdSidenav('right-sidenav').close();
      };

      $scope.$on('$destroy', function() {
        Project.cache.removeOnCacheChangeListener(updateProjects);
      });
    }
  }

})();
