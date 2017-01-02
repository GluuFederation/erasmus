(function () {
  'use strict';

  angular.module('FidesWebApplication.pages', [
    'ui.router',

    'FidesWebApplication.pages.home',
    'FidesWebApplication.pages.user',
    'FidesWebApplication.pages.provider',
    'FidesWebApplication.pages.login',
    'FidesWebApplication.pages.register'
  ]).config(routeConfig);

  /** @ngInject */
  function routeConfig($urlRouterProvider) {
    //$urlRouterProvider.otherwise('/home');
  }
})();
