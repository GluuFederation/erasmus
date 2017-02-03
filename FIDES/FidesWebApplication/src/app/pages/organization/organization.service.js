(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.organization')
    .factory('organizationService', organizationService);

  /** @ngInject */
  function organizationService($http, urls) {
    var service = {
      removeOrganization: removeOrganization,
      updateOrganization: updateOrganization,
      approveOrganization: approveOrganization,
      getAllOrganizations: getAllOrganizations
    };

    function removeOrganization(orgId, onSuccess, onError) {
      return $http.delete(urls.BASE_API + "/removeOrganization/" + orgId).then(onSuccess).catch(onError);
    }

    function updateOrganization(formData, onSuccess, onError) {
      return $http.put(urls.BASE_API + "/updateOrganization", formData).then(onSuccess).catch(onError);
    }

    function approveOrganization(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/approveOrganization", formData).then(onSuccess).catch(onError);
    }

    function getAllOrganizations(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllOrganizations").then(onSuccess).catch(onError);
    }

    return service;
  }
})();
