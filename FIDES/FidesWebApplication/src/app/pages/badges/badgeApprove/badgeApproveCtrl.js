(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeApprove')
    .controller('BadgeApproveCtrl', BadgeApproveCtrl);

  /** @ngInject */
  function BadgeApproveCtrl($state, toastr, $uibModal, participantService) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.participants = [];
    vm.displayedCollection = [];
    vm.category = 0;

    vm.activate = activate;
    vm.openBadgeApproveModel = openBadgeApproveModel;
    vm.getAllPendingParticipant = getAllPendingParticipant;
    vm.activate();

    function openBadgeApproveModel(participant) {
      vm.participantModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/badgeApprove/badgeApprove.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'participant', 'badgesService', 'badgeRequestService', 'badgeCategoryService', badgeApproveCtrl],
        controllerAs: 'vm',
        resolve: {
          participant: function () {
            return participant;
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

    function badgeApproveCtrl($uibModalInstance, participant, badgesService, badgeRequestService, badgeCategoryService) {
      var vm = this;
      vm.getPendingBadges = getPendingBadges;
      vm.getCategory = getCategory;
      vm.filterBadge = filterBadge;
      vm.badgeApprove = badgeApprove;
      vm.categories = [];
      vm.participant = participant;
      vm.badges = [];
      vm.safeBadges = [];
      vm.selectedBadges = [];

      function getPendingBadges() {
        badgesService.getBadgeByParticipant(vm.participant._id, 'pending').then(onSuccess).catch(onError);

        function onSuccess(response) {
          vm.badges = response.data;
          vm.safeBadges = response.data;
        }

        function onError() {
          vm.badges = [];
          vm.safeBadges = [];
        }
      }

      function getCategory() {
        badgeCategoryService.getAllBadgeCategory().then(onSuccess).catch(onError);

        function onSuccess(response) {
          vm.categories = response.data;
        }

        function onError(error) {
          vm.categories = [];
        }
      }

      function filterBadge() {
        if (!vm.category) {
          vm.badges = vm.safeBadges;
          return;
        }

        vm.badges = vm.safeBadges.filter(function (item) {
          return item.category._id === vm.category;
        });
      }

      function badgeApprove() {
        if (!vm.participant.isApproved) {
          toastr.error('Participant is not approved. Please contact to admin', 'Badge Request', {});
          return;
        }

        if (vm.selectedBadges.length <= 0) {
          toastr.error('Please select at least one badge', 'Badge Request', {});
          return;
        }

        var formData = {
          oid: vm.participant._id,
          bids: vm.selectedBadges
        };
        badgeRequestService.badgeApprove(formData).then(onSuccess).catch(onError);

        function onSuccess(response) {
          toastr.success('Badges approved successfully', 'Badges', {});
          $uibModalInstance.close(response.data);
        }

        function onError(error) {
          toastr.error('Internal server error', 'Badges', {})
        }
      }

      //init
      vm.getPendingBadges();
      vm.getCategory();
    }

    function getAllPendingParticipant() {
      participantService.getAllParticipants().then(onSuccess).catch(onError);
      function onSuccess(response) {
        vm.participants = response.data.filter(function (item) {
          return (!!item.pendingBadges && item.pendingBadges.length);
        });
        vm.displayedCollection = angular.copy(vm.participants);
      }

      function onError(error) {
        vm.participant = [];
        vm.displayedCollection = [];
      }
    }

    function activate() {
      vm.getAllPendingParticipant();
    }
  }
})();
