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
    .directive('sidenavView', sidenavView);

  /** @ngInject */
  function sidenavView(filterTerm, filterQuery) {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/sidenav-view/sidenav-view.html',
      scope: {
        dataViz: '=',
        params: '='
      },
      controller: SidenavViewController,
      controllerAs: 'sidenavViewVm',
      bindToController: true
    };

    return directive;

    /** @ngInject */
    function SidenavViewController($state) {
      var sidenavViewVm = this;

      sidenavViewVm.openChart = function(chartType) {
        return $state.go('app.view.chart', {chartType: chartType});
      };

      sidenavViewVm.vizGroups = {
        'Words': [
          {id: 'word-cloud', name: 'Word Cloud', filters: [filterTerm]},
          {id: 'word-pie', name: 'Pie Chart', filters: [filterTerm, filterQuery]},
          {id: 'word-bar', name: 'Bar Chart', filters: [filterTerm, filterQuery]}
        ],
        'Sentiment': [
          {id: 'sentiment-pie', name: 'Pie Chart', filters: [filterTerm, filterQuery]},
          {id: 'sentiment-bar', name: 'Bar Chart', filters: [filterTerm, filterQuery]},
          {id: 'sentiment-timeline', name: 'Timeline', filters: [filterTerm, filterQuery]}
        ],
        'Others': [
          {id: 'message-timeline', name: 'Message Timeline'},
          {id: 'profile-graph', name: 'Profile Graph'}
        ]
      };
    }
  }

})();
