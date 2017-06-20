(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.entity')
    .controller('EntityController', EntityController);

  /** @ngInject */
  function EntityController($scope, $filter, $localStorage, $uibModal, toastr, entityService) {
    var vm = this;
    vm.entities = vm.displayedCollection = undefined;
    vm.userRole = $localStorage.currentUser.role;

    function removeEntity(entityData) {
      if(entityData.isApproved === true) {
        toastr.error('You can not remove already approved entity.', 'Entity', {});
        return null;
      }

      var deleteEntity = confirm('Are you sure you want to remove this entity?');
      if (!deleteEntity) {
        return null;
      }

      entityService.removeEntity(entityData._id, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          _.remove(vm.entities, {_id: response.data._id});
          vm.displayedCollection = angular.copy(vm.entities);
        }
        toastr.success('Removed successfully', 'Entity', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Entity', {});
      }
    }

    function approveEntity(entityData) {
      if(entityData.isApproved === true) {
        toastr.error('Entity is already approved.', 'Entity', {});
        return null;
      }

      if(entityData.participant.isApproved !== true) {
        toastr.error('Please approve related participant first to proceed.', 'Entity', {});
        return null;
      }

      if(vm.userRole != 'admin'){
        toastr.error('You are not authorized to do this operation.', 'Entity', {});
        return null;
      }

      var confirmApproval = confirm('Do you want to approve this entity?');
      if (!confirmApproval) {
        return null;
      }

      entityService.approveEntity(entityData._id, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          var index = _.findIndex(vm.entities, {_id: response.data._id});
          if (index >= 0) {
            vm.entities[index] = response.data;
          }

          vm.displayedCollection = angular.copy(vm.entities);
        }

        toastr.success('Approved successfully', 'Entity', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Entity', {});
      }
    }

    function verifyEntity(entity) {
      var confirmVerify = confirm('Do you want to verify this entity?');
      if (!confirmVerify) {
        return null;
      }

      entityService.verifyEntity(entity, onSuccess, onError);

      function onSuccess(response) {
        if (response.data) {
          var index = _.findIndex(vm.entities, {_id: response.data._id});
          if (index >= 0) {
            vm.entities[index] = response.data;
          }

          vm.displayedCollection = angular.copy(vm.entities);
        }

        toastr.success('Verified successfully', 'Entity', {});
      }

      function onError(error) {
        toastr.error(error.data.message, 'Entity', {});
      }
    }

    function getEntities() {
      var userId = undefined;
      if(vm.userRole != 'admin' && $localStorage.currentUser.user){
        userId = $localStorage.currentUser.user._id;
      }

      entityService.getEntities(userId, onSuccess, onError);
      function onSuccess(response) {
        if (response.data && response.data.length > 0) {
          vm.entities = response.data;
          vm.displayedCollection = angular.copy(vm.entities);
        }
      }

      function onError(error) {
        toastr.error(error.data.message, 'Entity', {});
      }
    }

    function openEntityModal(entityData) {
      if(entityData && entityData.isApproved === true) {
        toastr.error('You can not modify data of already approved entity.', 'Entity', {});
        return null;
      }

      vm.entityModal = $uibModal.open({
        animation: true,
        templateUrl: 'app/pages/entity/createEntity.modal.html',
        size: 'lg',
        controller: ['$uibModalInstance', 'entityData', 'userService', CreateEntityController],
        controllerAs: 'vm',
        resolve: {
          entityData: function () {
            return entityData;
          }
        }
      });

      vm.entityModal.result.then(function (newEntity) {
        var index = _.findIndex(vm.entities, {_id: newEntity._id});
        if (index >= 0) {
          vm.entities[index] = newEntity;
        } else {
          if(vm.entities === undefined) {
            vm.entities = vm.displayedCollection = [];
          }

          vm.entities.push(newEntity);
        }

        vm.displayedCollection = angular.copy(vm.entities);
      });
    }

    //Export the modules for view.
    vm.openEntityModal = openEntityModal;
    vm.removeEntity = removeEntity;
    vm.getEntities = getEntities;
    vm.approveEntity = approveEntity;
    vm.verifyEntity = verifyEntity;

    vm.getEntities();

    // Model Controller
    function CreateEntityController($uibModalInstance, entityData, userService) {
      var vm = this;
      vm.modalEntity = {};
      vm.isInEditMode = false;
      vm.userRole = $localStorage.currentUser.role;
      vm.roles = {};
      vm.participants = {};

      getAllParticipants();

      if (entityData) {
        vm.isInEditMode = true;
        vm.modalEntity._id = entityData._id;
        vm.modalEntity.name = entityData.name;
        vm.modalEntity.discoveryUrl = entityData.discoveryUrl;
        vm.modalEntity.clientId = entityData.clientId;
        vm.modalEntity.clientSecret = entityData.clientSecret;
        // vm.modalEntity.responseType = entityData.responseType;
        // vm.modalEntity.state = entityData.state;
        // vm.modalEntity.redirectUri = entityData.redirectUri;
        // vm.modalEntity.grantType = entityData.grantType;
        // vm.modalEntity.code = entityData.code;
        // vm.modalEntity.scope = entityData.scope;
        // vm.modalEntity.username = entityData.username;
        // vm.modalEntity.password = entityData.password;
        // vm.modalEntity.errorUri = entityData.errorUri;
        if (entityData.participant) {
          vm.modalEntity.participant = entityData.participant._id;
        }
      }

      function getAllParticipants() {
        userService.getAllParticipants(onSuccess, onError);
        function onSuccess(response) {
          if (response.data && response.data.length > 0) {
            vm.participants = response.data;
          }
        }

        function onError(error) {
          console.log(JSON.stringify(error));
          toastr.error(error.data.message, 'Participants', {})
        }
      }

      function pushEntity(isFormValid) {
        if (!isFormValid) {
          return false;
        }

        if (vm.userRole === 'admin') {
          if (!vm.modalEntity.participant) {
            toastr.error("Please select participant.", "Update Entity", {});
            return false;
          }
        } else {
          vm.modalEntity.participant = $localStorage.currentUser.user.participant._id;
        }
        vm.modalEntity.createdBy = $localStorage.currentUser.user._id;

        if (vm.isInEditMode) {
          entityService.updateEntity(JSON.stringify(vm.modalEntity), onSuccess, onError);
        } else {
          entityService.createEntity(JSON.stringify(vm.modalEntity), onSuccess, onError);
        }

        function onSuccess(response) {
          toastr.success('Saved successfully', 'Entity', {});

          if (response.data) {
            $uibModalInstance.close(response.data);
          }
        }

        function onError(error) {
          toastr.error(error.data.message, 'Entity', {})
        }
      }

      vm.pushEntity = pushEntity;
    }
  }
})();
