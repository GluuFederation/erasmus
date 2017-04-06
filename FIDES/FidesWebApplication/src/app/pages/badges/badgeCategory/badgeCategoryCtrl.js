(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeCategory')
    .controller('badgeCategoryCtrl', badgeCategoryCtrl);

  /** @ngInject */
  function badgeCategoryCtrl($uibModal, badgeCategoryService, toastr) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.getBadgeCategory = getBadgeCategory;
    vm.removeBadgeCategory = removeBadgeCategory;
    vm.updateBadgeCategory = updateBadgeCategory;
    vm.openBadgeCategoryModal = openBadgeCategoryModal;
    vm.activate = activate;

    vm.activate();

    function getBadgeCategory() {
      badgeCategoryService.getAllBadgeCategory().then(onSuccess).catch(onError);

      function onSuccess(response) {
        vm.badgeCategories = response.data && response.data;
        vm.safeBadgeCategories = angular.copy(vm.badgeCategories);
      }

      function onError() {
        vm.badgeCategories = [];
        vm.safeBadgeCategories = [];
      }
    }

    function removeBadgeCategory(id) {
      if (!confirm('Are you sure you want to remove this category?')) {
        return null;
      }

      badgeCategoryService.removeBadgeCategory(id).then(onSuccess).catch(onError);

      function onSuccess(response) {
        if (_.remove(vm.safeBadgeCategories, {_id: id})) {
          vm.badgeCategories = angular.copy(vm.safeBadgeCategories);
        }
        toastr.success(response.data.success, 'BadgeCategory', {});
      }

      function onError(error) {
        toastr.error(error.data.error, 'BadgeCategory', {})
      }
    }

    function updateBadgeCategory(id, badgeCategory) {
      badgeCategoryService.updateBadgeCategory(id, badgeCategory).then(onSuccess).catch(onError);

      function onSuccess(response) {
        toastr.success(response.data.success, 'BadgeCategory', {});
      }

      function onError(error) {
        var badgeIndex = _.findIndex(vm.badgeCategories, {_id: id});
        if (badgeIndex !== -1) {
          vm.badgeCategories[badgeIndex] = vm.oldBadge;
          vm.safeBadgeCategories[badgeIndex] = vm.oldBadge;
        }
        toastr.error(error.data.error, 'BadgeCategory', {})
      }
    }

    function openBadgeCategoryModal(badgeCategory) {
      $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/badgeCategory/createBadgeCategory.modal.html',
        size: 'lg',
        controller: 'badgeCategoryModalCtrl',
        controllerAs: 'vm',
        resolve: {
          badgeCategory: function () {
            return badgeCategory;
          }
        }
      }).result.then(function (badge) {
        var index = _.findIndex(vm.badgeCategories, {_id: badge._id});
        if (index >= 0) {
          vm.badgeCategories[index] = badge;
        } else {
          if (vm.badgeCategories === undefined) {
            vm.badgeCategories = vm.displayedCollection = [];
          }
          vm.badgeCategories.push(badge);
        }
        vm.displayedCollection = angular.copy(vm.organizations);
      });
    }

    function activate() {
      vm.getBadgeCategory();
    }
  }

})();
