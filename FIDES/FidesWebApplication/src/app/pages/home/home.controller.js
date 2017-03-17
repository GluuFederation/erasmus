(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.home')
    .controller('HomeController', HomeController);

  /** @ngInject */
  function HomeController($http, toastr, urls, $localStorage, $uibModal) {
    var vm = this;
    vm.organization = $localStorage.currentUser.user.organization;
    vm.isShow = $localStorage.currentUser.role === 'orgadmin';

    vm.openOranizationModal = openOranizationModal;

    $http.get(urls.BASE_API + "/loggedIn").then(onSuccess).catch(onError);

    function onSuccess(response) {
      //console.log(response.data);
    }

    function onError(error) {
      //console.log(JSON.stringify(error));
      toastr.error(error.data.message, 'FIDES', {})
    }

    // organization edit model
    function openOranizationModal() {
      vm.organizationModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/organization/manageOrganization.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'organizationData', 'stateCityService', 'organizationService', createOrganizationController],
        controllerAs: 'vm',
        resolve: {
          organizationData: function () {
            return vm.organization;
          }
        }
      });

      vm.organizationModal.result.then(function (newOrganization) {
        $localStorage.currentUser.user.organization = newOrganization;
        vm.organization = newOrganization;
      });
    }

    function createOrganizationController($uibModalInstance, organizationData, stateCityService, organizationService) {
      var vm = this;
      vm.modalOrganization = {};
      vm.stateCityList = {};
      vm.states = [];
      vm.organizations = {};
      vm.federations = null;
      if (organizationData) {
        vm.modalOrganization._id = organizationData._id;
        vm.modalOrganization.name = organizationData.name;
        vm.modalOrganization.phoneNo = organizationData.phoneNo;
        vm.modalOrganization.address = organizationData.address;
        vm.modalOrganization.zipcode = organizationData.zipcode;
        vm.modalOrganization.state = organizationData.state;
        vm.modalOrganization.city = organizationData.city;
        vm.modalOrganization.type = organizationData.type;
        vm.modalOrganization.isApproved = organizationData.isApproved;
        vm.modalOrganization.description = organizationData.description;
      }

      function pushOrganization(isFormValid) {
        if (!isFormValid) {
          return false;
        }
        organizationService.updateOrganization(JSON.stringify(vm.modalOrganization), onSuccess, onError);

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Organization', {});

          if (response.data) {
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Organization', {})
        }
      }

      function initLoads() {
        stateCityService.then(function (response) {
          vm.stateCityList = response.data;
          vm.states = Object.keys(response.data);
          vm.cities = vm.stateCityList[vm.modalOrganization.state];
        });
      }

      function stateChanged() {
        vm.cities = vm.stateCityList[vm.modalOrganization.state];
      }

      vm.pushOrganization = pushOrganization;
      vm.stateChanged = stateChanged;
      initLoads();
    }
  }
})();
