(function() {
  'use strict';

  angular
    .module('webUi')
    .directive('toolbar', toolbar);

  /** @ngInject */
  function toolbar() {
    var directive = {
      restrict: 'E',
      templateUrl: 'app/components/toolbar/toolbar.html',
      scope: {
        title: '='
      },
      controller: ToolbarController,
      controllerAs: 'vm',
      bindToController: true
    };

    return directive;

    /** @ngInject */
    function ToolbarController($mdSidenav) {
      var vm = this;

      vm.toggleMainSidenav = function() {
        return $mdSidenav('main-sidenav').toggle();
      };
    }
  }

})();
