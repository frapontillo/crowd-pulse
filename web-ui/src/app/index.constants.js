/* global io:false, moment:false, FileReader:false */
(function() {
  'use strict';

  angular
    .module('webUi')
    .constant('io', io)
    .constant('moment', moment)
    .constant('FileReader', FileReader);

  angular.module('webUi')
    .constant('filterTerm', 'T')
    .constant('filterQuery', 'Q');

})();
