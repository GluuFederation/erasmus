(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.userActualBadge')
    .controller('userActualBadgeCtrl', userActualBadgeCtrl);

  /** @ngInject */
  function userActualBadgeCtrl(userActualBadgeService, $localStorage) {
    var vm = this;
    vm.tablePageSize = 10;
    vm.badges = [];
    vm.displayedCollection = [];
    vm.category = 0;
    vm.entity = (!!$localStorage.currentUser) ? $localStorage.currentUser.user.entity : null;

    vm.activate = activate;
    vm.getBadgeRequest = getBadgeRequest;
    vm.activate();

    function getBadgeRequest() {
      var formData = {
        opHost: vm.entity.discoveryUrl,
        status: 'Approved'
      };
      vm.badges = [
        {
          "inum": "@!4301.2A50.9A09.7688!1002!86BC.E0E4",
          "participant": "5923f1de8bc19a19d7e939c3",
          "templateBadgeId": "58fdb776da495fae4d6a3985",
          "templateBadgeTitle": "Entry-Level2 Firefighter",
          "status": "Pending",
          "requesterEmail": "megtest3@gluu.org",
          "privacy": "Public"
        },
        {
          "inum": "@!4301.2A50.9A09.7688!1002!2108.D20E",
          "participant": "5923f1de8bc19a19d7e939c3",
          "templateBadgeId": "58e49bec0cd5268169f7576a",
          "templateBadgeTitle": "Entry-Level Firefighter",
          "status": "Pending",
          "requesterEmail": "megtest3@gluu.org",
          "privacy": "Public"
        }];
      return 0;
      userActualBadgeService.getBadgeRequest(formData).then(onSuccess).catch(onError);
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
      vm.getBadgeRequest();
    }
  }
})();
