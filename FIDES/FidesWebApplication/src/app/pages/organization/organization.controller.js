(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.organization')
    .controller('OrganizationController', OrganizationController);

  /** @ngInject */
  function OrganizationController($scope, $filter, $localStorage, toastr, organizationService) {
    var vm = this;
    vm.organizations = vm.displayedCollection = undefined;

    function validateName(data) {
      if (!data) {
        return "*";
      }
    }

    function removeOrganization(orgData) {
      if(orgData.isApproved === true) {
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

    function pushOrganization(data, orgData) {
      if(orgData.isApproved === true) {
        toastr.error('You can not modify data of already approved organization.', 'Organization', {});
        return null;
      }

      angular.extend(data, {_id: orgData._id});
      organizationService.updateOrganization(data, onSuccess, onError);

      function onSuccess(response) {
        toastr.success('Saved successfully', 'Organization', {});
      }

      function onError(error) {
        orgData.name = name;
        toastr.error(error.data.message, 'Organization', {})
      }
    }

    function approveOrganization(orgData) {
      if(orgData.isApproved === true) {
        toastr.error('Organization is already approved.', 'Organization', {});
        return null;
      }

      var approveConfirm = confirm('Do you want to approve this organization?');
      if (!approveConfirm) {
        return null;
      }

      organizationService.approveOrganization(orgData._id, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          var index = _.findIndex(vm.organizations, {_id: response.data._id});
          if (index >= 0) {
            vm.organizations[index] = response.data;
          }

          vm.displayedCollection = angular.copy(vm.organizations);
        }

        toastr.success('Approved successfully.', 'Organization', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Organization', {});
      }
    }

    //Export the modules for view.
    vm.validateName = validateName;
    vm.pushOrganization = pushOrganization;
    vm.removeOrganization = removeOrganization;
    vm.getAllOrganizations = getAllOrganizations;
    vm.approveOrganization = approveOrganization;

    vm.getAllOrganizations();
  }
})();
