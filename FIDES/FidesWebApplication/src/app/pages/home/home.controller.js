(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.home')
    .controller('HomeController', HomeController);

  /** @ngInject */
  function HomeController($http, toastr, urls) {
    //var vm = this;
    $http.get(urls.BASE_API + "/loggedIn").then(onSuccess).catch(onError);

    function onSuccess(response) {
      //console.log(response.data);
    }

    function onError(error) {
      //console.log(JSON.stringify(error));
      toastr.error(error.data.message, 'FIDES', {})
    }
  }

})();
