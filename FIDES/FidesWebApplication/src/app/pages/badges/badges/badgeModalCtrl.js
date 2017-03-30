(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badges')
    .controller('BadgeModalCtrl', BadgeModalCtrl);

  /** @ngInject */
  function BadgeModalCtrl($uibModalInstance, organizationsData, toastr, badgesService, $http) {
    var vm = this;
    vm.modalBadge = {};
    vm.modalBadge.noPicture = true;
    vm.modalBadge.data = {};
    vm.modalBadge.data.gluuAssociatedOrganization = "";
    vm.modalBadge.logo = {};
    vm.organizations = organizationsData.organizations;
    vm.imgSrc = '';
    vm.createBadge = createBadge;
    vm.uploadPicture = uploadPicture;
    vm.removePicture = removePicture;
    vm.onFileSelect = onFileSelect;
    vm.drawBadgeLogo = drawBadgeLogo;
    vm.downloadBadgeLogo = downloadBadgeLogo;

    function createBadge(isFormValid) {
      $uibModalInstance.close(vm.modalBadge.data);
      return;
      if (!isFormValid) {
        return false;
      }

      var badgeData = JSON.stringify(vm.modalBadge.data);
      var picture = vm.modalBadge.file;
      var badgeForm = new FormData();
      badgeForm.append("badge", badgeData);
      badgeForm.append("picture", picture);

      badgesService.createBadge(badgeForm).then(onSuccess).catch(onError);

      function onSuccess(response) {
        if (response.data.badge.displayName) {
          $uibModalInstance.close(response.data.badge);
        }
        toastr.success('Badge created successfully', 'Badges', {});
      }

      function onError(error) {
        toastr.error(error.data.error, 'Badges', {})
      }
    }

    function uploadPicture() {
      var fileInput = document.getElementById('uploadBadgeLogo');
      fileInput.click();
    }

    function removePicture() {
      vm.modalBadge.file = {};
      vm.modalBadge.noPicture = true;
    }

    function onFileSelect(files) {
      vm.modalBadge.file = files[0];
      vm.modalBadge.noPicture = false;
    }

    function drawBadgeLogo() {
      vm.imgSrc = 'https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=' + vm.modalBadge.logo.bannerText;
    }

    function downloadBadgeLogo() {
      // Construct the a element
      var link = document.createElement("a");
      link.download = 'logo.png';
      link.target = "_blank";

      // Construct the uri
      link.href = 'https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=' + vm.modalBadge.logo.bannerText;
      ;
      document.body.appendChild(link);
      link.click();

      // Cleanup the DOM
      document.body.removeChild(link);
      //delete link;
    }

    $uibModalInstance.rendered.then(function () {
      drawBadgeLogo();
    });
  }
})();
