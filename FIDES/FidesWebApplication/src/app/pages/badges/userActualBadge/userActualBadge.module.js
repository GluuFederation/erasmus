(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.userActualBadge', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('badges.userActualBadge', {
                url: '/userActualBadge',
                templateUrl: 'app/pages/badges/userActualBadge/userActualBadge.html',
                controller: 'userActualBadgeCtrl',
                controllerAs: 'vm',
                roles: ['orgadmin'],
                title: 'Badges',
                sidebarMeta: {
                    order: 4
                }
            });
    }
})();