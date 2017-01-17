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
    'angular-progress-button-styles',
    'FidesWebApplication.theme',
    'FidesWebApplication.pages.user'
  ])
    .constant('urls', {
      BASE: 'http://192.168.200.70:3000',
      AUTH_URL: 'http://192.168.200.70:3000/login.html',
      BASE_API: 'http://192.168.200.70:8000'
    });
})();
