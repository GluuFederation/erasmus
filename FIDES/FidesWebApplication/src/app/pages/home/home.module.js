(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.home', [])
    .config(routeConfig);

  /** @ngInject */
  function routeConfig($stateProvider, $localStorageProvider) {
    $stateProvider
      .state('home', {
        url: '/home',
        title: ((!!$localStorageProvider.get("currentUser")) && $localStorageProvider.get("currentUser").role === 'orgadmin') ? 'Participant' : 'Home',
        templateUrl: 'app/pages/home/home.html',
        controller: 'HomeController',
        controllerAs: "vm",
        authenticate: true,
        sidebarMeta: {
          icon: 'ion-android-home',
          order: 0
        }
      });
  }
})();
