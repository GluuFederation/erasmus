(function () {
  'use strict';

  angular.module('FidesWebApplication.pages.badges.badges')
    .controller('BadgeModalCtrl', BadgeModalCtrl);

  /** @ngInject */
  function BadgeModalCtrl($uibModalInstance, badge, toastr, badgesService, $timeout, badgeCategoryService) {
    var vm = this;
    vm.modalBadge = {};
    vm.modalBadge.noPicture = true;
    vm.modalBadge.data = badge;
    vm.modalBadge.logo = {};
    vm.badgeCategories = [];
    vm.imgSrc = '';
    vm.createBadge = createBadge;
    vm.uploadPicture = uploadPicture;
    vm.removePicture = removePicture;
    vm.onFileSelect = onFileSelect;
    vm.drawBadgeLogo = drawBadgeLogo;
    vm.downloadBadgeLogo = downloadBadgeLogo;
    vm.getBadgeCategories = getBadgeCategories;

    function createBadge(isFormValid) {
      if (!isFormValid) {
        return false;
      }
      vm.modalBadge.data.type = 'BadgeClasses';
      vm.modalBadge.data.image = vm.modalBadge.file;
      var fd = new FormData();
      for (var key in vm.modalBadge.data) {
        fd.append(key, vm.modalBadge.data[key]);
      }

      badgesService.createBadge(fd).then(onSuccess).catch(onError);

      function onSuccess(response) {
        if (response.data.name) {
          $uibModalInstance.close(response.data);
        }
        toastr.success('Badge created successfully', 'Badges', {});
      }

      function onError(error) {
        toastr.error(error.data.error, 'Badges', {})
      }
    }

    function uploadPicture() {
      var fileInput = document.getElementById('badgePicture');
      fileInput.click();
    }

    function removePicture() {
      vm.modalBadge.file = {};
      vm.modalBadge.noPicture = true;
    }

    vm.fileReaderSupported = window.FileReader != null;
    function onFileSelect(files) {
      vm.modalBadge.file = files[0];
      vm.modalBadge.noPicture = false;
      if (files != null) {
        var file = files[0];
        if (vm.fileReaderSupported && file.type.indexOf('image') > -1) {
          $timeout(function() {
            var fileReader = new FileReader();
            fileReader.readAsDataURL(file);
            fileReader.onload = function(e) {
              $timeout(function(){
                vm.imgSrc = e.target.result;
              });
            }
          });
        }
      }
    }

    function drawBadgeLogo() {
      var logoOptions = vm.modalBadge.logo;
      var ctx = badgeLogoCanvas.getContext('2d');
      ctx.clearRect(0, 0, badgeLogoCanvas.width, badgeLogoCanvas.height);
      var txt = logoOptions && logoOptions.bannerText || 'Sample text';
      var font = '32px arial';
      var fontColor = logoOptions && logoOptions.bannerTextColor || '#000';
      var bgcolor = logoOptions && logoOptions.bannerColor || '#f50';

      var padding = 16;
      badgeLogoCanvas.width = ctx.measureText(txt).width * parseInt(font, 10) / 10 + padding;
      badgeLogoCanvas.height = parseInt(font, 10) + padding;

      ctx.save();
      ctx.font = font;
      ctx.textBaseline = 'top';
      ctx.fillStyle = bgcolor;

      ctx.fillRect(0, 0, badgeLogoCanvas.width, badgeLogoCanvas.height);

      ctx.fillStyle = fontColor;
      ctx.textAlign="center";
      ctx.fillText(txt, badgeLogoCanvas.width/2, 8);

      ctx.restore();
    }

    function downloadBadgeLogo() {
      var image = badgeLogoCanvas.toDataURL("image/png").replace("image/png", "image/octet-stream");
      image = image.replace(/^data:image\/[^;]*/, 'data:application/octet-stream');
      image = image.replace(/^data:application\/octet-stream/, 'data:application/octet-stream;headers=Content-Disposition%3A%20attachment%3B%20filename=Logo.png');
      var link = document.createElement("a");
      link.download = 'logo.png';
      link.target = '_blank';
      link.href = badgeLogoCanvas.toDataURL("image/png").replace("image/png", "image/octet-stream");
      vm.imgSrc = link.href;
      link.click();
    }

    // function drawBadgeLogo() {
    //   vm.imgSrc = 'https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=' + vm.modalBadge.logo.bannerText;
    // }

    // function downloadBadgeLogo() {
    //   // Construct the a element
    //   var link = document.createElement("a");
    //   link.download = 'logo.png';
    //   link.target = "_blank";
    //
    //   // Construct the uri
    //   link.href = 'https://api.qrserver.com/v1/create-qr-code/?size=150x150&data=' + vm.modalBadge.logo.bannerText;
    //   document.body.appendChild(link);
    //   link.click();
    //
    //   // Cleanup the DOM
    //   document.body.removeChild(link);
    //   //delete link;
    // }

    $uibModalInstance.rendered.then(function () {
      drawBadgeLogo();
    });

    function getBadgeCategories() {
      badgeCategoryService.getAllBadgeCategory().then(onSuccess).catch(onFailed);

      function onSuccess(response) {
        if (response.data && response.data.length > 0) {
          vm.badgeCategories = response.data;
        }
      }
      
      function onFailed(error) {
        toastr.error(error.data.message, 'Badges', {})
      }
    }

    // init
    vm.getBadgeCategories();
  }
})();
