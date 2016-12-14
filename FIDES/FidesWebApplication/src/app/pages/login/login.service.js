(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login')
    .factory('loginService', loginService);

  /** @ngInject */
  function loginService($http, urls) {

    var service = {
      login: login,
      updateUser: updateUser,
      updatePassword: updatePassword
    };

    function login(username, password, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/login", {
        username: username,
        password: password
      }).success(onSuccess).catch(onError);
    }

    function updateUser(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/updateUser", formData).then(onSuccess).catch(onError);
    }

    function updatePassword(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/updatePassword", formData).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
