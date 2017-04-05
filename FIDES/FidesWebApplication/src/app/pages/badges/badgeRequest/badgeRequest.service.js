(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeRequest')
    .factory('badgeRequestService', badgeRequestService);

  /** @ngInject */
  function badgeRequestService($http, urls) {
    var service = {
      badgeRequest: badgeRequest,
      badgeApprove: badgeApprove,
      getOrganizationById: getOrganizationById
    };

    function badgeRequest(formData) {
      return $http.post(urls.BASE_API + '/badgeRequest', formData);
    }

    function badgeApprove(formData) {
      return $http.post(urls.BASE_API + '/badgeApprove', formData);
    }

    function getOrganizationById(oid) {
      return $http.get(urls.BASE_API + '/getOrganizationById/' + oid);
    }

    return service;
  }

})();
