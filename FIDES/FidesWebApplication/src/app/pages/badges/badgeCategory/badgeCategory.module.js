(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.badgeCategory', [])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('badges.badgeCategory', {
                url: '/badgeCategory',
                templateUrl: 'app/pages/badges/badgeCategory/badgeCategory.html',
                controller: 'badgeCategoryCtrl',
                controllerAs: 'vm',
                roles: ['admin'],
                title: 'Badge Category',
                sidebarMeta: {
                    order: 1
                }
            });
    }
})();