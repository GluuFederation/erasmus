(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.badges.badgeCategory')
        .factory('badgeCategoryService', badgeCategoryService);

    /** @ngInject */
    function badgeCategoryService($http, urls) {
        var service = {
            getAllBadgeCategory: getAllBadgeCategory,
            removeBadgeCategory: removeBadgeCategory,
            updateBadgeCategory: updateBadgeCategory,
            createBadgeCategory: createBadgeCategory
        };

        function getAllBadgeCategory() {
            return $http.get(urls.FIDES_BASE_API + '/badgeCategory');
        }

        function removeBadgeCategory(id) {
            return $http.delete(urls.FIDES_BASE_API + '/badgeCategory/' + id);
        }

        function updateBadgeCategory(formData, id) {
            return $http.put(urls.FIDES_BASE_API + '/badgeCategory/' + id, formData);
        }

        function createBadgeCategory(formData) {
            return $http.post(urls.FIDES_BASE_API + '/badgeCategory', formData);
        }
        return service;
    }
})();
