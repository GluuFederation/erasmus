(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.userBadgeRequest')
    .controller('userBadgeRequestCtrl', userBadgeRequestCtrl);

  /** @ngInject */
  function userBadgeRequestCtrl(toastr, $uibModal, userBadgeRequestService, $localStorage) {
    var vm = this;
    vm.tablePageSize = 10;
    //vm.badges = [{"provider":"https://ce-dev2.gluu.org","_id":"58e5ed3a96d2046408a7359d","@context":"https://raw.githubusercontent.com/KantaraInitiative/wg-otto/master/schema/otto/organization.jsonld","@id":"http://localhost:5053,/otto/organization/58e5ed3a96d2046408a7359d","updatedAt":"2017-04-06T12:39:41.186Z","createdAt":"2017-04-06T07:24:42.213Z","name":"Local Org","phoneNo":"22221333366","address":"c123","zipcode":"21415","state":"Arizona","city":"Cochise","type":"service","description":"service","__v":2,"federation":{"_id":"58db728c6e98661975a9fae0","@context":"https://raw.githubusercontent.com/KantaraInitiative/wg-otto/master/schema/otto/federation.jsonld","@id":"http://localhost:5053,/otto/federations/58db728c6e98661975a9fae0","updatedAt":"2017-04-06T07:28:17.869Z","createdAt":"2017-03-29T08:38:36.602Z","name":"fed1","__v":5,"organization":"58d8b139987e8419d0a3c1cc","participants":["58db5e5d07f1b22d72deb271","58db5f1d07f1b22d72deb274","58e33b29d72d0f9e1e544e07","58e38ee573c84fe04e123b54","58e5ed3a96d2046408a7359d"],"entities":[],"keys":[],"isActive":true},"approvedBadges":[],"pendingBadges":["58dfa0d7a016c8832d9b7eab","58e236537e3a8d1121233123"],"federations":[],"entities":[],"isActive":false,"isApproved":true}];
    vm.badges = [];
    vm.displayedCollection = [];
    vm.category = 0;
    vm.organization = (!!$localStorage.currentUser) ? $localStorage.currentUser.user.organization : null;

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

      function badgeApprove() {
        if (vm.validity < 1 || vm.validity > 10) {
          toastr.error('Please enter days between 1 to 10', 'Badge', {});
          return;
        }

        var formData = {
          inum: badge.inum,
          validity: vm.days
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
      userBadgeRequestService.getPendingBadges(vm.organization._id).then(onSuccess).catch(onError);
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
