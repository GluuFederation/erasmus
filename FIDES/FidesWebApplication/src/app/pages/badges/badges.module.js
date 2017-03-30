(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges', [
        'FidesWebApplication.pages.badges.badges'
    ])
        .config(routeConfig);

    /** @ngInject */
    function routeConfig($stateProvider) {
        $stateProvider
            .state('badges', {
                url: '/badges',
                abstract: true,
                template: '<div ui-view></div>',
                title: 'Badges',
                sidebarMeta: {
                    icon: 'fa fa-bookmark',
                    order: 6
                }
            })
    }
})();
