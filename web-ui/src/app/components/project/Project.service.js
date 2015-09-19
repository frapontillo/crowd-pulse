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
    .factory('Project', Project);

  /** @ngInject */
  function Project(Restangular) {
    var name = 'projects';
    var ProjectService = Restangular.service(name);

    ProjectService.cache = {};
    ProjectService.cache.projects = undefined;
    ProjectService.cache.listeners = [];

    ProjectService.cache.addOnCacheChangeListener = function(fn) {
      ProjectService.cache.listeners.push(fn)
    };

    ProjectService.cache.removeOnCacheChangeListener = function(fn) {
      var index = ProjectService.cache.listeners.indexOf(fn);
      if (index >= 0) {
        ProjectService.cache.listeners.splice(index, 1);
      }
    };

    var noticeListeners = function(what) {
      ProjectService.cache.listeners.forEach(function(fn) {
        fn(what);
      });
    };

    ProjectService.cache.updateWithProject = function(project) {
      if (ProjectService.cache.isLoaded()) {
        for (var p in ProjectService.cache.projects) {
          if (ProjectService.cache.projects[p]._id === project._id) {
            ProjectService.cache.projects[p] = project;
            noticeListeners(project);
            return;
          }
        }
      } else {
        ProjectService.cache.projects = [];
      }

      ProjectService.cache.projects.push(project);
      noticeListeners(project);
    };

    ProjectService.cache.isLoaded = function() {
      return (typeof ProjectService.cache.projects !== 'undefined');
    };

    ProjectService.cache.invalidate = function() {
      return ProjectService.getList().then(function(projects) {
        ProjectService.cache.projects = projects;
        noticeListeners(projects);
      });
    };

    ProjectService.cache.getOrLoad = function() {
      if (ProjectService.cache.isLoaded()) {
        return ProjectService.cache.projects;
      }
      return ProjectService.cache.invalidate();
    };

    return ProjectService;
  }

})();
