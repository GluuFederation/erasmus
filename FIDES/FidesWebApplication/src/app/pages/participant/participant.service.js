(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.participant')
    .factory('participantService', participantService);

  /** @ngInject */
  function participantService($http, urls) {
    var service = {
      removeParticipant: removeParticipant,
      updateParticipant: updateParticipant,
      approveParticipant: approveParticipant,
      getAllParticipants: getAllParticipants,
      updateParticipantWithFile: updateParticipantWithFile
    };

    function removeParticipant(orgId, onSuccess, onError) {
      return $http.delete(urls.FIDES_BASE_API + "/participant/" + orgId).then(onSuccess).catch(onError);
    }

    function updateParticipant(formData, onSuccess, onError) {
      return $http.put(urls.FIDES_BASE_API + "/participant", formData).then(onSuccess).catch(onError);
    }

    function approveParticipant(formData, onSuccess, onError) {
      return $http.post(urls.FIDES_BASE_API + "/participant/" + formData.pid + "/federation/" + formData.fid).then(onSuccess).catch(onError);
    }

    function getAllParticipants(onSuccess, onError) {
      return $http.get(urls.FIDES_BASE_API + "/participant").then(onSuccess).catch(onError);
    }

    function updateParticipantWithFile(formData, onSuccess, onError) {
      return $http.put(urls.FIDES_BASE_API + "/participant", formData, {
        headers: {'Content-Type': undefined },
        transformRequest: angular.identity
      }).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
