(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .factory('registerService', registerService);

  /** @ngInject */
  function registerService($http, urls) {

    var service = {
      validateRegistrationDetail: validateRegistrationDetail,
      registerDetail: registerDetail
    };

    function validateRegistrationDetail(providerInfo, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/validateRegistrationDetail", providerInfo).then(onSuccess).catch(onError);
    }

    function registerDetail(providerInfo, clientInfo, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/registerDetail", {
        providerInfo: providerInfo,
        clientInfo: clientInfo
      }).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
