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
      return $http.delete(urls.FIDES_BASE_API + "/federations/" + fedId).then(onSuccess).catch(onError);
    }

    function addFederation(formData, onSuccess, onError) {
      return $http.post(urls.FIDES_BASE_API + "/federations", formData).then(onSuccess).catch(onError);
    }

    function updateFederation(formData, onSuccess, onError) {
      return $http.put(urls.FIDES_BASE_API + "/federations", formData).then(onSuccess).catch(onError);
    }

    function getAllFederations(onSuccess, onError) {
      return $http.get(urls.FIDES_BASE_API + "/federations").then(onSuccess).catch(onError);
    }

    return service;
  }
})();
