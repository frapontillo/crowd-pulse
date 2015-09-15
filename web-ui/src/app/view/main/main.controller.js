(function() {
  'use strict';

  angular
    .module('webUi')
    .controller('ViewMainController', ViewMainController);

  /** @ngInject */
  function ViewMainController() {
    var vm = this;

    vm.title = '';
  }
})();
