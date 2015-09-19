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
    .directive('mdChooseFile', mdChooseFile);

  /** @ngInject */
  function mdChooseFile($log, FileReader) {
    var directive = {
      restrict: 'A',
      require: 'ngModel',
      priority: 1000,
      link: MdChooseFilePostLink
    };

    function MdChooseFilePostLink(scope, elem, attr, ngModelCtrl) {
      // add the file input
      elem.append('<input type="file"/>');

      var button = elem;
      var input = elem.find('input');
      input.css({
        'width': '0.1px',
        'height': '0.1px',
        'opacity': '0',
        'overflow': 'hidden',
        'position': 'absolute',
        'z-index': '-1'
      });

      var handleClick = function() {
        input[0].click();
        return false;
      };

      var handleFile = function(evt) {
        var file = evt.target.files[0];

        if (!file) {
          return;
        }

        if (file.type !== 'application/json') {
          $log.warn('Unhandled file type ' + file.type);
          return;
        }

        var reader = new FileReader();
        reader.onload = function() {
          ngModelCtrl.$setViewValue(reader.result);
          ngModelCtrl.$modelValue = reader.result;
        };
        reader.readAsText(file);
      };

      // when the button is clicked
      button.bind('click', handleClick);
      input.bind('change', handleFile);

      scope.$on('$destroy', function() {
        button.unbind('click', handleClick);
      });
    }

    return directive;
  }

})();
