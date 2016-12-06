(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.users', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('users', {
        url: '/users',
        title: 'Users',
        templateUrl: 'app/pages/users/users.html',
        controller: 'UsersController',
        controllerAs: "vm",
        role: ['admin'],
        sidebarMeta: {
          icon: 'fa fa-users',
          order: 2,
        },
      });
  }

})();
