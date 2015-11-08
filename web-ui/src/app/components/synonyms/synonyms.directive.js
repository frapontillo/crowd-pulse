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
    .directive('synonyms', synonyms);

  /** @ngInject */
  function synonyms() {
    return {
      restrict: 'E',
      templateUrl: 'app/components/synonyms/synonyms.html',
      controller: SynonymsController,
      controllerAs: 'synonymsVm',
      bindToController: true,
      replace: true,
      scope: {
        synonymGroups: '='
      }
    };
  }

  /** @ngInject */
  function SynonymsController() {
    this.hasElements = function(group) {
      return angular.isArray(group.details);
    };
  }

})();
