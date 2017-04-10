(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeApprove')
    .controller('BadgeApproveCtrl', BadgeApproveCtrl);

  /** @ngInject */
  function BadgeApproveCtrl($state, toastr, $uibModal, organizationService) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.organizations = [{"provider":"https://ce-dev2.gluu.org","_id":"58e5ed3a96d2046408a7359d","@context":"https://raw.githubusercontent.com/KantaraInitiative/wg-otto/master/schema/otto/organization.jsonld","@id":"http://localhost:5053,/otto/organization/58e5ed3a96d2046408a7359d","updatedAt":"2017-04-06T12:39:41.186Z","createdAt":"2017-04-06T07:24:42.213Z","name":"Local Org","phoneNo":"22221333366","address":"c123","zipcode":"21415","state":"Arizona","city":"Cochise","type":"service","description":"service","__v":2,"federation":{"_id":"58db728c6e98661975a9fae0","@context":"https://raw.githubusercontent.com/KantaraInitiative/wg-otto/master/schema/otto/federation.jsonld","@id":"http://localhost:5053,/otto/federations/58db728c6e98661975a9fae0","updatedAt":"2017-04-06T07:28:17.869Z","createdAt":"2017-03-29T08:38:36.602Z","name":"fed1","__v":5,"organization":"58d8b139987e8419d0a3c1cc","participants":["58db5e5d07f1b22d72deb271","58db5f1d07f1b22d72deb274","58e33b29d72d0f9e1e544e07","58e38ee573c84fe04e123b54","58e5ed3a96d2046408a7359d"],"entities":[],"keys":[],"isActive":true},"approvedBadges":[],"pendingBadges":["58dfa0d7a016c8832d9b7eab","58e236537e3a8d1121233123"],"federations":[],"entities":[],"isActive":false,"isApproved":true}];
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
        return;
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
        toastr.success('Badges approved successfully', 'Badges', {});
        $uibModalInstance.close({});
        return;
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
      //vm.getAllPendingOrganization();
    }
  }
})();
