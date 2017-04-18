(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.participant', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('participant', {
        url: '/participant',
        title: 'Participants',
        templateUrl: 'app/pages/participant/participant.html',
        controller: 'ParticipantController',
        controllerAs: 'vm',
        roles: ['admin'],
        authenticate: true,
        sidebarMeta: {
          icon: 'fa fa-users',
          order: 3
        }
      });
  }
})();
