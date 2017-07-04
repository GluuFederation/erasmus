(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.userBadgeRequest')
    .controller('userBadgeRequestCtrl', userBadgeRequestCtrl);

  /** @ngInject */
  function userBadgeRequestCtrl($timeout, toastr, $uibModal, userBadgeRequestService, $localStorage) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.pendingBadges = [];
    vm.approvedBadges = [];
    vm.displayedPendingCollection = [];
    vm.displayedApprovedCollection = [];
    vm.category = 0;
    vm.participant = (!!$localStorage.currentUser) ? $localStorage.currentUser.user.participant : null;

    vm.activate = activate;
    vm.openBadgeApproveModel = openBadgeApproveModel;
    vm.openBadgeInfoModel = openBadgeInfoModel;
    vm.getBadges = getBadges;
    vm.activate();

    function openBadgeApproveModel(badge) {
      vm.badgeModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/userBadgeRequest/badgeApproved.modal.html',
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
        vm.pendingBadges = vm.pendingBadges.filter(function (item) {
          return item.inum != newBadgeModal.inum;
        });
        vm.displayedPendingCollection = angular.copy(vm.pendingBadges);
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

        // if (vm.validity < 1 || vm.validity > 10) {
        //   toastr.error('Please enter days between 1 to 10', 'Badge', {});
        //   return;
        // }

        var formData = {
          inum: badge.inum,
          validity: 90, // vm.validity,
          privacy: 'Public'
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

    function openBadgeInfoModel(badge) {
      vm.badgeModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/userBadgeRequest/badgeDetail.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'badge', 'userBadgeRequestService', badgeInfoCtrl],
        controllerAs: 'vm',
        resolve: {
          badge: function () {
            return badge;
          }
        }
      });
    }

    function badgeInfoCtrl($uibModalInstance, badge, userBadgeRequestService) {
      var vm = this;
      vm.badge = null;

      function badgeApprove() {
        userBadgeRequestService.badgeInfo(badge.inum).then(onSuccess).catch(onError);

        function onSuccess(response) {
          vm.badge = response.data;
          var recipient = parseJwt(vm.badge.recipient.identity);
          vm.badge.userInfo = JSON.parse(recipient.userinfo);
        }

        function onError(error) {
          toastr.error('Internal server error', 'Badges', {});
          $uibModalInstance.close(null);
        }

        function parseJwt (token) {
          var base64Url = token.split('.')[1];
          var base64 = base64Url.replace('-', '+').replace('_', '/');
          return JSON.parse(window.atob(base64));
        }

      }

      badgeApprove();
    }

    function getBadges() {
      getApprovedBadges();
      $timeout(getPendingBadges, 500);
    }

    function getPendingBadges() {
      userBadgeRequestService.getBadges(vm.participant._id, 'Pending').then(onSuccess).catch(onError);
      function onSuccess(response) {
        vm.pendingBadges = response.data.badgeRequests;
        vm.displayedPendingCollection = angular.copy(vm.pendingBadges);
      }

      function onError(error) {
        vm.pendingBadges = [];
        vm.displayedPendingCollection = [];
      }
    }

    function getApprovedBadges() {
      userBadgeRequestService.getBadges(vm.participant._id, 'Approved').then(onSuccess).catch(onError);
      function onSuccess(response) {
        vm.approvedBadges = response.data.badgeRequests;
        vm.displayedApprovedCollection = angular.copy(vm.pendingBadges);
      }

      function onError(error) {
        vm.pendingBadges = [];
        vm.displayedApprovedCollection = [];
      }
    }

    function activate() {
      vm.getBadges();
    }
  }
})();
