(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.entity', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('entity', {
        url: '/entity',
        title: 'Entities',
        templateUrl: 'app/pages/entity/entity.html',
        controller: 'EntityController',
        controllerAs: 'vm',
        roles: ['admin', 'orgadmin'],
        authenticate: true,
        sidebarMeta: {
          icon: 'fa fa-ticket',
          order: 4
        }
      });
  }
})();
