(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register', [
    'angular-loading-bar',
    'ngAnimate',
    'ngStorage',
    'ui.bootstrap',
    'ui.sortable',
    'ui.select',
    'ngSanitize',
    'ui.router',
    'ngTouch',
    'toastr',
    'angular-progress-button-styles',
    'FidesWebApplication.theme'
  ]).config(function ($locationProvider) {
    $locationProvider.html5Mode({
      enabled: true,
      requireBase: false,
      rewriteLinks: true
    });
  }).constant('urls', {
      BASE: 'https://127.0.0.1:3000',
      AUTH_URL: 'https://127.0.0.1:3000/login.html',
      FIDES_BASE_API: 'http://127.0.0.1:8000'
    });
})();
