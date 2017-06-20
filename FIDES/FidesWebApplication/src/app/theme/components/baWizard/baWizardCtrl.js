(function () {
  'use strict';

  angular.module('FidesWebApplication.theme.components')
    .controller('baWizardCtrl', baWizardCtrl);

  /** @ngInject */
  function baWizardCtrl($scope) {
    var vm = this;
    vm.tabs = [];

    vm.tabNum = 0;
    vm.progress = 0;

    vm.addTab = function (tab) {
      tab.setPrev(vm.tabs[vm.tabs.length - 1]);
      vm.tabs.push(tab);
      vm.selectTab(0, true);
    };

    $scope.$watch(angular.bind(vm, function () {
      return vm.tabNum;
    }), calcProgress);

    vm.selectTab = function (tabNum, isIndexChanged) {
      vm.tabs[vm.tabNum].submit();
      if (vm.tabs[tabNum].isAvailiable() && (isIndexChanged || (tabNum >= 0 && (vm.tabNum - tabNum >= -1) && vm.validateTab(vm.tabNum, tabNum)))) {
        vm.tabNum = tabNum;
        vm.tabs.forEach(function (t, tIndex) {
          tIndex == vm.tabNum ? t.select(true) : t.select(false);
        });
      }
    };

    vm.isFirstTab = function () {
      return vm.tabNum == 0;
    };

    vm.isLastTab = function () {
      return vm.tabNum == vm.tabs.length - 1;
    };

    vm.nextTab = function () {
      if (!vm.isLastTab()) {
        return vm.selectTab(vm.tabNum + 1);
      } else {
        vm.tabs[vm.tabNum].submit();
        if (vm.tabs[vm.tabNum].isComplete() && vm.validateTab()) {
          return vm.finishWizard();
        } else {
          return true;
        }
      }
    };

    vm.previousTab = function () {
      return vm.selectTab(vm.tabNum - 1);
    };

    function calcProgress() {
      vm.progress = ((vm.tabNum + 1) / vm.tabs.length) * 100;
    }

    //call validateTab() which calls onIndexChange() which is declared on an attribute and linked to controller via wizard directive.
    vm.validateTab = function (lastIndex, index) {
      if ($scope.onIndexChange) {
        return $scope.onIndexChange({lastIndex: lastIndex, index: index});
      }
      return false;
    };

    //call finishWizard() which calls onFinish() which is declared on an attribute and linked to controller via wizard directive.
    vm.finishWizard = function () {
      if ($scope.onFinish) {
        return $scope.onFinish();
      }
      return false;
    };
  }
})();

