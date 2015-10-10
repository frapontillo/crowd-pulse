(function() {
  'use strict';

  angular
    .module('webUi')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log) {
    $log.debug('runBlock end');
  }

})();
