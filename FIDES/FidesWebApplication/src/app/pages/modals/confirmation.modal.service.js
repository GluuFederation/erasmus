(function () {
    'use strict';

    angular.module('FidesWebApplication.pages')
        .factory('confirmationModalService', confirmationModalService);

    /** @ngInject */
    function confirmationModalService($uibModal, $rootScope) {
        var service = {
            confirmationModal: confirmationModal,
        };

        function confirmationModal(options){
            var scope = $rootScope.$new();
            scope.title = options.title;
            scope.content = options.content;
            scope.approveButton = options.buttonTitle || "Approve";
            scope.buttonType = options.buttonType;
            return $uibModal.open({
                templateUrl: "app/pages/modals/confirmation.modal.html",
                scope: scope
            }).result;
        }

        return service;
    }

})();
