(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login')
    .factory('loginService', loginService);

  /** @ngInject */
  function loginService($http, urls) {

    var service = {
      login: login,
    };

    function login(username, password, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/login", {
        username: username,
        password: password
      }).success(onSuccess).catch(onError);
    }

    return service;
  }
})();
