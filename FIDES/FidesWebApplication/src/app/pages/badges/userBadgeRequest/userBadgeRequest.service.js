(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.userBadgeRequest')
    .factory('userBadgeRequestService', userBadgeRequestService);

  /** @ngInject */
  function userBadgeRequestService($http, urls) {
    var service = {
      getPendingBadges: getPendingBadges,
      badgeApprove: badgeApprove
    };

    function getPendingBadges(id) {
      return $http.get(urls.BADGE_URL + '/badges/request/listPending/' + id, { headers: {'Authorization': 'Bearer 9a0564c0-c8da-498b-8337-3e05d26da21c'} });
    }

    function badgeApprove(formData) {
      return $http.post(urls.BADGE_URL + '/badges/request/approve', formData, { headers: {'Authorization': 'Bearer 9a0564c0-c8da-498b-8337-3e05d26da21c'} });
    }

    return service;
  }
})();
