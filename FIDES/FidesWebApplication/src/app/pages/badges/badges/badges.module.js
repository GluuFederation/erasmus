(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.badges', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('badges.badges', {
                url: '/badges',
                templateUrl: 'app/pages/badges/badges/badges.html',
                controller: 'BadgesCtrl',
                controllerAs: 'vm',
                roles: ['admin', 'orgadmin'],
                title: 'Badges',
                sidebarMeta: {
                    order: 2
                }
            });
    }
})();