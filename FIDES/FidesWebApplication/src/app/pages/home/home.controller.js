(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.home')
    .controller('HomeController', HomeController);

  /** @ngInject */
  function HomeController($http, toastr, urls, $localStorage, $uibModal, $timeout) {
    var vm = this;
    vm.participant = (!!$localStorage.currentUser) ? $localStorage.currentUser.user.participant : null;
    vm.isShow = (!!$localStorage.currentUser) ? $localStorage.currentUser.role === 'orgadmin' : false;
    var file = '';
    vm.FIDES_BASE_API = urls.FIDES_BASE_API;

    vm.openParticipantModal = openParticipantModal;

    $http.get(urls.FIDES_BASE_API + "/loggedIn").then(onSuccess).catch(onError);

    function onSuccess(response) {
      //console.log(response.data);
    }

    function onError(error) {
      //console.log(JSON.stringify(error));
      toastr.error(error.data.message, 'FIDES', {})
    }

    // participant edit model
    function openParticipantModal() {
      vm.participantModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/home/manageParticipant.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'participantData', 'stateCityService', 'participantService', createParticipantController],
        controllerAs: 'vm',
        resolve: {
          participantData: function () {
            return vm.participant;
          }
        }
      });

      vm.participantModal.result.then(function (newParticipant) {
        $localStorage.currentUser.user.participant = newParticipant;
        vm.participant = newParticipant;
      });
    }

    function createParticipantController($uibModalInstance, participantData, stateCityService, participantService) {
      var vm = this;
      vm.modalParticipant = {};
      vm.stateCityList = {};
      vm.states = [];
      vm.participants = {};
      vm.federations = null;
      var file = '';

      if (participantData) {
        vm.modalParticipant._id = participantData._id;
        vm.modalParticipant.name = participantData.name;
        vm.modalParticipant.phoneNo = participantData.phoneNo;
        vm.modalParticipant.address = participantData.address;
        vm.modalParticipant.zipcode = participantData.zipcode;
        vm.modalParticipant.state = participantData.state;
        vm.modalParticipant.city = participantData.city;
        vm.modalParticipant.type = participantData.type;
        vm.modalParticipant.isApproved = participantData.isApproved;
        vm.modalParticipant.description = participantData.description;
        vm.modalParticipant.trustMarkFile = participantData.trustMarkFile;
        vm.modalParticipant.oldtrustMarkFile = participantData.trustMarkFile;
      }

      function pushParticipant(isFormValid) {
        if (!isFormValid) {
          return false;
        }
        vm.modalParticipant.trustMarkFile = file;
        var fd = new FormData();
        for (var key in vm.modalParticipant) {
          fd.append(key, vm.modalParticipant[key]);
        }

        participantService.updateParticipantWithFile(fd, onSuccess, onError);

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Participant', {});

          if (response.data) {
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Participant', {});
        }
      }

      function initLoads() {
        stateCityService.then(function (response) {
          vm.stateCityList = response.data;
          vm.states = Object.keys(response.data);
          vm.cities = vm.stateCityList[vm.modalParticipant.state];
        });
      }

      function stateChanged() {
        vm.cities = vm.stateCityList[vm.modalParticipant.state];
      }

      //set file function
      function photoChanged(files) {
        if (files != null) {
          file = files[0];
          $timeout(function () {
            var fileReader = new FileReader();
            fileReader.readAsText(file);
            fileReader.onload = function (e) {
              $timeout(function () {
                try {
                  JSON.parse(fileReader.result);
                  $("#btnApproveParticipant").show();
                } catch (e) {
                  toastr.error('Please select valid json file', 'Participant', {});
                  $("#btnApproveParticipant").hide();
                }
              });
            }
          });
        }
      }

      vm.pushParticipant = pushParticipant;
      vm.stateChanged = stateChanged;
      vm.photoChanged = photoChanged;
      initLoads();
    }
  }
})();
