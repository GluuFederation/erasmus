(function () {
  'use strict';

  angular.module('FidesWebApplication.pages', [
    'ui.router',

    'FidesWebApplication.pages.home',
    'FidesWebApplication.pages.users',
    'FidesWebApplication.pages.login',
  ]).config(routeConfig);

  /** @ngInject */
  function routeConfig($urlRouterProvider) {
    $urlRouterProvider.otherwise('/home');
  }
})();
