(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login', [
    'ngStorage',
    'toastr'
  ])//.config(routeConfig)
    .constant('urls', {
      BASE: 'http://192.168.200.70:3000',
      AUTH_URL: 'http://192.168.200.70:3000/login.html',
      BASE_API: 'http://192.168.200.70:8000',
    });

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('login', {
        url: '/login',
        templateUrl: 'app/pages/login/login.html',
        controller: 'LoginController',
        controllerAs: 'vm',
        fixedHref: 'login.html',
        blank: true,
        title: 'Login',
        sidebarMeta: {
          order: 800,
        },
      })
      /*.state('login', {
        url: '/login',
        title: 'Login',
        redirectTo: '/login.html',
        authenticate: false
      });*/
  }

})();