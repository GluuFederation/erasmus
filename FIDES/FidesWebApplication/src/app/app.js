'use strict';

angular.module('FidesWebApplication', [
  'ngAnimate',
  'ngStorage',
  'ui.bootstrap',
  'ui.sortable',
  'ui.router',
  'ngTouch',
  'toastr',
  'smart-table',
  "xeditable",
  'ui.slimscroll',
  'ngJsTree',
  'angular-progress-button-styles',

  'FidesWebApplication.theme',
  'FidesWebApplication.pages'
]).config(function ($locationProvider) {
  $locationProvider.html5Mode(true);
}).constant('urls', {
  BASE: 'http://192.168.200.70:3000',
  AUTH_URL: 'http://192.168.200.70:3000/auth.html',
  BASE_API: 'http://192.168.200.70:8000',
  USER_PROFILE: 'assets/img/theme/no-photo.png'
});
/* .run(function ($rootScope, $localStorage, $window, urls) {

 if ($window.location.pathname == "/auth.html")
 return;

 var userDetails = $localStorage.userDetails;

 if ($window.location.href !== urls.AUTH_URL && userDetails == undefined) {
 $window.location = urls.AUTH_URL;
 } else {
 $rootScope.$on('$stateChangeStart', function (event, toState) {

 if (toState.name == "login" || $window.location.pathname == "/auth.html") {
 return;
 }
 var userRole = $localStorage.userRole;
 if (userRole != undefined) {
 if (!toState.role.includes(userRole)) {
 $localStorage.$reset();
 // //$state.transitionTo('login');
 // var cookies = $localStorage.getAll();
 // angular.forEach(cookies, function (v, k) {
 // $localStorage.remove(k);
 // });
 $window.location = urls.AUTH_URL + "?error=You are not authorized person to view this content. Please contact admin for more details.";
 }
 } else {
 $window.location = urls.AUTH_URL + "?error=Please login first.";
 }
 });
 }
 });*/