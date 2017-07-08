(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.user')
    .factory('userService', userService);

  /** @ngInject */
  function userService($http, urls) {
    var service = {
      getUsers: getUsers,
      removeUser: removeUser,
      updateUser: updateUser,
      getAllRoles: getAllRoles,
      getAllParticipants: getAllParticipants
    };

    function getUsers(onSuccess, onError) {
      return $http.get(urls.FIDES_BASE_API + "/user").then(onSuccess).catch(onError);
    }

    function removeUser(id, onSuccess, onError) {
      return $http.delete(urls.FIDES_BASE_API + "/user/" + id).then(onSuccess).catch(onError);
    }

    function updateUser(formData, onSuccess, onError) {
      return $http.put(urls.FIDES_BASE_API + "/user", formData).then(onSuccess).catch(onError);
    }

    function getAllRoles(onSuccess, onError) {
      return $http.get(urls.FIDES_BASE_API + "/role").then(onSuccess).catch(onError);
    }

    function getAllParticipants(onSuccess, onError) {
      return $http.get(urls.FIDES_BASE_API + "/participant").then(onSuccess).catch(onError);
    }

    return service;
  }
})();
