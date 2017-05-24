(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.userBadgeRequest')
    .factory('userBadgeRequestService', userBadgeRequestService);

  /** @ngInject */
  function userBadgeRequestService($http, urls) {
    var service = {
      getBadges: getBadges,
      badgeApprove: badgeApprove,
      badgeInfo: badgeInfo
    };

    function getBadges(id, status) {
      return $http.get(urls.BADGE_URL + '/badges/request/list/' + id + '/' + status, { headers: {'Authorization': 'Bearer 9a0564c0-c8da-498b-8337-3e05d26da21c'} });
    }

    function badgeApprove(formData) {
      return $http.post(urls.BADGE_URL + '/badges/request/approve', formData, { headers: {'Authorization': 'Bearer 9a0564c0-c8da-498b-8337-3e05d26da21c'} });
    }

    function badgeInfo(id) {
      return $http.get(urls.BADGE_URL + '/badges/' + id, { headers: {'Authorization': 'Bearer 9a0564c0-c8da-498b-8337-3e05d26da21c'} });
    }

    return service;
  }
})();
