(function () {
  'use strict';

  angular.module('FidesWebApplication.theme.components')
    .directive('baWizard', baWizard);

  /** @ngInject */
  function baWizard() {
    return {
      restrict: 'E',
      transclude: true,
      scope: {
        onIndexChange: '&',
        onFinish: '&'
      },
      templateUrl: 'app/theme/components/baWizard/baWizard.html',
      controllerAs: '$baWizardController',
      controller: 'baWizardCtrl'
    }
  }
})();
