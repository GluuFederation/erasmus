(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.userBadgeRequest', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('badges.userBadgeRequest', {
                url: '/userBadgeRequest',
                templateUrl: 'app/pages/badges/userBadgeRequest/userBadgeRequest.html',
                controller: 'userBadgeRequestCtrl',
                controllerAs: 'vm',
                roles: ['orgadmin'],
                title: 'Badge Review',
                sidebarMeta: {
                    order: 4
                }
            });
    }
})();