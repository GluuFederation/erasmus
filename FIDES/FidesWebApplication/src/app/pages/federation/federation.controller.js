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
            vm.inserted = {
                name: '',
                isActive: true
            };
            vm.federations.push(vm.inserted);
            vm.displayedCollection = angular.copy(vm.federations);
        }

        function saveFederation(data, fedData) {
            if (fedData._id == null) {
                federationService.addFederation(data, onSuccess, onError);
            } else {
                angular.extend(data, {_id: fedData._id});
                federationService.updateFederation(data, onSuccess, onError);
            }

            function onSuccess(response) {
                toastr.success('Saved successfully', 'Federation', {});
            }

            function onError(error) {
                fedData.name = name;
                toastr.error(error.data.message, 'Federation', {})
            }
        }

        /*editableOptions.theme = 'bs3';
        editableThemes['bs3'].submitTpl = '<button type="submit" class="btn btn-primary btn-with-icon"><i class="ion-checkmark-round"></i></button>';
        editableThemes['bs3'].cancelTpl = '<button type="button" ng-click="$form.$cancel()" class="btn btn-default btn-with-icon"><i class="ion-close-round"></i></button>';*/

        //Export the modules for view.
        vm.validateName = validateName;
        vm.addFederation = addFederation;
        vm.removeFederation = removeFederation;
        vm.getAllFederations = getAllFederations;
        vm.saveFederation = saveFederation;
        vm.loadAddForm = loadAddForm;
        vm.getAllFederations();
    }
})();
