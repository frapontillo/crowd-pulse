(function() {
  'use strict';

  angular
    .module('webUi')
    .config(config);

  /** @ngInject */
  function config($logProvider, $httpProvider, configProvider, RestangularProvider) {
    // Enable log
    $logProvider.debugEnabled(true);

    // Set the config.json path
    configProvider.useConfigPath('/config.json');
    configProvider.addConfigResolvedListener(function(data) {
      RestangularProvider.setBaseUrl(data.api);
    });

    // Set options third-party lib
    // Mongo IDs are "_id", not "id"
    RestangularProvider.setRestangularFields({
      id: "_id"
    });

    /** @ngInject */
    var interceptor = function($q, $rootScope, $log, toolbarLoadedEvent, toolbarLoadingEvent) {
      var requests = 0;

      var broadcastEventually = function() {
        if (requests === 0) {
          return $rootScope.$broadcast(toolbarLoadedEvent);
        }

        if (requests > 0) {
          return $rootScope.$broadcast(toolbarLoadingEvent);
        }

        return $log.warn('Current number of requests is incompatible (' + requests +
                         '), please check what\'s going on.');
      };

      return {
        'request': function(config) {
          requests++;
          broadcastEventually();
          return config;
        },

        'response': function(response) {
          requests--;
          broadcastEventually();
          return response;
        },

        'responseError': function(rejection) {
          requests--;
          broadcastEventually();
          return $q.reject(rejection);
        }
      };
    };

    $httpProvider.interceptors.push(interceptor);
  }

})();
