(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.userBadgeRequest')
    .controller('userBadgeRequestCtrl', userBadgeRequestCtrl);

  /** @ngInject */
  function userBadgeRequestCtrl(toastr, $uibModal, userBadgeRequestService, $localStorage) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.badges = [];
    vm.displayedCollection = [];
    vm.category = 0;
    vm.participant = (!!$localStorage.currentUser) ? $localStorage.currentUser.user.participant : null;

    vm.activate = activate;
    vm.openBadgeApproveModel = openBadgeApproveModel;
    vm.getPendingBadges = getPendingBadges;
    vm.activate();

    function openBadgeApproveModel(badge) {
      vm.badgeModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/userBadgeRequest/userBadgeRequest.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'badge', 'userBadgeRequestService', badgeApproveCtrl],
        controllerAs: 'vm',
        resolve: {
          badge: function () {
            return badge;
          }
        }
      });

      vm.badgeModal.result.then(function (newBadgeModal) {
        if (!newBadgeModal) return;
        vm.badges = vm.badges.filter(function (item) {
          return item.inum != newBadgeModal.inum;
        });
        vm.displayedCollection = angular.copy(vm.badges);
      });
    }

    function badgeApproveCtrl($uibModalInstance, badge, userBadgeRequestService) {
      var vm = this;
      vm.badgeApprove = badgeApprove;
      vm.badge = badge;
      vm.validity = 0;

      function badgeApprove(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        if (vm.validity < 1 || vm.validity > 10) {
          toastr.error('Please enter days between 1 to 10', 'Badge', {});
          return;
        }

        var formData = {
          inum: badge.inum,
          validity: vm.validity,
          privacy: vm.privacy
        };
        userBadgeRequestService.badgeApprove(formData).then(onSuccess).catch(onError);

        function onSuccess(response) {
          toastr.success('Badges approved successfully', 'Badges', {});
          $uibModalInstance.close(badge);
        }

        function onError(error) {
          toastr.error('Internal server error', 'Badges', {});
          $uibModalInstance.close(null);
        }
      }
    }

    function getPendingBadges() {
      userBadgeRequestService.getPendingBadges(vm.participant._id).then(onSuccess).catch(onError);
      function onSuccess(response) {
        vm.badges = response.data.badgeRequests;
        vm.displayedCollection = angular.copy(vm.badges);
      }

      function onError(error) {
        vm.badges = [];
        vm.displayedCollection = [];
      }
    }

    function activate() {
      vm.getPendingBadges();
    }
  }
})();
