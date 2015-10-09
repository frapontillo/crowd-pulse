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
    .directive('sidenav', sidenav);

  /** @ngInject */
  function sidenav() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/sidenav/sidenav.html',
      transclude: true,
      controller: SidenavController,
      controllerAs: 'sidenavVm',
      bindToController: true,
      replace: true
    };

    return directive;

    /** @ngInject */
    function SidenavController($mdMedia) {
      var sidenavVm = this;

      sidenavVm.evalMdMedia = function(sizing) {
        return $mdMedia(sizing);
      };
    }
  }

})();
