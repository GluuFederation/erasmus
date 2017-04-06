(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeRequest')
    .controller('BadgeRequestCtrl', BadgeRequestCtrl);

  /** @ngInject */
  function BadgeRequestCtrl($state, badgesService, badgeRequestService, badgeCategoryService, toastr, $localStorage, $uibModal) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.allBadges = {};
    vm.selectedBadges = [];
    vm.categories = [];
    vm.category = 0;
    vm.organization = (!!$localStorage.currentUser) ? $localStorage.currentUser.user.organization : null;

    vm.getBadges = getBadges;
    vm.badgeRequest = badgeRequest;
    vm.activate = activate;
    vm.getCategory = getCategory;
    vm.filterBadge = filterBadge;
    vm.openModalBadgeDetail = openModalBadgeDetail;
    vm.reqBadge = [];
    vm.activate();

    function getBadges() {
      badgesService.getBadges().then(onSuccess).catch(onError);

      function onSuccess(response) {
        vm.allBadges = response.data;
        badgeRequestService.getOrganizationById(vm.organization._id).then(onSuccess).catch(onError);

        function onSuccess(response) {
          vm.reqBadge = _.union(response.data.pendingBadges, response.data.approvedBadges) || [];
          vm.selectedBadges = angular.copy(vm.reqBadge);
          vm.allBadges = vm.allBadges.map(function (item) {
            (response.data.pendingBadges.indexOf(item._id) > -1) ? item.isPending = true : '';
            (response.data.approvedBadges.indexOf(item._id) > -1) ? item.isApproved = true : '';
            return item;
          });
          vm.allSafeBadges = vm.allBadges;
        }

        function onError() {
          vm.selectedBadges = [];
        }
      }

      function onError() {
        vm.allBadges = [];
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

    function badgeRequest() {
      if (!vm.organization.isApproved) {
        toastr.error('Organization is not approved. Please contact to admin', 'Badge Request', {});
        return;
      }
      if (vm.selectedBadges.length <= 0) {
        toastr.error('Please select at least one badge', 'Badge Request', {});
        return;
      }
      vm.selectedBadges = vm.selectedBadges.filter(function (item) {
        return vm.reqBadge.indexOf(item) <= -1;
      });

      var formData = {
        oid: vm.organization._id,
        bids: vm.selectedBadges
      };
      badgeRequestService.badgeRequest(formData).then(onSuccess).catch(onError);

      function onSuccess(response) {
        toastr.success('Badges request successfully', 'Badges', {});
        $state.go('badges.badges');
      }

      function onError(error) {
        toastr.error('Internal server error', 'Badges', {})
      }
    }

    function filterBadge() {
      if (!vm.category) {
        vm.allBadges = vm.allSafeBadges;
        return;
      }

      vm.allBadges = vm.allSafeBadges.filter(function (item) {
        return item.category._id === vm.category;
      });
    }

    function openModalBadgeDetail(badge) {
      vm.organizationModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/badgeRequest/badgeDetail.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'badge', badgeDetailCtrl],
        controllerAs: 'vm',
        resolve: {
          badge: function () {
            return badge;
          }
        }
      });
    }

    function badgeDetailCtrl($uibModalInstance, badge) {
      var vm = this;
      vm.badge = badge;
    }

    function activate() {
      vm.getBadges();
      vm.getCategory();
    }
  }
})();
