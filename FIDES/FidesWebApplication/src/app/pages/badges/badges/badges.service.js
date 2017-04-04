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
      createBadge: createBadge,
      getBadgeByOrganization: getBadgeByOrganization
    };

    function getBadges() {
      return $http.get(urls.BASE_API + '/badges');
    }

    function removeBadge(id) {
      return $http.delete(urls.BASE_API + '/badges/' + id);
    }

    function updateBadge(id, formData) {
      return $http.put(urls.BASE_API + '/badges/' + id, formData);
    }

    function createBadge(formData) {
      return $http.post(urls.BASE_API + '/badges', formData, {
        transformRequest: angular.identity,
        headers: {'Content-Type': undefined}
      });
    }

    function getBadgeByOrganization(id, status) {
      return $http.get(urls.BASE_API + '/getBadgeByOrganization/' + id + '/' + status);
    }

    return service;
  }
})();
