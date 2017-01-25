(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .controller('RegisterController', RegisterController);

  /** @ngInject */
  function RegisterController($scope, $window, $location, $localStorage, $uibModal, toastr, urls, registerService) {
    var vm = this;
    vm.providerInfo = {};
    vm.params = $location.search();

    if(vm.params && vm.params.state && $localStorage.provider) {
      if(vm.params.state === $localStorage.provider.client.state) {
        vm.providerInfo = $localStorage.provider.providerInfo;
        if (vm.params.error) {
          toastr.error(vm.params.error_description, 'Sign Up', {});
        } else {
          $localStorage.provider.client.code = vm.params.code;
          registerService.registerDetail($localStorage.provider.providerInfo, $localStorage.provider.client, onSuccess, onError);
        }
      }

      delete $localStorage.provider;
    }

    vm.register = function (isFormValid) {
      if(!isFormValid) {
        return false;
      }

      vm.providerInfo.redirectUrl = urls.BASE.concat('/register.html');
      registerService.validateRegistrationDetail(vm.providerInfo, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          vm.state = response.data.state;
          $localStorage.provider = {client: response.data, providerInfo: vm.providerInfo};
          $window.location = response.data.authEndpoint;
          event.preventDefault();
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'Sign Up', {});
      }

      return false;
    };

    vm.pushDetail = function () {
      /*var progressModal = $uibModal.open({
        animation: true,
        backdrop: false,
        keyboard: false,
        scope: $scope,
        size: 'sm',
        templateUrl: 'app/theme/template/progressModal.html'
      });*/

      registerService.registerDetail(vm.providerInfo, vm.personInfo, onSuccess, onError);

      function onSuccess(response) {
        //progressModal.close();

        $scope.message = "Your information has been saved successfully!";
        $scope.buttonCaption = "Sign In now!";

        $uibModal.open({
          animation: true,
          templateUrl: 'app/theme/template/successModal.html',
          scope: $scope
        }).result.then(function (result) {
          $window.location = urls.AUTH_URL;
        }).catch(function (reason) {
          $window.location = urls.AUTH_URL;
        });

        return true;
      }

      function onError(error) {
        //progressModal.close();

        $scope.message = "The server encountered an internal error and was unable to complete your request. Please contact administrator.";
        if(error.data && error.data.message) {
          $scope.message = error.data.message;
        }

        $uibModal.open({
          animation: true,
          templateUrl: 'app/theme/template/errorModal.html',
          scope: $scope
        });

        return true;
      }
    };

    function onSuccess(response) {
      $scope.message = "Your information has been saved successfully!";
      $scope.buttonCaption = "Sign In now!";

      $uibModal.open({
        animation: true,
        templateUrl: 'app/theme/template/successModal.html',
        scope: $scope
      }).result.then(function (result) {
        $window.location = urls.AUTH_URL;
      }).catch(function (reason) {
        $window.location = urls.AUTH_URL;
      });

      return true;
    }

    function onError(error) {
      //progressModal.close();

      $scope.message = "The server encountered an internal error and was unable to complete your request. Please contact administrator.";
      if(error.data && error.data.message) {
        $scope.message = error.data.message;
      }

      $uibModal.open({
        animation: true,
        templateUrl: 'app/theme/template/errorModal.html',
        scope: $scope
      });

      return true;
    }
  }
})();
