(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.badges')
        .factory('badgesService', badgesService);

    /** @ngInject */
    function badgesService($http, urls) {

        var service = {
            getBadges: getBadges,
            removeBadge: removeBadge,
            updateBadge: updateBadge,
            createBadge: createBadge
        };

        function getBadges() {
            $http.defaults.headers.common.Authorization = '';
            return $http.get(urls.BADGE_URL + "/badges");
        }

        function removeBadge(inum) {
            return $http.delete(urls.BADGE_URL + "/badges/" + inum);
        }

        function updateBadge(inum, badge) {
            return $http.post(urls.BADGE_URL + "/badges/update/" + inum, badge);
        }

        function createBadge(formData) {
            return $http.post(urls.BADGE_URL + "/badges", formData, {
                transformRequest: angular.identity,
                headers: {'Content-Type': undefined}
            });
        }
        return service;
    }

})();
