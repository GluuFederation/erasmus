(function () {
  'use strict';

  angular.module('FidesWebApplication.pages', [
    'ui.router',

    'FidesWebApplication.pages.home',
    'FidesWebApplication.pages.user',
    'FidesWebApplication.pages.federation',
    'FidesWebApplication.pages.participant',
    'FidesWebApplication.pages.entity',
    'FidesWebApplication.pages.login',
    'FidesWebApplication.pages.register',
    'FidesWebApplication.pages.badges'
  ]).config(routeConfig);

  /** @ngInject */
  function routeConfig($urlRouterProvider) {
    //$urlRouterProvider.otherwise('/home');
  }
})();
