(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badgeCategory')
    .controller('badgeCategoryModalCtrl', badgeCategoryModalCtrl);

  /** @ngInject */
  function badgeCategoryModalCtrl($uibModalInstance, badgeCategory, toastr, badgeCategoryService) {
    var vm = this;
    vm.modalBadgeCategory = angular.copy(badgeCategory) || {};
    vm.createBadgeCategory = createBadgeCategory;

    function createBadgeCategory(isFormValid) {
      if (!isFormValid) {
        return false;
      }

      if (vm.modalBadgeCategory._id) {
        badgeCategoryService.updateBadgeCategory(vm.modalBadgeCategory, vm.modalBadgeCategory._id).then(onSuccess).catch(onError);
      } else {
        badgeCategoryService.createBadgeCategory(vm.modalBadgeCategory).then(onSuccess).catch(onError);
      }

      function onSuccess(response) {
        if (response.data.name) {
          $uibModalInstance.close(response.data);
        }
        toastr.success('Badge category created successfully', 'Badges', {});
      }

      function onError(error) {
        toastr.error(error.data.error, 'Badges', {})
      }
    }
  }
})();
