(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.register')
    .controller('RegisterController', RegisterController);

  /** @ngInject */
  function RegisterController($scope, toastr) {
    var vm = this;

    vm.personInfo = {};
    vm.organizationInfo = {};
    vm.providerInfo = {};

    vm.onIndexChange = function () {
      if(!(vm.personInfo.confirmPassword && vm.personInfo.password === vm.personInfo.confirmPassword)) {
        toastr.error('Passwords should match.', 'Sign Up', {});
        return false;
      }

      return true;
    };

    vm.pushDetail = function () {
      //TODO
      toastr.success('Congratulations! Information submitted successfully', 'Sign Up', {});
      return true;
    };
  }
})();
