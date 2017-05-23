(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.userActualBadge')
    .factory('userActualBadgeService', userActualBadgeService);

  /** @ngInject */
  function userActualBadgeService($http, urls) {
    var service = {
      getBadgeRequest: getBadgeRequest
    };

    function getBadgeRequest(formData) {
      return $http.post(urls.BADGE_URL + '/badges/request/list', formData, { headers: {'Authorization': 'Bearer 9a0564c0-c8da-498b-8337-3e05d26da21c'} });
    }

    return service;
  }
})();
