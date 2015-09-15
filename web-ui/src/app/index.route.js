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
        templateUrl: 'app/__abstract.html'
      })
      .state('app.view', {
        url: '/view',
        templateUrl: 'app/view/main/main.html',
        controller: 'ViewMainController',
        controllerAs: 'main'
      })
      .state('app.admin', {
        url: '/admin',
        abstract: true,
        templateUrl: 'app/admin/index.html',
        controller: 'AdminIndexController',
        controllerAs: 'admin'
      })
      .state('app.admin.main', {
        url: '',
        templateUrl: 'app/admin/main/main.html',
        controller: 'AdminMainController',
        controllerAs: 'admin'
      })
      .state('app.admin.project', {
        url: '/project/:projectId',
        templateUrl: 'app/admin/project/project.html',
        controller: 'AdminProjectController',
        controllerAs: 'project'
      });



    $urlRouterProvider.otherwise('/');
  }

})();
