(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.organization', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('organization', {
        url: '/organization',
        title: 'Organizations',
        templateUrl: 'app/pages/organization/organization.html',
        controller: 'OrganizationController',
        controllerAs: "vm",
        roles: ['admin'],
        authenticate: true,
        sidebarMeta: {
          icon: 'fa fa-users',
          order: 3
        }
      });
  }
})();
