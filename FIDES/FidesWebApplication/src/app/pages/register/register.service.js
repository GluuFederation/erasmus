(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .factory('registerService', registerService);

  /** @ngInject */
  function registerService($http, urls) {

    var service = {
      validateRegistrationDetail: validateRegistrationDetail,
      registerDetail: registerDetail,
      isUserAlreadyExist: isUserAlreadyExist,
      getUSStateCity: getUSStateCity
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

    function isUserAlreadyExist(personInfo, onSuccess, onError) {
      return $http.get(urls.BASE_API + "/isUserAlreadyExist/" + personInfo.email).then(onSuccess).catch(onError);
    }

    function getUSStateCity() {
      return $http.get('us_states_cities.json');
    }
    return service;
  }
})();
