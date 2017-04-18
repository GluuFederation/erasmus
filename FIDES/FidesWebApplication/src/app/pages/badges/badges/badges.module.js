(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.badges', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider, $localStorageProvider) {
        $stateProvider
            .state('badges.badges', {
                url: '/badges',
                templateUrl: 'app/pages/badges/badges/badges.html',
                controller: 'BadgesCtrl',
                controllerAs: 'vm',
                roles: ['admin', 'orgadmin'],
                title: ((!!$localStorageProvider.get("currentUser")) && $localStorageProvider.get("currentUser").role === 'orgadmin') ? 'Badge Admin' : 'Badge Templates',
                sidebarMeta: {
                    order: 2
                }
            });
    }
})();