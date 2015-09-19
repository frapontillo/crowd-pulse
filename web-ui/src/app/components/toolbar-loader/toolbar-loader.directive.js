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
    .directive('toolbarLoader', toolbarLoader);

  /** @ngInject */
  function toolbarLoader(toolbarLoadedEvent, toolbarLoadingEvent) {
    var directive = {
      restrict: 'A',
      link: ToolbarLoaderPostLink
    };

    return directive;

    function ToolbarLoaderPostLink(scope, elem, attrs) {
      scope.$on(toolbarLoadedEvent, function() {
        attrs.$set('mdMode', '');
      });
      scope.$on(toolbarLoadingEvent, function() {
        attrs.$set('mdMode', 'indeterminate');
      });
    }
  }

})();
