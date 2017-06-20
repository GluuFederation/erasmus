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
      return $http.delete(urls.BASE_API + "/removeParticipant/" + orgId).then(onSuccess).catch(onError);
    }

    function updateParticipant(formData, onSuccess, onError) {
      return $http.put(urls.BASE_API + "/updateParticipant", formData).then(onSuccess).catch(onError);
    }

    function approveParticipant(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/approveParticipant", formData).then(onSuccess).catch(onError);
    }

    function getAllParticipants(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllParticipants").then(onSuccess).catch(onError);
    }

    function updateParticipantWithFile(formData, onSuccess, onError) {
      return $http.put(urls.BASE_API + "/updateParticipant", formData, {
        headers: {'Content-Type': undefined },
        transformRequest: angular.identity
      }).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
