(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.encrypt')
    .controller('EncryptController', EncryptController);

  /** @ngInject */
  function EncryptController($http, toastr, encryptService) {
    var vm = this;
    vm.oData = {
        data:"",
        privateKey:""
    };
    vm.output = null;
    vm.encryptData = encryptData;

    function encryptData() {
        // vm.oData.privateKey = vm.oData.privateKey.replace(/(\r\n|\n|\r)/gm,"");
        encryptService.encrypt(vm.oData, onSuccess, onError);

        function onSuccess(response) {
            vm.output = response.data;
        }

        function onError(error) {
            toastr.error(error.data.message, 'FIDES', {})
        }
    }
  }
})();
