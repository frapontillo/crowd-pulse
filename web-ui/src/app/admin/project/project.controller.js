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
    .controller('AdminProjectController', AdminProjectController);

  /** @ngInject */
  function AdminProjectController($log, $stateParams) {
    var vm = this;

    vm.projectId = $stateParams.projectId;

    vm.editorConfig = {
      useWrapMode: true,
      showGutter: true,
      theme: 'solarized_dark',
      mode: 'json'
    };

    vm.runs = [{
      id: 'run4',
      date_start: '2015-09-13T08:08:08+02:00'
    },{
      id: 'run3',
      date_start: '2015-09-11T10:20:08+02:00',
      date_end: '2015-09-11T10:32:08+02:00'
    },{
      id: 'run2',
      date_start: '2015-09-10T15:18:08+02:00',
      date_end: '2015-09-10T16:01:08+02:00'
    },{
      id: 'run1',
      date_start: '2015-09-09T23:43:08+02:00',
      date_end: '2015-09-09T23:59:08+02:00'
    }];

    vm.showLog = function(run) {

    };

    vm.stop = function(run) {
      $log.log('Run stopped.');
    };


  }
})();
