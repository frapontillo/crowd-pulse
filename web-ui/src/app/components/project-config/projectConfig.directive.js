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
      scope: {
        config: '='
      }
    };

    return directive;

    /** @ngInject */
    function ProjectConfigController($log, $mdToast, $mdDialog, $state, Project) {
      var vm = this;

      vm.editorLoaded = function(_editor) {
        _editor.$blockScrolling = Infinity;
        _editor.getSession().setTabSize(2);
      };

      vm.isNew = function() {
        // a project is new if it is not defined or if it doesn't have an ID
        return !(vm.config && vm.config.hasOwnProperty('_id'));
      };

      var showToast = function(message) {
        var toast = $mdToast.simple()
          .content(message)
          .position('bottom right');
        return $mdToast.show(toast);
      };

      vm.save = function() {
        vm.isSaving = true;
        try {
          var objConfig = JSON.parse(vm.config.config);
          vm.config.config = JSON.stringify(objConfig, null, '  ');
        } catch (Error) {
          return showToast('Error in the JSON configuration.');
        }
        return _save()
          .then(function(model) {
            vm.config = model;
            Project.cache.updateWithProject(model);
            showToast('Project saved.');
            $state.go('^.edit', {projectId: vm.config._id});
            return true;
          })
          .catch(function() {
            $log.error('Couldn\'t save project.');
            showToast('Error while saving project.');
            return false;
          })
          .finally(function() {
            vm.isSaving = false;
          });
      };

      var _save = function() {
        return vm.isNew() ? Project.post(vm.config) : vm.config.save();
      };

      vm.delete = function(event) {
        vm.isSaving = true;
        var confirm = $mdDialog.confirm()
          .title('Delete project ' + vm.config.name)
          .content('Do you really want to delete this project?')
          .ariaLabel('Delete project')
          .targetEvent(event)
          .ok('Yes, delete it')
          .cancel('Don\'t delete it');
        return $mdDialog.show(confirm)
          .then(function() {
            return _delete();
          })
          .then(function() {
            Project.cache.removeProject(vm.config);
            showToast('Project removed.');
            $state.go('^.main');
            return true;
          })
          .catch(function(err) {
            showToast(err.data.message);
          })
          .finally(function() {
            vm.isSaving = false;
          });
      };

      var _delete = function() {
        return vm.config.remove();
      };
    }
  }

})();
