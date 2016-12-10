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
  $locationProvider.html5Mode({
    enabled: true,
    requireBase: false,
    rewriteLinks: true
  });
}).constant('urls', {
  BASE: 'http://192.168.200.70:3000',
  AUTH_URL: 'http://192.168.200.70:3000/login.html',
  BASE_API: 'http://192.168.200.70:8000',
  USER_PROFILE: 'assets/img/theme/no-photo.png'
}).run(function ($rootScope, $localStorage, $http, $window, $state, urls) {
  if ($window.location.pathname == "/login.html" || $window.location.pathname == "/login") {
    removeToken($localStorage, $http, $window, urls, false);
    return;
  }

  if ($window.location.href !== urls.AUTH_URL && $localStorage.currentUser == undefined) {
    removeToken($localStorage, $http, $window, urls, true);
  } else {
    $rootScope.$on('$stateChangeStart', function (event, toState) {

      if (toState.name == "login" || $window.location.pathname == "/login.html") {
        removeToken($localStorage, $http, $window, urls, false);
        return;
      }

      if (toState.authenticate && $localStorage.currentUser == undefined) {
        removeToken($localStorage, $http, $window, urls, true);
        return;
      }

      // add jwt token to auth header for all requests made by the $http service
      $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.currentUser.token;

      /*var userRole = $localStorage.userRole;
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
       }*/
    });
  }
}).factory('authHttpResponseInterceptor', ['$q', '$window', 'urls', function ($q, $window, urls) {
  return {
    response: function (response) {
      if (response.status === 401) {
        //console.log("Response 401");
      }
      return response || $q.when(response);
    },
    responseError: function (rejection) {
      if (rejection.status === 401) {
       // console.log("Response Error 401", rejection);
        $window.location = urls.AUTH_URL;
      }
      return $q.reject(rejection);
    }
  }
}]).config(['$httpProvider', function ($httpProvider) {
  //Http Interceptor to check auth failures for xhr requests
  $httpProvider.interceptors.push('authHttpResponseInterceptor');
}])
;

function removeToken($localStorage, $http, $window, urls, redirectToLogin) {
  delete $localStorage.currentUser;
  $http.defaults.headers.common.Authorization = '';
  if(redirectToLogin){
    $window.location = urls.AUTH_URL;
  }
}