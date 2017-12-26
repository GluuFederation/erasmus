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
      getUSStateCity: getUSStateCity,
      getAllFederations: getAllFederations
    };

    function validateRegistrationDetail(entityInfo, onSuccess, onError) {
      return $http.post(urls.FIDES_BASE_API + "/validateRegistrationDetail", entityInfo).then(onSuccess).catch(onError);
    }

    function registerDetail(entityInfo, clientInfo, onSuccess, onError) {
      return $http.post(urls.FIDES_BASE_API + "/registerDetail", {
        entityInfo: entityInfo,
        clientInfo: clientInfo
      }).then(onSuccess).catch(onError);
    }

    function isUserAlreadyExist(personInfo, onSuccess, onError) {
      return $http.get(urls.FIDES_BASE_API + "/isUserAlreadyExist/" + personInfo.email).then(onSuccess).catch(onError);
    }

    function getUSStateCity() {
      return $http.get(urls.BASE + '/us_states_cities.json');
    }

    function getAllFederations(onSuccess, onError) {
      return $http.get(urls.FIDES_BASE_API + "/federations").then(onSuccess).catch(onError);
    }

    return service;
  }
})();
