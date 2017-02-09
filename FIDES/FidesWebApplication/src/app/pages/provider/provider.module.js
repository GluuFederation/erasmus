(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.provider', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('provider', {
        url: '/provider',
        title: 'Providers',
        templateUrl: 'app/pages/provider/provider.html',
        controller: 'ProviderController',
        controllerAs: "vm",
        roles: ['admin', 'orgadmin'],
        authenticate: true,
        sidebarMeta: {
          icon: 'fa fa-ticket',
          order: 4
        }
      });
  }
})();
