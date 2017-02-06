(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.organization')
        .controller('OrganizationController', OrganizationController);

    /** @ngInject */
    function OrganizationController($scope, $filter, $localStorage, toastr, organizationService, federationService, $timeout) {
        var vm = this;
        vm.organizations = vm.displayedCollection = undefined;
        vm.isApproveFormLoad = false;
        vm.federations = null;

        function validateName(data) {
            if (!data) {
                return "*";
            }
        }

        function validateFederation(data) {
            if (!data) {
                return "*";
            }
        }

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

        function pushOrganization(data, orgData) {
            if (orgData.isApproved === true) {
                toastr.error('You can not modify data of already approved organization.', 'Organization', {});
                return null;
            }
            // for approve
            if (vm.isApproveFormLoad) {
                if (data.federationId == null) {
                    toastr.error('Please select federation.', 'Organization', {});
                    return null;
                }

                var formData = {
                    organizationId: orgData._id,
                    federationId: data.federationId._id,
                    federationOttoId : data.federationId.ottoId
                };
                vm.approveOrganization(formData);
                vm.isApproveFormLoad = false;
                return;
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

        function approveOrganization(formData) {
            organizationService.approveOrganization(formData, onSuccess, onError);

            function onSuccess(response) {
                if (response.data) {
                    var index = _.findIndex(vm.organizations, {_id: response.data._id});
                    if (index >= 0) {
                        vm.organizations[index] = response.data;
                    }
                    var obj = _.find(vm.federations, { _id: formData.federationId});
                    vm.organizations[index].federationId = obj;
                    vm.displayedCollection = angular.copy(vm.organizations);
                }

                toastr.success('Approved successfully.', 'Organization', {});
            }

            function onError(error) {
                toastr.error(error.data.message, 'Organization', {});
            }
        }

        function loadFormApprove(organizationForm) {
            vm.isApproveFormLoad = true;
            organizationForm.$show();
        }

        function getAllFederations() {
            federationService.getAllFederations(onSuccess, onError);
            function onSuccess(response) {
                if (response.data && response.data.length > 0) {
                    vm.federations = response.data;
                }
            }

            function onError(error) {
                toastr.error(error.data.message, 'Federations', {})
            }
        }

        function cancelForm(organizationForm) {
            vm.isApproveFormLoad = false;
            organizationForm.$cancel();
        }

        //Export the modules for view.
        vm.validateName = validateName;
        vm.pushOrganization = pushOrganization;
        vm.removeOrganization = removeOrganization;
        vm.getAllOrganizations = getAllOrganizations;
        vm.approveOrganization = approveOrganization;
        vm.validateFederation = validateFederation;
        vm.loadFormApprove = loadFormApprove;
        vm.getAllFederations = getAllFederations;
        vm.cancelForm = cancelForm;

        vm.getAllOrganizations();
        vm.getAllFederations();
    }
})();
