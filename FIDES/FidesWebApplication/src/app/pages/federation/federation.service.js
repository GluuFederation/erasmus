(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.federation')
    .factory('federationService', federationService);

  /** @ngInject */
  function federationService($http, urls) {
    var service = {
      removeFederation: removeFederation,
      updateFederation: updateFederation,
      addFederation: addFederation,
      getAllFederations: getAllFederations
    };

    function removeFederation(fedId, onSuccess, onError) {
      return $http.delete(urls.BASE_API + "/removeFederation/" + fedId).then(onSuccess).catch(onError);
    }

    function addFederation(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/addFederation", formData).then(onSuccess).catch(onError);
    }

    function updateFederation(formData, onSuccess, onError) {
      return $http.put(urls.BASE_API + "/updateFederation", formData).then(onSuccess).catch(onError);
    }

    function getAllFederations(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllFederations").then(onSuccess).catch(onError);
    }

    return service;
  }
})();
