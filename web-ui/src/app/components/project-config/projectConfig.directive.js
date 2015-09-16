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
    .directive('projectConfig', projectConfig);

  /** @ngInject */
  function projectConfig() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/project-config/project-config.html',
      controller: ProjectConfigController,
      controllerAs: 'projectConfigVm',
      bindToController: true,
      replace: true,
      // require: 'ngModel',
      scope: {
        config: '='
      }
    };

    return directive;

    /** @ngInject */
    function ProjectConfigController($log, Project) {
      var vm = this;

      vm.editorConfig = {
        useWrapMode: true,
        showGutter: true,
        theme: 'solarized_dark',
        mode: 'json',
        onLoad: function(_editor) {
          _editor.$blockScrolling = Infinity;
        }
      };

      var _save = function() {
        if (vm.config.hasOwnProperty('save')) {
          return vm.config.save();
        }
        return Project.post(vm.config);
      };

      vm.save = function() {
        return _save()
          .then(function(model) {
            $log.debug('Project saved.');
            return model;
          });
      };
    }
  }

})();
