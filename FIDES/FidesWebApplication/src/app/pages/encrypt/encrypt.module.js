(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.encrypt', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('encrypt', {
        url: '/encrypt',
        title: 'Encrypt',
        templateUrl: 'app/pages/encrypt/encrypt.html',
        controller: 'EncryptController',
        controllerAs: "vm",
        authenticate: true,
        roles: ['admin'],
        sidebarMeta: {
          icon: 'fa fa-puzzle-piece',
          order: 5
        }
      });
  }
})();
