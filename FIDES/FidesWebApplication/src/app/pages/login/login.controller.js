(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.login')
    .controller('LoginController', LoginController);

  /** @ngInject */
  function LoginController($http, $localStorage, $window, loginService, toastr, urls, $uibModal) {
    var vm = this;
    vm.login = login;
    vm.logout = logout;
    vm.openProfileModal = openProfileModal;
    vm.userProfilePic = userProfilePic;

    function login(isFormValid) {
      if(!isFormValid) {
        return;
      }

      loginService.login(vm.username, vm.password, onSuccess, onError);

      function onSuccess(response) {
        // login successful if there's a token in the response
        if (response.token) {
          // store username and token in local storage to keep user logged in between page refreshes
          $localStorage.currentUser = {user: response.user, role: response.role, token: response.token};
          $window.location = urls.BASE;
        } else {
          // execute callback with false to indicate failed login
          toastr.error(response.info.message, 'Login failed.', {})
        }
      }

      function onError(error) {
        console.log(error);
        toastr.error(error.data.message, 'FIDES');
      }
    }

    function userProfilePic() {
      //var userDetails = JSON.parse($cookies.get('userDetails'));
      //var userPic = userDetails && userDetails.picture && userDetails.picture[0];
      return urls.USER_PROFILE;
    }

    function openProfileModal() {
      vm.profileModal = $uibModal.open({
        animation: true,
        templateUrl: '/app/pages/login/profile.modal.html',
        size: 'md',
        controller: ['$uibModalInstance', 'userData', profileInfoController],
        controllerAs: 'vm',
        resolve: {
          userData: function () {
            return $localStorage.currentUser.user;
          }
        }
      });
    }

    function logout() {
      // remove user from local storage and clear http auth header
      delete $localStorage.currentUser;
      $http.defaults.headers.common.Authorization = '';
      $window.location = urls.AUTH_URL;
    }

    function profileInfoController($uibModalInstance, userData) {
      var vm = this;
      vm.modalUser = {};

      if (userData) {
        vm.modalUser.username = userData.username;
        vm.modalUser.password = userData.password;
        vm.modalUser.firstName = userData.firstName;
        vm.modalUser.lastName = userData.lastName;
        vm.modalUser.email = userData.email;
        vm.modalUser.roleId = userData.role._id;
      }

      function updateProfile(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        loginService.updateUser(JSON.stringify(vm.modalUser), onSuccess, onError);

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Profile', {});

          if (response.data) {
            $localStorage.currentUser.user = response.data;
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Profile', {})
        }
      }

      vm.updateProfile = updateProfile;
    }
  }
})();
