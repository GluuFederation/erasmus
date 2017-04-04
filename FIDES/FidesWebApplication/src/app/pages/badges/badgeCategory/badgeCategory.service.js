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
            return $http.get(urls.BASE_API + '/badgeCategory');
        }

        function removeBadgeCategory(id) {
            return $http.delete(urls.BASE_API + '/badgeCategory/' + id);
        }

        function updateBadgeCategory(formData, id) {
            return $http.put(urls.BASE_API + '/badgeCategory/' + id, formData);
        }

        function createBadgeCategory(formData) {
            return $http.post(urls.BASE_API + '/badgeCategory', formData);
        }
        return service;
    }
})();
