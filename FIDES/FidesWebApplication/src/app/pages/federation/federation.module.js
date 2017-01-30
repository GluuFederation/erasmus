(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.federation', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('federation', {
        url: '/federation',
        title: 'Federations',
        templateUrl: 'app/pages/federation/federation.html',
        controller: 'FederationController',
        controllerAs: "vm",
        roles: ['admin'],
        authenticate: true,
        sidebarMeta: {
          icon: 'fa fa-users',
          order: 2
        }
      });
  }
})();
