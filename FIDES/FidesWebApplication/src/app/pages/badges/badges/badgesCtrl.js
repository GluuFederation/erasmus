(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badges')
    .controller('BadgesCtrl', BadgesCtrl);

  /** @ngInject */
  function BadgesCtrl($uibModal, badgesService, badgeCategoryService, toastr, $localStorage) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.oldBadge = {};
    vm.categories = [];
    vm.isShow = (!!$localStorage.currentUser) ? $localStorage.currentUser.role === 'admin' : false;

    vm.getBadges = getBadges;
    vm.removeBadge = removeBadge;
    vm.updateBadge = updateBadge;
    vm.openBadgeModal = openBadgeModal;
    vm.getCategory = getCategory;
    vm.activate = activate;
    vm.participant = (!!$localStorage.currentUser) ? $localStorage.currentUser.user.participant : null;

    vm.activate();

    function getBadges() {
      if (vm.isShow) {
        badgesService.getBadges().then(onSuccess).catch(onError);
      } else {
        badgesService.getBadgeByParticipant(vm.participant._id, 'all').then(onSuccess).catch(onError);
      }

      function onSuccess(response) {
        vm.badges = response.data;
        vm.safeBadges = angular.copy(vm.badges);
      }

      function onError() {
        vm.badges = [];
        vm.safeBadges = [];
      }
    }

    function removeBadge(id) {
      if (!confirm('Are you sure you want to remove this badge?')) {
        return null;
      }

      badgesService.removeBadge(id).then(onSuccess).catch(onError);
      function onSuccess(response) {
        if (_.remove(vm.safeBadges, {_id: id})) {
          vm.badges = angular.copy(vm.safeBadges);
        }
        toastr.success(response.data.success, 'Badges', {});
      }

      function onError(error) {
        toastr.error(error.data.error, 'Badges', {})
      }
    }

    function updateBadge(id, badge) {
      badgesService.updateBadge(id, badge).then(onSuccess).catch(onError);

      function onSuccess(response) {
        toastr.success(response.data.success, 'Badges', {});
      }

      function onError(error) {
        var badgeIndex = _.findIndex(vm.badges, {_id: id});
        if (badgeIndex !== -1) {
          vm.badges[badgeIndex] = vm.oldBadge;
          vm.safeBadges[badgeIndex] = vm.oldBadge;
        }
        toastr.error(error.data.error, 'Badges', {})
      }
    }

    function openBadgeModal(badge) {
      $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/badges/badges/createBadge.modal.html',
        size: 'lg',
        controller: 'BadgeModalCtrl',
        controllerAs: 'vm',
        resolve: {
          badge: function () {
            return badge;
          }
        }
      }).result.then(function (badge) {
        var index = _.findIndex(vm.badges, {_id: badge._id});
        if (index >= 0) {
          vm.badges[index] = badge;
        } else {
          if (vm.badges === undefined) {
            vm.badges = vm.safeBadges = [];
          }
          vm.badges.push(badge);
        }
        vm.safeBadges = angular.copy(vm.badges);
      });
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

    function activate() {
      vm.getBadges();
      vm.getCategory();
    }
  }

})();
