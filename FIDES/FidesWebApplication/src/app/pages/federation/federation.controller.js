(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.federation')
        .controller('FederationController', FederationController);

    /** @ngInject */
    function FederationController($scope, $filter, $localStorage, toastr, federationService, editableOptions, editableThemes) {
        var vm = this;
        vm.federations = vm.displayedCollection = undefined;

        function validateName(data) {
            if (!data) {
                return "*";
            }
        }

        function removeFederation(fedData) {
            if (fedData.isActive === true) {
                toastr.error('You can not remove already approved federation.', 'Federation', {});
                return null;
            }

            var deleteFederation = confirm('Are you sure you want to remove this federation?');
            if (!deleteFederation) {
                return null;
            }
            federationService.removeFederation(fedData._id, onSuccess, onError);

            function onSuccess(response) {
                if (response.data) {
                    _.remove(vm.federations, {_id: fedData._id});
                    vm.displayedCollection = angular.copy(vm.federations);
                }
                toastr.success('Removed successfully', 'Federation', {});
            }

            function onError(error) {
                toastr.error(error.data.message, 'Federation', {});
            }
        }

        function getAllFederations() {
            federationService.getAllFederations(onSuccess, onError);
            function onSuccess(response) {
                if (response.data && response.data.length > 0) {
                    vm.federations = response.data;
                    vm.displayedCollection = angular.copy(vm.federations);
                }
            }

            function onError(error) {
                toastr.error(error.data.message, 'Federations', {})
            }
        }

        function addFederation(data, fedData) {
            federationService.addFederation(data, onSuccess, onError);

            function onSuccess(response) {
                toastr.success('Saved successfully', 'Federation', {});
            }

            function onError(error) {
                fedData.name = name;
                toastr.error(error.data.message, 'Federation', {})
            }
        }

        function loadAddForm() {
            vm.nFederation = {
                name: '',
                isActive: true
            }
            vm.federations.push(vm.nFederation);
        }

        function updateFederation(data, fedData) {
            if (fedData.isActive === true) {
                toastr.error('You can not modify data of already approved federation.', 'Federation', {});
                return null;
            }

            angular.extend(data, {_id: fedData._id});
            federationService.updateFederation(data, onSuccess, onError);

            function onSuccess(response) {
                toastr.success('Saved successfully', 'Federation', {});
            }

            function onError(error) {
                fedData.name = name;
                toastr.error(error.data.message, 'Federation', {})
            }
        }

        //Export the modules for view.
        vm.validateName = validateName;
        vm.addFederation = addFederation;
        vm.removeFederation = removeFederation;
        vm.getAllFederations = getAllFederations;
        vm.updateFederation = updateFederation;
        vm.loadAddForm = loadAddForm;
        vm.getAllFederations();
    }
})();
