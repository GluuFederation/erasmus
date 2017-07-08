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
      return $http.get(urls.FIDES_BASE_API + "/entity?uid=" + userId).then(onSuccess).catch(onError);
    }

    function removeEntity(entityId, onSuccess, onError) {
      return $http.delete(urls.FIDES_BASE_API + "/entity/" + entityId).then(onSuccess).catch(onError);
    }

    function approveEntity(entityId, onSuccess, onError) {
      return $http.post(urls.FIDES_BASE_API + "/entity/approve/" + entityId).then(onSuccess).catch(onError);
    }

    function createEntity(formData, onSuccess, onError) {
      return $http.post(urls.FIDES_BASE_API + "/entity", formData).then(onSuccess).catch(onError);
    }

    function updateEntity(formData, onSuccess, onError) {
      return $http.post(urls.FIDES_BASE_API + "/entity", formData).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
