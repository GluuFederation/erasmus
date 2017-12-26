(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .controller('RegisterController', RegisterController);

  /** @ngInject */
  function RegisterController($scope, $window, $location, $localStorage, $uibModal, toastr, urls, registerService, $timeout) {
    var vm = this;

    // data members
    vm.entityInfo = {};
    vm.entityInfo.discoveryUrl = "https://";
    vm.states = [];
    vm.cities = [];
    vm.stateCityList = {};
    vm.federations = [];
    // member functions
    vm.register = register;
    vm.onIndexChange = onIndexChange;
    vm.stateChanged = stateChanged;
    vm.fetchFederation = fetchFederation;

    // definations
    vm.params = $location.search();
    if (vm.params && vm.params.state && $localStorage.entity) {
      if (vm.params.state === $localStorage.entity.client.state) {
        vm.entityInfo = $localStorage.entity.entityInfo;
        if (vm.params.error) {
          toastr.error(vm.params.error_description, 'Sign Up', {});
        } else {
          $localStorage.entity.client.code = vm.params.code;
          registerService.registerDetail($localStorage.entity.entityInfo, $localStorage.entity.client, onSuccess, onError);
        }
      }

      delete $localStorage.entity;
    }

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
      if (error.data && error.data.message) {
        $scope.message = error.data.message;
      }

      $uibModal.open({
        animation: true,
        templateUrl: 'app/theme/template/errorModal.html',
        scope: $scope
      });

      return true;
    }

    function register() {
      if (!vm.entityInfo.discoveryUrl.startsWith("https://")) {
        toastr.error(error.data.message, 'URL must be https', {});
        return;
      }

      vm.entityInfo.memberOf = vm.entityInfo.memberOf.map(function (item) {
        return item._id;
      });

      vm.entityInfo.redirectUrls = [urls.BASE.concat('/login.html'), urls.BASE.concat('/register.html')];
      registerService.validateRegistrationDetail(vm.entityInfo, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          vm.state = response.data.state;
          $localStorage.entity = {client: response.data, entityInfo: vm.entityInfo};
          $window.location = response.data.authEndpoint;
          event.preventDefault();
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'Sign Up', {});
      }

      return false;
    }

    function onIndexChange(lastIndex, index) {
      if (lastIndex === 0) {
        registerService.isUserAlreadyExist(vm.entityInfo, onSuccess, onError);
      } else if (lastIndex === 1) {

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
    }

    function stateChanged() {
      vm.cities = vm.stateCityList[vm.entityInfo.state];
    }

    function fetchFederation() {
      registerService.getAllFederations(onSuccess, onError);
      function onSuccess(response) {
        if (response.data && response.data.length > 0) {
          vm.federations = response.data;
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'Federations', {});
      }
    }

    // init
    registerService.getUSStateCity().then(function (response) {
      vm.stateCityList = response.data;
      vm.states = Object.keys(response.data);
    });

    vm.fetchFederation();
  }
})();
