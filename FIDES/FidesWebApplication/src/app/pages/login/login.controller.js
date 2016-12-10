(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login')
    .controller('LoginController', LoginController);

  /** @ngInject */
  function LoginController($http, $localStorage, $window, loginService, toastr, urls) {

    toastr.options = {
      "closeButton": true,
      "debug": false,
      "newestOnTop": true,
      "progressBar": false,
      "positionClass": "toast-top-full-width",
      "preventDuplicates": true,
      "onclick": null,
      "showDuration": "50000",
      "hideDuration": "1000",
      "timeOut": "50000",
      "extendedTimeOut": "1000",
      "showEasing": "swing",
      "hideEasing": "linear",
      "showMethod": "fadeIn",
      "hideMethod": "fadeOut"
    };

    var vm = this;
    vm.login = login;
    vm.logout = logout;

    function login(isFormValid) {
      if(!isFormValid) {
        return;
      }

      loginService.login(vm.username, vm.password, onSuccess, onError);

      function onSuccess(response) {
        // login successful if there's a token in the response
        if (response.token) {
          // store username and token in local storage to keep user logged in between page refreshes
          $localStorage.currentUser = {username: vm.username, token: response.token};

          $window.location = urls.BASE;
        } else {
          // execute callback with false to indicate failed login
          toastr.error("Login failed");
        }
      }

      function onError(error) {
        //console.log(error);
        toastr.error(error.data.message, 'FIDES');
      }
    }

    function logout() {
      // remove user from local storage and clear http auth header
      delete $localStorage.currentUser;
      $http.defaults.headers.common.Authorization = '';
      $window.location = urls.AUTH_URL;
    }
  }
})();
