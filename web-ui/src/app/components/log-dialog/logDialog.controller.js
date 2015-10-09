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
    .controller('LogDialogCtrl', LogDialogCtrl);

  /** @ngInject */
  function LogDialogCtrl($scope, $stateParams, config, logsSocket, run, TerminalTrain) {
    $scope.terminal = TerminalTrain;

    $scope.getFullLogUrl = function() {
      return config.api + 'projects/' + $stateParams.projectId + '/runs/' + run._id + '/log';
    };

    // forward all events to this scope
    logsSocket.forward(['logs:clear','logs:cat', 'logs:tail'], $scope);

    logsSocket.emit('openLog', run._id);

    $scope.$on('socket:logs:tail', function (ev, data) {
      $scope.terminal.add(data);
    });
  }

})();
