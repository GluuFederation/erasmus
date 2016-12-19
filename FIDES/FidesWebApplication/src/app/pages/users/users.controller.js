(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.users')
    .controller('UsersController', UsersController);

  /** @ngInject */
  function UsersController($scope, $filter, $localStorage, $uibModal, toastr, userService) {
    var vm = this;
    vm.users = vm.displayedCollection = {};

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
        toastr.error(error.data.message, 'Users', {});
      }
    }

    function getUsers() {
      userService.getUsers(onSuccess, onError);
      function onSuccess(response) {
        if (response.data) {
          vm.users = response.data;
          vm.displayedCollection = [].concat(vm.users);
        }
      }

      function onError(error) {
        //console.log(JSON.stringify(error));
        toastr.error(error.data.message, 'Users', {});
      }
    }

    function openUserModal(userData, index) {
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
        if (index >= 0) {
          vm.users[index] = newUser;
        } else {
          vm.users.push(newUser);
        }
        vm.displayedCollection = [].concat(vm.users);
      });
    }

    //Export the modules for view.
    vm.openUserModal = openUserModal;
    vm.removeUser = removeUser;
    vm.getUsers = getUsers;

    vm.getUsers();

    //Model Controller
    function CreateUserController($uibModalInstance, userData) {
      var vm = this;
      vm.modalUser = {};
      vm.isInEditMode = false;
      //vm.editPassword = false;
      vm.selectedRole='admin';
      vm.roles = {};
      vm.organizations = {};

      getAllRoles();
      getAllOrganizations();

      if (userData) {
        vm.isInEditMode = true;
        vm.modalUser.username = userData.username;
        vm.modalUser.firstName = userData.firstName;
        vm.modalUser.lastName = userData.lastName;
        vm.modalUser.email = userData.email;
        vm.modalUser.roleId = userData.role._id;
        vm.selectedRole = userData.role.name;
        if(userData.organization) {
          vm.modalUser.organizationId = userData.organization._id;
        }
      }

      function roleChanged(itemId) {
        var selectedRole = _.find(vm.roles, {'_id': itemId});
        if(selectedRole) {
          vm.selectedRole = selectedRole.name;
        }
      }

      function getAllRoles() {
        userService.getAllRoles(onSuccess, onError);
        function onSuccess(response) {
          if (response.data) {
            vm.roles = response.data;
            if (!vm.modalUser.roleId && vm.roles.length > 0) {
              vm.modalUser.roleId = vm.roles[0]._id;
            }
          }
        }

        function onError(error) {
          //console.log(JSON.stringify(error));
          toastr.error(error.data.message, 'Roles', {})
        }
      }

      function getAllOrganizations() {
        userService.getAllOrganizations(onSuccess, onError);
        function onSuccess(response) {
          if (response.data) {
            if (response.data.length > 0) {
              console.log(response.data);
              vm.organizations = response.data;
            }
          }
        }

        function onError(error) {
          console.log(JSON.stringify(error));
          toastr.error(error.data.message, 'Organizations', {})
        }
      }

      function pushUser(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        // if(vm.editPassword){
        //   if(!vm.modalUser.password){
        //     toastr.error("Please provide password.", "Update User", {});
        //     return false;
        //   }
        // }

        if(vm.selectedRole === 'orgadmin'){
          if(!vm.modalUser.organizationId){
            toastr.error("Please select organization.", "Update User", {});
            return false;
          }
        } else {
          vm.modalUser.organizationId = undefined;
        }

        if (vm.isInEditMode) {
          userService.updateUser(JSON.stringify(vm.modalUser), onSuccess, onError);
        } else {
          userService.createUser(JSON.stringify(vm.modalUser), onSuccess, onError);
        }

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Users', {});

          if (response.data) {
            if($localStorage.currentUser.user.username === response.data.username) {
              $localStorage.currentUser.user = response.data;
            }
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Users', {})
        }
      }

      vm.roleChanged = roleChanged;
      vm.pushUser = pushUser;
    }
  }
})();
