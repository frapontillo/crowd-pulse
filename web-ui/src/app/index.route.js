(function() {
  'use strict';

  angular
    .module('webUi')
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider, $urlRouterProvider) {
    $stateProvider
      .state('app', {
        url: '',
        abstract: true,
        templateUrl: 'app/__abstract.html',
        resolve: {
          'config': 'config'
        }
      })

      .state('app.view', {
        url: '/view',
        abstract: true,
        templateUrl: 'app/view/index.html',
        controller: 'ViewIndexController',
        controllerAs: 'vm'
      })
      .state('app.view.main', {
        url: '',
        templateUrl: 'app/view/main/main.html'
      })
      .state('app.view.chart', {
        url: '/:chartType',
        templateUrl: 'app/view/chart/chart.html',
        controller: 'ViewChartController',
        controllerAs: 'chartVm'
      })

      .state('app.admin', {
        url: '/admin',
        abstract: true,
        templateUrl: 'app/admin/index.html'
      })
      .state('app.admin.main', {
        url: '',
        templateUrl: 'app/admin/main/main.html'
      })
      .state('app.admin.project', {
        url: '/project',
        abstract: true,
        templateUrl: 'app/__abstract.html'
      })
      .state('app.admin.project.main', {
        url: '',
        templateUrl: 'app/admin/project/main/project-main.html'
      })
      .state('app.admin.project.new', {
        url: '/new',
        templateUrl: 'app/admin/project/new/project-new.html',
        controller: 'AdminProjectNewController',
        controllerAs: 'vm'
      })
      .state('app.admin.project.edit', {
        url: '/:projectId',
        templateUrl: 'app/admin/project/edit/project-edit.html',
        controller: 'AdminProjectEditController',
        controllerAs: 'vm'
      });

    $urlRouterProvider.otherwise('/');
  }

})();
