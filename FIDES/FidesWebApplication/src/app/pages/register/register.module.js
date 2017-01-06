(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register', [
    'ngAnimate',
    'ngStorage',
    'ui.bootstrap',
    'ui.sortable',
    'ui.router',
    'ngTouch',
    'toastr',
    'smart-table',
    "xeditable",
    'ui.slimscroll',
    'ngJsTree',
    'angular-progress-button-styles',

    'FidesWebApplication.theme',
    'FidesWebApplication.pages'
  ])
    .constant('urls', {
      BASE: 'http://erasmus.gluu.org',
      AUTH_URL: 'http://erasmus.gluu.org/login.html',
      BASE_API: 'http://erasmus.gluu.org:8000'
    });

})();
