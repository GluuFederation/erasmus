(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.badgeApprove', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('badges.badgeApprove', {
                url: '/badgeApprove',
                templateUrl: 'app/pages/badges/badgeApprove/badgeApprove.html',
                controller: 'BadgeApproveCtrl',
                controllerAs: 'vm',
                roles: ['admin'],
                title: 'Badge Request',
                sidebarMeta: {
                    order: 3
                }
            });
    }
})();