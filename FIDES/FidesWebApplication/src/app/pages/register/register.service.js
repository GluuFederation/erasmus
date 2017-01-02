(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .factory('registerService', registerService);

  /** @ngInject */
  function registerService($http, urls) {

    var service = {
      register: register,
      updatePassword: updatePassword
    };

    function register(username, password, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/register", {
        username: username,
        password: password
      }).success(onSuccess).catch(onError);
    }

    function updatePassword(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/updatePassword", formData).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
