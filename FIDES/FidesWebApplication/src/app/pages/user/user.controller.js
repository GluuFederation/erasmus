(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.user')
    .controller('UserController', UserController);

  /** @ngInject */
  function UserController($scope, $filter, $localStorage, $uibModal, toastr, userService, stateCityService) {
    var vm = this;
    vm.users = vm.displayedCollection = undefined;

    function removeUser(id) {
      var deleteUser = confirm('Are you sure you want to remove this user?');
      if (!deleteUser) {
        return null;
      }
      userService.removeUser(id, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          _.remove(vm.users, {username: response.data.username});
          vm.displayedCollection = angular.copy(vm.users);
        }
        toastr.success('Removed successfully', 'User', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'User', {});
      }
    }

    function getUsers() {
      userService.getUsers(onSuccess, onError);
      function onSuccess(response) {
        if (response.data && response.data.length > 0) {
          vm.users = response.data;
          vm.displayedCollection = angular.copy(vm.users);
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'User', {});
      }
    }

    function openUserModal(userData) {
      vm.userModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/user/createUser.modal.html',
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
        var userIndex = _.findIndex(vm.users, {username: newUser.username});
        if (userIndex >= 0) {
          vm.users[userIndex] = newUser;
        } else {
          vm.users.push(newUser);
        }

        vm.displayedCollection = angular.copy(vm.users);
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
      vm.participants = {};
      vm.stateCityList = {};
      vm.states = [];

      getAllRoles();
      getAllParticipants();
      getStateCity();

      if (userData) {
        vm.isInEditMode = true;
        vm.modalUser.username = userData.username;
        vm.modalUser.firstName = userData.firstName;
        vm.modalUser.lastName = userData.lastName;
        vm.modalUser.email = userData.email;
        vm.modalUser.roleId = userData.role[0]._id;
        vm.selectedRole = userData.role[0].name;
        vm.modalUser.phoneNo = userData.phoneNo;
        vm.modalUser.address = userData.address;
        vm.modalUser.zipcode = userData.zipcode;
        vm.modalUser.state = userData.state;
        vm.modalUser.city = userData.city;
        vm.modalUser.description = userData.description;
        if(userData.participant) {
          vm.modalUser.participant = userData.participant[0]._id;
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

      function getAllParticipants() {
        userService.getAllParticipants(onSuccess, onError);
        function onSuccess(response) {
          if (response.data) {
            if (response.data.length > 0) {
              vm.participants = response.data;
            }
          }
        }

        function onError(error) {
          console.log(JSON.stringify(error));
          toastr.error(error.data.message, 'Participants', {})
        }
      }

      function getStateCity() {
        stateCityService.then(function (response) {
          vm.stateCityList = response.data;
          vm.states = Object.keys(response.data);
          (!!vm.modalUser.state) ? vm.cities = vm.stateCityList[vm.modalUser.state] :'';
        });
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
          if(!vm.modalUser.participant){
            toastr.error("Please select participant.", "Update User", {});
            return false;
          }
        } else {
          vm.modalUser.participant = undefined;
        }

        if (vm.isInEditMode) {
          userService.updateUser(JSON.stringify(vm.modalUser), onSuccess, onError);
        } else {
         // userService.createUser(JSON.stringify(vm.modalUser), onSuccess, onError);
        }

        function onSuccess(response) {
          toastr.success('Saved successfully', 'User', {});

          if (response.data) {
            if($localStorage.currentUser.user.username === response.data.username) {
              $localStorage.currentUser.user = response.data;
            }
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'User', {})
        }
      }

      function stateChanged() {
        vm.cities = vm.stateCityList[vm.modalUser.state];
      }

      vm.roleChanged = roleChanged;
      vm.pushUser = pushUser;
      vm.stateChanged = stateChanged;
    }
  }
})();
