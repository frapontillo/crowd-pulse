/* global moment:false, FileReader:false */
(function() {
  'use strict';

  angular
    .module('webUi')
    .constant('moment', moment)
    .constant('FileReader', FileReader);

})();
