(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.encrypt')
    .factory('encryptService', encryptService);

  /** @ngInject */
  function encryptService($http, urls) {
    var service = {
        encrypt: encrypt
    };

    function encrypt(formData, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/encrypt", formData).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
