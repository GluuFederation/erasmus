(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeRequest')
    .factory('badgeRequestService', badgeRequestService);

  /** @ngInject */
  function badgeRequestService($http, urls) {
    var service = {
      badgeRequest: badgeRequest,
      badgeApprove: badgeApprove,
      getParticipantById: getParticipantById
    };

    function badgeRequest(formData) {
      return $http.post(urls.BASE_API + '/badgeRequest', formData);
    }

    function badgeApprove(formData) {
      return $http.post(urls.BASE_API + '/badgeApprove', formData);
    }

    function getParticipantById(oid) {
      return $http.get(urls.BASE_API + '/getParticipantById/' + oid);
    }

    return service;
  }

})();
