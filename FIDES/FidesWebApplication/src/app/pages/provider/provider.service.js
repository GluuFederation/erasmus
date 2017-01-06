(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.provider')
    .factory('providerService', providerService);

  /** @ngInject */
  function providerService($http, urls) {
    var service = {
      getProviders: getProviders,
      removeProvider: removeProvider,
      updateProvider: updateProvider,
      createProvider: createProvider,
      approveProvider: approveProvider,
      verifyProvider: verifyProvider,
      //getAllOrganizations: getAllOrganizations
    };

    function getProviders(userId, onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllProviders/" + userId).then(onSuccess).catch(onError);
    }

    function removeProvider(providerId, onSuccess, onError) {
      return $http.delete(urls.BASE_API + "/removeProvider/" + providerId).then(onSuccess).catch(onError);
    }

    function approveProvider(providerId, onSuccess, onError) {
      return $http.get(urls.BASE_API + "/approveProvider/" + providerId).then(onSuccess).catch(onError);
    }

    function createProvider(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/createProvider", formData).then(onSuccess).catch(onError);
    }

    function updateProvider(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/updateProvider", formData).then(onSuccess).catch(onError);
    }

    function getAllOrganizations(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllOrganizations").then(onSuccess).catch(onError);
    }

    function verifyProvider(provider, onSuccess, onError) {
      //return $http.get(urls.BASE_API + "/approveProvider/" + providerId).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
