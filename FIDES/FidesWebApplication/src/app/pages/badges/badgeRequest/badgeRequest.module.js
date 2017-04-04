(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.badgeRequest', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('badges.badgeRequest', {
                url: '/badgeRequest',
                templateUrl: 'app/pages/badges/badgeRequest/badgeRequest.html',
                controller: 'BadgeRequestCtrl',
                controllerAs: 'vm',
                roles: ['orgadmin'],
                title: 'Badge Request',
                sidebarMeta: {
                    order: 3
                }
            });
    }
})();