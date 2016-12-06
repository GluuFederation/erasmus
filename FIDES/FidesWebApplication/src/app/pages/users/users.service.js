(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.users')
    .factory('userService', userService);

  /** @ngInject */
  function userService($http, urls) {
    var service = {
      getUsers: getUsers,
      removeUser: removeUser,
      updateUser: updateUser,
      login: login,
      createUser: createUser,
    };

    function login(formData, onSuccess, onError) {
      return $http.get(urls.BASE_API + "/login", formData).then(onSuccess).catch(onError);
    }

    function getUsers(onSuccess, onError) {
      return $http.get(urls.BASE_API + "/getAllUsers").then(onSuccess).catch(onError);
    }

    function removeUser(username, onSuccess, onError) {
      return $http.delete(urls.BASE_API + "/removeUser/" + username).then(onSuccess).catch(onError);
    }

    function createUser(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/signup", formData).then(onSuccess).catch(onError);
    }

    function updateUser(formData, onSuccess, onError) {
      console.log(formData);
      return $http.post(urls.BASE_API + "/updateUser", formData).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
