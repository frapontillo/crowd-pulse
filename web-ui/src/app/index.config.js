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
    var interceptor = function($q, $rootScope, $log, $timeout, toolbarLoadedEvent, toolbarLoadingEvent) {
      var requests = 0;
      var completed = 0;
      var timeoutPromise;

      var handleCompletion = function() {
        completed++;
      };

      var evaluateAsync = function() {
        // if we are already waiting for something, discard it as this is newer
        if (timeoutPromise) {
          $timeout.cancel(timeoutPromise);
        }
        timeoutPromise = $timeout(function() {
          if (completed >= requests) {
            completed = 0;
            requests = 0;
            return $rootScope.$broadcast(toolbarLoadedEvent);
          }
          return $rootScope.$broadcast(toolbarLoadingEvent);
        }, 300);
      };

      return {
        'request': function(config) {
          if (requests === 0) {
            $rootScope.$broadcast(toolbarLoadingEvent);
          }
          requests++;
          return config;
        },

        'response': function(response) {
          handleCompletion();
          evaluateAsync();
          return response;
        },

        'responseError': function(rejection) {
          handleCompletion();
          evaluateAsync();
          return $q.reject(rejection);
        }
      };
    };

    $httpProvider.interceptors.push(interceptor);
  }

})();
