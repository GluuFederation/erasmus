(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register', [
    'angular-loading-bar',
    'ngAnimate',
    'ngStorage',
    'ui.bootstrap',
    'ui.sortable',
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
      BASE: 'http://localhost:3000',
      AUTH_URL: 'http://localhost:3000/login.html',
      BASE_API: 'http://localhost:8000'
    });
})();
