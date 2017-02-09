(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.encrypt')
    .controller('EncryptController', EncryptController);

  /** @ngInject */
  function EncryptController($http, toastr, encryptService) {
    var vm = this;
    vm.oData = null;
    vm.output = null;
    vm.encryptData = encryptData;

    function encryptData() {
        // vm.oData.privateKey = vm.oData.privateKey.replace(/(\r\n|\n|\r)/gm,"");
        var strArr = vm.oData.privateKey.split(" ");
        var str = "";
        for (var i=0;i<strArr.length;i++) {
                if (i == 3 || i == strArr.length - 5)
                    str += strArr[i] + '\n';
                else
                    str += strArr[i] + ' ';
        }

        vm.oData.privateKey = str;
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
