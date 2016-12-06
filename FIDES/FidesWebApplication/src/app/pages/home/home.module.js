(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.home', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider) {
    $stateProvider
      .state('home', {
        url: '/home',
        title: 'Home',
        templateUrl: 'app/pages/home/home.html',
        controller: 'HomeController',
        controllerAs: "vm",
        role: ['admin', 'issuer'],
        sidebarMeta: {
          icon: 'ion-android-home',
          order: 0,
        },
      });
  }

})();
