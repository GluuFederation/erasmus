(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .controller('RegisterController', RegisterController);

  /** @ngInject */
  function RegisterController($scope, $window, $timeout, $uibModal, toastr, urls, registerService, userService) {
    var vm = this;

    vm.personInfo = {};
    vm.organizationInfo = {};
    vm.providerInfo = {};
    vm.organizations = {};

    getAllOrganizations();

    function getAllOrganizations() {
      userService.getAllOrganizations(onSuccess, onError);
      function onSuccess(response) {
        if (response.data && response.data.length > 0) {
          vm.organizations = response.data;
        }
      }

      function onError(error) {
        console.log(JSON.stringify(error));
        toastr.error(error.data.message, 'Organizations', {})
      }
    }

    vm.onIndexChange = function (lastIndex, index) {
      if (!(vm.personInfo.confirmPassword && vm.personInfo.password === vm.personInfo.confirmPassword)) {
        toastr.error('Passwords should match.', 'Sign Up', {});
        return false;
      }

      if (lastIndex === 0) {
        registerService.isUserAlreadyExist(vm.personInfo, onSuccess, onError);
      } else if (lastIndex === 1) {
        if(vm.organizationInfo.organizationName){
          var ind = _.findIndex(vm.organizations, function(o) { return o.name.toLowerCase() == vm.organizationInfo.organizationName.toLowerCase(); });
          if (ind >= 0) {
            toastr.error('Organization already exists.', 'Sign Up', {});
            return false;
          }
        }
      }

      function onSuccess(response) {
        if (response.data && response.data.isExists === true) {
          toastr.error('Username or email already exists.', 'Sign Up', {});
          $timeout(function () {
            angular.element('#btnPrevious').triggerHandler('click');
          }, 0);
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'Sign Up', {});
        $timeout(function () {
          angular.element('#btnPrevious').triggerHandler('click');
        }, 0);
      }

      return true;
    };

    vm.pushDetail = function () {
      registerService.registerDetail(vm.personInfo, vm.organizationInfo, vm.providerInfo, onSuccess, onError);

      function onSuccess(response) {
        $scope.message = "Your information has been saved successfully!";
        $scope.buttonCaption = "Sign In now!";

        $uibModal.open({
          animation: true,
          templateUrl: '/app/theme/template/successModal.html',
          scope: $scope
        }).result.then(function (result) {
          $window.location = urls.AUTH_URL;
        }).catch(function (reason) {
          $window.location = urls.AUTH_URL;
        });
      }

      function onError(error) {
        $scope.message = "The server encountered an internal error and was unable to complete your request. Please contact administrator.";
        if(error.data && error.data.message) {
          $scope.message = error.data.message;
        }

        $uibModal.open({
          animation: true,
          templateUrl: '/app/theme/template/errorModal.html',
          scope: $scope
        });
      }

      return true;
    };
  }
})();
