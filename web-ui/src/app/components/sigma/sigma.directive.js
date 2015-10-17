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
    .directive('sigma', sigmaDirective);

  /** @ngInject */
  function sigmaDirective($timeout, Sigma) {
    var directive = {
      restrict: 'E',
      scope: {
        graph: '='
      },
      link: linkFn
    };

    return directive;

    function linkFn(scope, elem) {
      // Let's first initialize sigma:
      var sigmaGraph = new Sigma({
        container: elem[0],
        settings: {
          defaultNodeColor: '#1A237E',
          defaultEdgeColor: '#5C6BC0',
          edgeColor: 'default',
          labelThreshold: 8
        }
      });

      scope.$watch('graph', function() {
        sigmaGraph.graph.clear();
        if (!scope.graph) {
          return;
        }
        if (scope.graph && scope.graph.hasOwnProperty('nodes') && scope.graph.hasOwnProperty('edges')) {
          sigmaGraph.graph.read(scope.graph);
        }
        sigmaGraph.killForceAtlas2();
        sigmaGraph.refresh();
        if (scope.graph.nodes.length > 0) {
          sigmaGraph.startForceAtlas2({worker: true, barnesHutOptimize: true});
          $timeout(function() {
            sigmaGraph.stopForceAtlas2();
          }, 3000);
        }
      });

      scope.$on('$destroy', function() {
        sigmaGraph.killForceAtlas2();
        sigmaGraph.graph.clear();
      });
    }
  }

})();
