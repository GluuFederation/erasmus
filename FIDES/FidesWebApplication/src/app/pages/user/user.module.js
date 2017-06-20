(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.user', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('user', {
        url: '/user',
        title: 'Users',
        templateUrl: 'app/pages/user/user.html',
        controller: 'UserController',
        controllerAs: "vm",
        roles: ['admin'],
        authenticate: true,
        sidebarMeta: {
          icon: 'fa fa-users',
          order: 1
        }
      });
  }
})();
