(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.entity')
    .factory('entityService', entityService);

  /** @ngInject */
  function entityService($http, urls) {
    var service = {
      getEntities: getEntities,
      removeEntity: removeEntity,
      updateEntity: updateEntity,
      createEntity: createEntity,
      approveEntity: approveEntity
    };

    function getEntities(userId, onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllEntities/" + userId).then(onSuccess).catch(onError);
    }

    function removeEntity(entityId, onSuccess, onError) {
      return $http.delete(urls.BASE_API + "/removeEntity/" + entityId).then(onSuccess).catch(onError);
    }

    function approveEntity(entityId, onSuccess, onError) {
      return $http.get(urls.BASE_API + "/approveEntity/" + entityId).then(onSuccess).catch(onError);
    }

    function createEntity(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/createEntity", formData).then(onSuccess).catch(onError);
    }

    function updateEntity(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/updateEntity", formData).then(onSuccess).catch(onError);
    }

    function getAllOrganizations(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllOrganizations").then(onSuccess).catch(onError);
    }

    return service;
  }
})();
