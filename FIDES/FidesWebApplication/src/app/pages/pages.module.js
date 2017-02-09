(function () {
  'use strict';

  angular.module('FidesWebApplication.pages', [
    'ui.router',

    'FidesWebApplication.pages.home',
    'FidesWebApplication.pages.user',
    'FidesWebApplication.pages.federation',
    'FidesWebApplication.pages.organization',
    'FidesWebApplication.pages.provider',
    'FidesWebApplication.pages.login',
    'FidesWebApplication.pages.register',
    'FidesWebApplication.pages.encrypt'
  ]).config(routeConfig);

  /** @ngInject */
  function routeConfig($urlRouterProvider) {
    //$urlRouterProvider.otherwise('/home');
  }
})();
