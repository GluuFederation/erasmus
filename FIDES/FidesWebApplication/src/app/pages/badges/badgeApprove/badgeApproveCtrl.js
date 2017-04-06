(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeApprove')
    .controller('BadgeApproveCtrl', BadgeApproveCtrl);

  /** @ngInject */
  function BadgeApproveCtrl($state, toastr, $uibModal, organizationService) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.organizations = [];
    vm.displayedCollection = [];
    vm.category = 0;

    vm.activate = activate;
    vm.openBadgeApproveModel = openBadgeApproveModel;
    vm.getAllPendingOrganization = getAllPendingOrganization;
    vm.activate();

    function openBadgeApproveModel(organization) {
      vm.organizationModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/badgeApprove/badgeApprove.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'organization', 'badgesService', 'badgeRequestService', 'badgeCategoryService', badgeApproveCtrl],
        controllerAs: 'vm',
        resolve: {
          organization: function () {
            return organization;
          }
        }
      });

      vm.organizationModal.result.then(function (newOrganization) {
        var index = _.findIndex(vm.organizations, {_id: newOrganization._id});
        if (index >= 0) {
          vm.organizations[index] = newOrganization;
        } else {
          if (vm.organizations === undefined) {
            vm.organizations = vm.displayedCollection = [];
          }

          vm.organizations.push(newOrganization);
        }

        vm.displayedCollection = angular.copy(vm.organizations);
      });
    }

    function badgeApproveCtrl($uibModalInstance, organization, badgesService, badgeRequestService, badgeCategoryService) {
      var vm = this;
      vm.getPendingBadges = getPendingBadges;
      vm.getCategory = getCategory;
      vm.filterBadge = filterBadge;
      vm.badgeApprove = badgeApprove;
      vm.categories = [];
      vm.organization = organization;
      vm.badges = [];
      vm.safeBadges = [];
      vm.selectedBadges = [];

      function getPendingBadges() {
        badgesService.getBadgeByOrganization(vm.organization._id, 'pending').then(onSuccess).catch(onError);

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
        if (!vm.organization.isApproved) {
          toastr.error('Organization is not approved. Please contact to admin', 'Badge Request', {});
          return;
        }

        if (vm.selectedBadges.length <= 0) {
          toastr.error('Please select at least one badge', 'Badge Request', {});
          return;
        }

        var formData = {
          oid: vm.organization._id,
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

    function getAllPendingOrganization() {
      organizationService.getAllOrganizations().then(onSuccess).catch(onError);
      function onSuccess(response) {
        vm.organizations = response.data.filter(function (item) {
          return (!!item.pendingBadges && item.pendingBadges.length);
        });
        vm.displayedCollection = angular.copy(vm.organizations);
      }

      function onError(error) {
        vm.organization = [];
        vm.displayedCollection = [];
      }
    }

    function activate() {
      vm.getAllPendingOrganization();
    }
  }
})();
