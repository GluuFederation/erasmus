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
      return $http.get(urls.BADGE_URL + '/badges/request/listPending/' + id, { headers: {'Authorization': ''} });
    }

    function badgeApprove(formData) {
      return $http.post(urls.BADGE_URL + '/badges/request/approve', formData, { headers: {'Authorization': ''} });
    }

    return service;
  }
})();
