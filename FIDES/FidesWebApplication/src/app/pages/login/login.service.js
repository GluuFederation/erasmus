(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login')
    .factory('loginService', loginService);

  /** @ngInject */
  function loginService($http, urls) {

    var service = {
      login: login,
      validateEmail: validateEmail,
      updatePassword: updatePassword,
      getUSStateCity: getUSStateCity
    };

    function login(authDetail, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/login", authDetail).success(onSuccess).catch(onError);
    }

    function validateEmail(email, isBadge, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/validateEmail", {
        email: email,
        isBadge: isBadge
      }).success(onSuccess).catch(onError);
    }

    function updatePassword(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/updatePassword", formData).then(onSuccess).catch(onError);
    }

    function getUSStateCity() {
      return $http.get('us_states_cities.json');
    }

    return service;
  }
})();
