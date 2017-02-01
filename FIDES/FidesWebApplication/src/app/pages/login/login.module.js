(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login', [
    'angular-loading-bar',
    'ngAnimate',
    'ui.bootstrap',
    'ngStorage',
    'toastr'
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

  /** @ngInject */
  /*function routeConfig($stateProvider) {
    $stateProvider
      .state('login', {
        url: '/login',
        templateUrl: 'app/pages/login/login.html',
        controller: 'LoginController',
        controllerAs: 'vm',
        fixedHref: 'login.html',
        blank: true,
        title: 'Login'
      });
  }*/
})();