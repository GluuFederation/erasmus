(function () {
    'use strict';

    angular.module('FidesWebApplication.pages.federation')
        .controller('FederationController', FederationController);

    /** @ngInject */
    function FederationController($scope, $filter, $localStorage, toastr, federationService, editableOptions, editableThemes) {
        var vm = this;
        vm.federations = vm.displayedCollection = undefined;
        vm.isInsert = false;

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

        function loadAddForm() {
            if(!vm.federations){
              vm.federations = [];
            }

            vm.inserted = {
                name: '',
                isActive: true
            };
            vm.federations.push(vm.inserted);
            vm.displayedCollection = angular.copy(vm.federations);
            vm.isInsert = true;
        }

        function saveFederation(data, fedData) {
            if (fedData._id == null) {
                federationService.addFederation(data, onSuccess, onError);
            } else {
                angular.extend(data, {_id: fedData._id});
                federationService.updateFederation(data, onSuccess, onError);
            }

            function onSuccess(response) {
                if (fedData._id == null)
                    vm.inserted._id = response.data._id;

                toastr.success('Saved successfully', 'Federation', {});
            }

            function onError(error) {
                vm.federations.pop();
                vm.displayedCollection = angular.copy(vm.federations);
                toastr.error(error.data.message, 'Federation', {})
            }
            vm.isInsert = false;
        }

        function cancelForm(federationForm) {
            if (vm.isInsert) {
                vm.federations.pop();
                vm.displayedCollection = angular.copy(vm.federations);
                vm.isInsert = false;
            }
            federationForm.$cancel();
        }

        /*editableOptions.theme = 'bs3';
        editableThemes['bs3'].submitTpl = '<button type="submit" class="btn btn-primary btn-with-icon"><i class="ion-checkmark-round"></i></button>';
        editableThemes['bs3'].cancelTpl = '<button type="button" ng-click="$form.$cancel()" class="btn btn-default btn-with-icon"><i class="ion-close-round"></i></button>';*/

        //Export the modules for view.
        vm.validateName = validateName;
        vm.removeFederation = removeFederation;
        vm.getAllFederations = getAllFederations;
        vm.saveFederation = saveFederation;
        vm.loadAddForm = loadAddForm;
        vm.cancelForm = cancelForm;

        vm.getAllFederations();
    }
})();
