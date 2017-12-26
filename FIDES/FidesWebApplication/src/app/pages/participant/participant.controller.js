(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.participant')
    .controller('ParticipantController', ParticipantController);

  /** @ngInject */
  function ParticipantController($scope, $filter, $localStorage, toastr, participantService, stateCityService, $uibModal, urls) {
    var vm = this;
    vm.participants = vm.displayedCollection = undefined;
    vm.BASE_API = urls.BASE_API;

    function removeParticipant(orgData) {
      if (orgData.isApproved === true) {
        toastr.error('You can not remove already approved participant.', 'Participant', {});
        return null;
      }

      var deleteParticipant = confirm('Are you sure you want to remove this participant?');
      if (!deleteParticipant) {
        return null;
      }
      participantService.removeParticipant(orgData._id, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          _.remove(vm.participants, {_id: orgData._id});
          vm.displayedCollection = angular.copy(vm.participants);
        }
        toastr.success('Removed successfully', 'Participant', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Participant', {});
      }
    }

    function getAllParticipants() {
      participantService.getAllParticipants(onSuccess, onError);
      function onSuccess(response) {
        if (response.data && response.data.length > 0) {
          vm.participants = response.data;
          vm.displayedCollection = angular.copy(vm.participants);
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'Participants', {})
      }
    }

    function openParticipantModal(participantData, isBtnApprove) {
      if (participantData && participantData.isApproved === true) {
        toastr.error('You can not modify data of already approved participant.', 'Participant', {});
        return null;
      }

      (isBtnApprove) ? participantData.isBtnApprove = true : participantData.isBtnApprove = false;

      vm.participantModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/participant/manageParticipant.modal.html',
        size: isBtnApprove ? 'md' : 'lg',
        controller: ['$uibModalInstance', 'participantData', 'federationService', 'stateCityService', 'participantService', createParticipantController],
        controllerAs: 'vm',
        resolve: {
          participantData: function () {
            return participantData;
          }
        }
      });

      vm.participantModal.result.then(function (newParticipant) {
        var index = _.findIndex(vm.participants, {_id: newParticipant._id});
        if (index >= 0) {
          vm.participants[index] = newParticipant;
        } else {
          if (vm.participants === undefined) {
            vm.participants = vm.displayedCollection = [];
          }

          vm.participants.push(newParticipant);
        }

        vm.displayedCollection = angular.copy(vm.participants);
      });
    }

    // Model Controller
    function createParticipantController($uibModalInstance, participantData, federationService, stateCityService, participantService) {
      var vm = this;
      vm.modalParticipant = {};
      vm.stateCityList = {};
      vm.states = [];
      vm.participants = {};
      vm.federations = null;
      if (participantData) {
        vm.modalParticipant._id = participantData._id;
        vm.modalParticipant.name = participantData.name;
        vm.modalParticipant.phoneNo = participantData.phoneNo;
        vm.modalParticipant.address = participantData.address;
        vm.modalParticipant.zipcode = participantData.zipcode;
        vm.modalParticipant.state = participantData.state;
        vm.modalParticipant.city = participantData.city;
        vm.modalParticipant.type = participantData.type;
        vm.modalParticipant.isApproved = participantData.isApproved;
        vm.modalParticipant.description = participantData.description;
        if (participantData.federationId) {
          vm.modalParticipant.federationiId = participantData.federationId;
        }
        vm.modalParticipant.isBtnApprove = participantData.isBtnApprove;
      }

      function pushParticipant(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        // for approve
        if (vm.modalParticipant.isBtnApprove) {
          // if (vm.modalParticipant.federationId == null) {
          //   toastr.error('Please select federation.', 'Participant', {});
          //   return null;
          // }

          var formData = {
            pid: vm.modalParticipant._id
            //fid: vm.modalParticipant.federationId._id
          };
          participantService.approveParticipant(formData, onSuccess, onError);
          return;
        }
        participantService.updateParticipant(JSON.stringify(vm.modalParticipant), onSuccess, onError);

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Participant', {});

          if (response.data) {
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Participant', {})
        }
      }

      function initLoads() {
        stateCityService.then(function (response) {
          vm.stateCityList = response.data;
          vm.states = Object.keys(response.data);
          vm.cities = vm.stateCityList[vm.modalParticipant.state];
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
        vm.cities = vm.stateCityList[vm.modalParticipant.state];
      }

      vm.pushParticipant = pushParticipant;
      vm.stateChanged = stateChanged;
      initLoads();
    }

    function openApproveBadges(participantData) {
      vm.participantModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/participant/approvedBadge.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'participantData', 'badgesService', badgeDetailCtrl],
        controllerAs: 'vm',
        resolve: {
          participantData: function () {
            return participantData;
          }
        }
      });
    }

    function badgeDetailCtrl($uibModalInstance, participantData, badgesService) {
      var vm = this;
      vm.badges = [];
      vm.participant = participantData;

      badgesService.getBadgeByParticipant(participantData._id, 'approved').then(onSuccess).catch(onError);

      function onSuccess(response) {
        vm.badges = response.data;
      }

      function onError() {
        vm.badges = [];
      }
    }

    //Export the modules for view.
    vm.removeParticipant = removeParticipant;
    vm.getAllParticipants = getAllParticipants;
    vm.openParticipantModal = openParticipantModal;
    vm.openApproveBadges = openApproveBadges;
    // init
    vm.getAllParticipants();
  }
})();
