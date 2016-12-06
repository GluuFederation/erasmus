(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.users')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController($scope, $filter, $uibModal, toastr, userService) {
    var vm = this;
    vm.users = {};

    function removeUser(username) {
      var deleteUser = confirm('Are you sure you want to remove this user?');
      if (!deleteUser) {
        return null;
      }
      userService.removeUser(username, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          _.remove(vm.users, {username: response.data.username});
        }
        toastr.success('Removed successfully', 'Users', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Users', {})
      }
    }

    function getUsers() {
      userService.getUsers(onSuccess, onError);
      function onSuccess(response) {
        if (response.data) {
          vm.users = response.data;
        }
        toastr.success('Fetched successfully', 'Users', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Users', {})
      }
    }

    function openUserModal(userData) {
      vm.userModal = $uibModal.open({
        animation: true,
        templateUrl: '/app/pages/users/createUser.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'userData', CreateUserController],
        controllerAs: 'vm',
        resolve: {
          userData: function () {
            return userData;
          }
        }
      });

      vm.userModal.result.then(function (newUser) {
        if (userData) {
          _.remove(vm.users, {username: newUser.username});
        }

        vm.users.push(newUser);
      });
    }

    //Export the modules for view.
    vm.openUserModal = openUserModal;
    vm.removeUser = removeUser;
    vm.getUsers = getUsers;
    vm.displayedCollection = [].concat(vm.users);

    vm.getUsers();
    //Model Controller
    function CreateUserController($uibModalInstance, userData) {
      var vm = this;
      vm.modalUser = {};
      vm.isInEditMode = false;
      console.log(userData);

      if(userData) {
        vm.modalUser.username = userData.username;
        vm.modalUser.password = userData.password;
        vm.modalUser.firstName = userData.firstName;
        vm.modalUser.lastName = userData.lastName;
        vm.modalUser.email = userData.email;
        vm.isInEditMode = true;
      }

      function pushUser(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        if(vm.isInEditMode){
          userService.updateUser(JSON.stringify(vm.modalUser), onSuccess, onError);
        } else {
          userService.createUser(JSON.stringify(vm.modalUser), onSuccess, onError);
        }

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Users', {});

          if (response.data) {
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Users', {})
        }
      }

      vm.pushUser = pushUser;
    }
  }
})();
