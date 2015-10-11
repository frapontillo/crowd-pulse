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
  function sidenavView() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/sidenav-view/sidenav-view.html',
      scope: {
        params: '='
      },
      controller: SidenavViewController,
      controllerAs: 'sidenavViewVm',
      bindToController: true
    };

    return directive;

    /** @ngInject */
    function SidenavViewController($state, $scope, $mdMedia, $stateParams, Database, Term,
      filterTerm, filterQuery) {
      var sidenavViewVm = this;

      // data visualization grouped by concept of interest
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

      // available filters on the data
      sidenavViewVm.availableFilters = [
        {name: 'tags', type: 'tag'},
        {name: 'categories', type: 'category'},
        {name: 'tokens', type: 'token'}];

      // init the sidenav parameters with the state parameters
      sidenavViewVm.params.dataViz = $stateParams.chartType;
      sidenavViewVm.params.database = $stateParams.db;
      if ($stateParams.from) {
        sidenavViewVm.params.fromDate = new Date($stateParams.from);
      }
      if ($stateParams.to) {
        sidenavViewVm.params.toDate = new Date($stateParams.to);
      }
      sidenavViewVm.params.filterOn = $stateParams.filter;
      if (angular.isArray($stateParams.search)) {
        sidenavViewVm.params.query = $stateParams.search;
      } else if (angular.isDefined($stateParams.search)) {
        sidenavViewVm.params.query = [$stateParams.search];
      } else {
        sidenavViewVm.params.query = [];
      }

      // when the type of filter or the database changes, remove the query parameters
      $scope.$watchGroup(['sidenavViewVm.params.filterOn', 'sidenavViewVm.params.database'],
        function(newValues, oldValues) {
          // only reset when the previous values weren't undefined
          if ((angular.isDefined(oldValues[0]) && oldValues[0] !== newValues[0]) ||
              (angular.isDefined(oldValues[1]) && oldValues[1] !== newValues[1])) {
            sidenavViewVm.params.query = [];
          }
        });

      // when any of the parameters changes, set the new query string
      $scope.$watch('sidenavViewVm.params', function(newParams) {
        var newStateParams = {
          chartType: newParams.dataViz,
          db: newParams.database,
          filter: newParams.filterOn,
          search: newParams.query
        };
        newStateParams.from = newParams.fromDate ? newParams.fromDate.toISOString() : null;
        newStateParams.to = newParams.toDate ? newParams.toDate.toISOString() : null;
        return $state.go('app.view', newStateParams);
      }, true);

      // fetch databases, when done set the db in the querystring, if any
      Database.getList().then(function(dbs) {
        sidenavViewVm.databases = dbs;
        if (angular.isDefined($stateParams.db)) {
          sidenavViewVm.params.database = $stateParams.db;
        }
      });

      // search for filter-specific elements
      sidenavViewVm.queryForElement = function(query, type) {
        return Term.getList({
          db: sidenavViewVm.params.database,
          type: type,
          term: query
        });
      };
    }
  }

})();
