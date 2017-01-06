(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.provider')
    .controller('ProviderController', ProviderController);

  /** @ngInject */
  function ProviderController($scope, $filter, $localStorage, $uibModal, toastr, providerService) {
    var vm = this;
    vm.providers = vm.displayedCollection = undefined;
    vm.userRole = $localStorage.currentUser.role;

    function removeProvider(providerId) {
      var deleteProvider = confirm('Are you sure you want to remove this provider?');
      if (!deleteProvider) {
        return null;
      }

      providerService.removeProvider(providerId, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          _.remove(vm.providers, {_id: response.data._id});
          vm.displayedCollection = angular.copy(vm.providers);
        }
        toastr.success('Removed successfully', 'Provider', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Provider', {});
      }
    }

    function approveProvider(providerId) {
      if(vm.userRole != 'admin'){
        toastr.success('You are not authorized to do this operation', 'Provider', {});
        return null;
      }

      var confirmApproval = confirm('Do you want to approve this provider?');
      if (!confirmApproval) {
        return null;
      }

      providerService.approveProvider(providerId, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          var index = _.findIndex(vm.providers, {_id: response.data._id});
          if (index >= 0) {
            vm.providers[index] = response.data;
          }

          vm.displayedCollection = angular.copy(vm.providers);
        }

        toastr.success('Approved successfully', 'Provider', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Provider', {});
      }
    }

    function verifyProvider(provider) {
      var confirmVerify = confirm('Do you want to verify this provider?');
      if (!confirmVerify) {
        return null;
      }

      providerService.verifyProvider(provider, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          var index = _.findIndex(vm.providers, {_id: response.data._id});
          if (index >= 0) {
            vm.providers[index] = response.data;
          }

          vm.displayedCollection = angular.copy(vm.providers);
        }

        toastr.success('Verified successfully', 'Provider', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Provider', {});
      }
    }

    function getProviders() {
      var userId = undefined;
      if(vm.userRole != 'admin' && $localStorage.currentUser.user){
        userId = $localStorage.currentUser.user._id;
      }

      providerService.getProviders(userId, onSuccess, onError);
      function onSuccess(response) {
        if (response.data) {
          vm.providers = response.data;
          vm.displayedCollection = angular.copy(vm.providers);
        }
      }

      function onError(error) {
        //console.log(JSON.stringify(error));
        toastr.error(error.data.message, 'Provider', {});
      }
    }

    function openProviderModal(providerData) {
      vm.providerModal = $uibModal.open({
        animation: true,
        templateUrl: '/app/pages/provider/createProvider.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'providerData', 'userService', CreateProviderController],
        controllerAs: 'vm',
        resolve: {
          providerData: function () {
            return providerData;
          }
        }
      });

      vm.providerModal.result.then(function (newProvider) {
        var index = _.findIndex(vm.providers, {_id: newProvider._id});
        if (index >= 0) {
          vm.providers[index] = newProvider;
        } else {
          if(vm.providers === undefined) {
            vm.providers = vm.displayedCollection = [];
          }

          vm.providers.push(newProvider);
        }

        vm.displayedCollection = angular.copy(vm.providers);
      });
    }

    //Export the modules for view.
    vm.openProviderModal = openProviderModal;
    vm.removeProvider = removeProvider;
    vm.getProviders = getProviders;
    vm.approveProvider = approveProvider;
    vm.verifyProvider = verifyProvider;

    vm.getProviders();

    //Model Controller
    function CreateProviderController($uibModalInstance, providerData, userService) {
      var vm = this;
      vm.modalProvider = {};
      vm.isInEditMode = false;
      //vm.editPassword = false;
      vm.userRole = $localStorage.currentUser.role;
      vm.roles = {};
      vm.organizations = {};

      getAllOrganizations();

      if (providerData) {
        vm.isInEditMode = true;
        vm.modalProvider._id = providerData._id;
        vm.modalProvider.name = providerData.name;
        vm.modalProvider.url = providerData.url;
        vm.modalProvider.clientId = providerData.clientId;
        vm.modalProvider.clientSecret = providerData.clientSecret;
        vm.modalProvider.responseType = providerData.responseType;
        vm.modalProvider.state = providerData.state;
        vm.modalProvider.redirectUri = providerData.redirectUri;
        vm.modalProvider.grantType = providerData.grantType;
        vm.modalProvider.code = providerData.code;
        vm.modalProvider.scope = providerData.scope;
        vm.modalProvider.username = providerData.username;
        vm.modalProvider.password = providerData.password;
        vm.modalProvider.errorUri = providerData.errorUri;
        if (providerData.organization) {
          vm.modalProvider.organizationId = providerData.organization._id;
        }
      }

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

      function pushProvider(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        if (vm.userRole === 'admin') {
          if (!vm.modalProvider.organizationId) {
            toastr.error("Please select organization.", "Update Provider", {});
            return false;
          }
        } else {
          vm.modalProvider.organizationId = $localStorage.currentUser.user.organization._id;
        }

        if (vm.isInEditMode) {
          providerService.updateProvider(JSON.stringify(vm.modalProvider), onSuccess, onError);
        } else {
          providerService.createProvider(JSON.stringify(vm.modalProvider), onSuccess, onError);
        }

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Provider', {});

          if (response.data) {
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Provider', {})
        }
      }

      vm.pushProvider = pushProvider;
    }
  }
})();
