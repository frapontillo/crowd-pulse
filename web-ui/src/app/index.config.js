(function() {
  'use strict';

  angular
    .module('webUi')
    .config(config);

  /** @ngInject */
  function config($logProvider, configProvider, RestangularProvider) {
    // Enable log
    $logProvider.debugEnabled(true);

    // Set the config.json path
    configProvider.useConfigPath('/config.json');
    configProvider.addConfigResolvedListener(function(data) {
      RestangularProvider.setBaseUrl(data['api']);
    });

    // Set options third-party lib
    // Mongo IDs are "_id", not "id"
    RestangularProvider.setRestangularFields({
      id: "_id"
    });
  }

})();
