(function () {
  'use strict';

  angular.module('FidesWebApplication.pages', [
    'ui.router',

    'FidesWebApplication.pages.login',
    'FidesWebApplication.pages.home',
    'FidesWebApplication.pages.users',
    // 'FidesWebApplication.pages.dashboard',
    // 'FidesWebApplication.pages.ui',
    // 'FidesWebApplication.pages.components',
    // 'FidesWebApplication.pages.form',
    //'FidesWebApplication.pages.tables',
    // 'FidesWebApplication.pages.charts',
    // 'FidesWebApplication.pages.maps',
    //'FidesWebApplication.pages.profile',
  ]).config(routeConfig);

  /** @ngInject */
  function routeConfig($urlRouterProvider, baSidebarServiceProvider) {
    $urlRouterProvider.otherwise('/home');

    /*baSidebarServiceProvider.addStaticItem({
     title: 'Pages',
     icon: 'ion-document',
     subMenu: [{
     title: 'Sign In',
     fixedHref: 'login.html',
     blank: true
     }, {
     title: 'Sign Up',
     fixedHref: 'reg.html',
     blank: true
     }, {
     title: 'User Profile',
     stateRef: 'profile'
     }, {
     title: '404 Page',
     fixedHref: '404.html',
     blank: true
     }]
     });
     baSidebarServiceProvider.addStaticItem({
     title: 'Menu Level 1',
     icon: 'ion-ios-more',
     subMenu: [{
     title: 'Menu Level 1.1',
     disabled: true
     }, {
     title: 'Menu Level 1.2',
     subMenu: [{
     title: 'Menu Level 1.2.1',
     disabled: true
     }]
     }]
     });*/
  }

})();
