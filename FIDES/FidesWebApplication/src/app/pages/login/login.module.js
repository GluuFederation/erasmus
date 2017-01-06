(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login', [
    'ui.bootstrap',
    'ngStorage',
    'toastr'
  ])//.config(routeConfig)
    .constant('urls', {
      BASE: 'http://erasmus.gluu.org',
      AUTH_URL: 'http://erasmus.gluu.org/login.html',
      BASE_API: 'http://erasmus.gluu.org:8000'
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