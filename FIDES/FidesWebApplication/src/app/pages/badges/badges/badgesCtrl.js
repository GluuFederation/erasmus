(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badges')
    .controller('BadgesCtrl', BadgesCtrl);

  /** @ngInject */
  function BadgesCtrl($filter, $uibModal, badgesService, confirmationModalService, urls, editableOptions, editableThemes, toastr) {
    var vm = this;
    vm.statuses = [
      {value: true, text: 'Active'},
      {value: false, text: 'Inactive'},
    ];
    vm.tablePageSize = 2;
    vm.oldBadge = {};

    vm.getBadges = getBadges;
    vm.removeBadge = removeBadge;
    vm.updateBadge = updateBadge;
    vm.showStatus = showStatus;
    vm.openBadgeModal = openBadgeModal;
    vm.getOrganizations = getOrganizations;
    vm.openConfirmationDialog = openConfirmationDialog;
    vm.activate = activate;

    vm.activate();

    function getBadges() {
      vm.badges = [{displayName: 'org1', description: 'EMT-Basic training', active: true}];
      vm.safeBadges = angular.copy(vm.badges);
      //badgesService.getBadges().then(onSuccess).catch(onError);

      function onSuccess(response) {
        vm.badges = response.data && response.data.badges;
        _.forEach(vm.badges, function (badge) {
          badge.hostedURL = urls.BADGE_API + '/badges/' + badge.inum;
          badge.picture = urls.BADGE_API + badge.picture;
        });
        vm.safeBadges = angular.copy(vm.badges);
      }

      function onError() {
        vm.badges = [];
        vm.safeBadges = [];
      }
    }

    function removeBadge(inum) {
      badgesService.removeBadge(inum).then(onSuccess).catch(onError);

      function onSuccess(response) {
        if (_.remove(vm.safeBadges, {inum: inum})) {
          vm.badges = angular.copy(vm.safeBadges);
        }
        toastr.success(response.data.success, 'Badges', {});
      }

      function onError(error) {
        toastr.error(error.data.error, 'Badges', {})
      }
    }

    function updateBadge(inum, badge, vmData) {
      vm.oldBadge = angular.copy(vmData);
      badge.gluuAssociatedOrganization = vmData.gluuAssociatedOrganization;
      badgesService.updateBadge(inum, badge).then(onSuccess).catch(onError);

      function onSuccess(response) {
        toastr.success(response.data.success, 'Badges', {});
      }

      function onError(error) {
        var badgeIndex = _.findIndex(vm.badges, {inum: inum});
        if (badgeIndex !== -1) {
          vm.badges[badgeIndex] = vm.oldBadge;
          vm.safeBadges[badgeIndex] = vm.oldBadge;
        }
        toastr.error(error.data.error, 'Badges', {})
      }
    }

    function showStatus(badge) {
      var selected = [];
      if (badge.active) {
        selected = $filter('filter')(vm.statuses, {value: badge.active});
      } else {
        selected = $filter('filter')(vm.statuses, {value: badge.active});
      }
      return selected.length ? selected[0].text : 'Not set';
    }

    function openBadgeModal() {
      $uibModal.open({
        animation: true,
        templateUrl: '/app/pages/modals/createBadge.modal.html',
        size: 'lg',
        controller: 'BadgeModalCtrl',
        controllerAs: 'vm',
        resolve: {
          organizationsData: function () {
            return {
              organizations: vm.organizations
            };
          }
        }
      }).result.then(function (badge) {
        //badge.picture = urls.BADGE_API + badge.picture;
        vm.badges.push(badge);
        vm.safeBadges.push(badge);
      });
    }

    function getOrganizations() {
      // organizationsService.getOrganizations().then(onSuccess).catch(onError);

      function onSuccess(response) {
        vm.organizations = response.data && response.data.organizations;
      }

      function onError() {
        vm.organizations = [];
        toastr.error("Error loading organizations", 'Organizations', {});
        return undefined;
      }
    }

    function openConfirmationDialog(inum) {
      var options = {
        title: "Delete Badge",
        content: "Are you sure you want to Delete Badge?",
        buttonTitle: "Yes",
        buttonType: "danger"
      };
      confirmationModalService.confirmationModal(options)
        .then(function () {
          vm.removeBadge(inum);
        });
    }

    function activate() {
      vm.getBadges();
      vm.getOrganizations();
      editableOptions.theme = 'bs3';
      editableThemes['bs3'].submitTpl = '<button type="submit" class="btn btn-primary btn-with-icon"><i class="ion-checkmark-round"></i></button>';
      editableThemes['bs3'].cancelTpl = '<button type="button" ng-click="$form.$cancel()" class="btn btn-default btn-with-icon"><i class="ion-close-round"></i></button>';
    }

  }

})();
