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
      createUser: createUser,
      getAllRoles: getAllRoles,
      getAllParticipants: getAllParticipants
    };

    function getUsers(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllUsers").then(onSuccess).catch(onError);
    }

    function removeUser(id, onSuccess, onError) {
      return $http.delete(urls.BASE_API + "/removeUser/" + id).then(onSuccess).catch(onError);
    }

    function createUser(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/signup", formData).then(onSuccess).catch(onError);
    }

    function updateUser(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/updateUser", formData).then(onSuccess).catch(onError);
    }

    function getAllRoles(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllRoles").then(onSuccess).catch(onError);
    }

    function getAllParticipants(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllParticipants").then(onSuccess).catch(onError);
    }

    return service;
  }
})();
