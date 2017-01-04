(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .factory('registerService', registerService);

  /** @ngInject */
  function registerService($http, urls) {

    var service = {
      isUserAlreadyExist: isUserAlreadyExist,
      registerDetail: registerDetail
    };

    function isUserAlreadyExist(personInfo, onSuccess, onError) {
      return $http.get(urls.BASE_API + "/isUserAlreadyExist", {
        params: {username: personInfo.username, email: personInfo.email}
      }).then(onSuccess).catch(onError);
    }

    function registerDetail(personInfo, organizationInfo, providerInfo, onSuccess, onError) {
      return $http.post(urls.BASE_API + "/registerDetail", {
        personInfo: personInfo,
        organizationInfo: organizationInfo,
        providerInfo: providerInfo
      }).then(onSuccess).catch(onError);
    }

    return service;
  }
})();
