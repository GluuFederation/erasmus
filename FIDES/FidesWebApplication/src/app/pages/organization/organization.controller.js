(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.organization')
    .controller('OrganizationController', OrganizationController);

  /** @ngInject */
  function OrganizationController($scope, $filter, $localStorage, toastr, organizationService, stateCityService, $uibModal, urls) {
    var vm = this;
    vm.organizations = vm.displayedCollection = undefined;
    vm.BASE_API = urls.BASE_API;

    function removeOrganization(orgData) {
      if (orgData.isApproved === true) {
        toastr.error('You can not remove already approved organization.', 'Organization', {});
        return null;
      }

      var deleteOrganization = confirm('Are you sure you want to remove this organization?');
      if (!deleteOrganization) {
        return null;
      }
      organizationService.removeOrganization(orgData._id, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          _.remove(vm.organizations, {_id: orgData._id});
          vm.displayedCollection = angular.copy(vm.organizations);
        }
        toastr.success('Removed successfully', 'Organization', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Organization', {});
      }
    }

    function getAllOrganizations() {
      organizationService.getAllOrganizations(onSuccess, onError);
      function onSuccess(response) {
        if (response.data && response.data.length > 0) {
          vm.organizations = response.data;
          vm.displayedCollection = angular.copy(vm.organizations);
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'Organizations', {})
      }
    }

    function openOranizationModal(organizationData, isBtnApprove) {
      if (organizationData && organizationData.isApproved === true) {
        toastr.error('You can not modify data of already approved organization.', 'Organization', {});
        return null;
      }

      (isBtnApprove) ? organizationData.isBtnApprove = true : organizationData.isBtnApprove = false;

      vm.organizationModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/organization/manageOrganization.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'organizationData', 'federationService', 'stateCityService', 'organizationService', createOrganizationController],
        controllerAs: 'vm',
        resolve: {
          organizationData: function () {
            return organizationData;
          }
        }
      });

      vm.organizationModal.result.then(function (newOrganization) {
        var index = _.findIndex(vm.organizations, {_id: newOrganization._id});
        if (index >= 0) {
          vm.organizations[index] = newOrganization;
        } else {
          if (vm.organizations === undefined) {
            vm.organizations = vm.displayedCollection = [];
          }

          vm.organizations.push(newOrganization);
        }

        vm.displayedCollection = angular.copy(vm.organizations);
      });
    }

    // Model Controller
    function createOrganizationController($uibModalInstance, organizationData, federationService, stateCityService, organizationService) {
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
        if (organizationData.federationId) {
          vm.modalOrganization.federationiId = organizationData.federationId;
        }
        vm.modalOrganization.isBtnApprove = organizationData.isBtnApprove;
      }

      function pushOrganization(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        // for approve
        if (vm.modalOrganization.isBtnApprove) {
          if (vm.modalOrganization.federationId == null) {
            toastr.error('Please select federation.', 'Organization', {});
            return null;
          }

          var formData = {
            organizationId: vm.modalOrganization._id,
            federationId: vm.modalOrganization.federationId._id,
            federationOttoId: vm.modalOrganization.federationId.ottoId
          };
          organizationService.approveOrganization(formData, onSuccess, onError);
          return;
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

        federationService.getAllFederations(onSuccess, onError);
        function onSuccess(response) {
          if (response.data && response.data.length > 0) {
            vm.federations = response.data;
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Federations', {});
        }
      }

      function stateChanged() {
        vm.cities = vm.stateCityList[vm.modalOrganization.state];
      }

      vm.pushOrganization = pushOrganization;
      vm.stateChanged = stateChanged;
      initLoads();
    }

    function openApproveBadges(organizationData) {
      vm.organizationModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/organization/approvedBadge.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'organizationData', 'badgesService', badgeDetailCtrl],
        controllerAs: 'vm',
        resolve: {
          organizationData: function () {
            return organizationData;
          }
        }
      });
    }

    function badgeDetailCtrl($uibModalInstance, organizationData, badgesService) {
      var vm = this;
      vm.badges = [];
      vm.organization = organizationData;

      badgesService.getBadgeByOrganization(organizationData._id, 'approved').then(onSuccess).catch(onError);

      function onSuccess(response) {
        vm.badges = response.data;
      }

      function onError() {
        vm.badges = [];
      }
    }

    //Export the modules for view.
    vm.removeOrganization = removeOrganization;
    vm.getAllOrganizations = getAllOrganizations;
    vm.openOranizationModal = openOranizationModal;
    vm.openApproveBadges = openApproveBadges;
    // init
    vm.getAllOrganizations();
  }
})();
