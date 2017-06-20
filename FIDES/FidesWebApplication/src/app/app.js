'use strict';

angular.module('FidesWebApplication', [
  'angular-loading-bar',
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
  'checklist-model',
  'FidesWebApplication.theme',
  'FidesWebApplication.pages'
]).config(function ($locationProvider) {
  $locationProvider.html5Mode({
    enabled: true,
    requireBase: false,
    rewriteLinks: true
  });
}).constant('urls', {
  BASE: 'https://127.0.0.1:3000',
  AUTH_URL: 'https://127.0.0.1:3000/login.html',
  BASE_API: 'http://127.0.0.1:8000',
  USER_PROFILE: 'assets/img/theme/no-photo.png',
  BADGE_URL: 'http://192.168.200.78:8080/badge-mgr'
}).run(function ($rootScope, $localStorage, $http, $window, $state, urls, toastr) {
  if ($window.location.pathname == "/login.html" || $window.location.pathname == "/register.html") {
    removeTokenAndRedirect($localStorage, $http, $window, urls);
    return;
  }

  if (($window.location.href !== urls.AUTH_URL && $localStorage.currentUser == undefined)) {
    removeTokenAndRedirect($localStorage, $http, $window, urls, true);
  } else {
    angular.forEach($state.get(), function (s) {
      s.visible = false;
      var userRole = $localStorage.currentUser.role;
      if (userRole != undefined) {
        if (s.roles === undefined || s.roles.indexOf(userRole) >= 0) {
          s.visible = true;
        }
      }
    });

    $rootScope.$on('$stateChangeStart', function (event, toState, toParams, fromState, fromParams) {
      if ($window.location.pathname == "/login.html" || $window.location.pathname == "/register.html") {
        removeTokenAndRedirect($localStorage, $http, $window, urls);
        return;
      }

      if (toState.authenticate && $localStorage.currentUser == undefined) {
        removeTokenAndRedirect($localStorage, $http, $window, urls, true);
        return;
      }

      // add jwt token to auth header for all requests made by the $http service
      $http.defaults.headers.common.Authorization = 'Bearer ' + $localStorage.currentUser.token;
      var userRole = $localStorage.currentUser.role;
      if (userRole != undefined) {
        if (toState.roles != undefined) {
          if (!toState.roles.includes(userRole)) {
            toastr.error("You are not authorized person to view this content. Please contact admin for more details.", '', {});
            event.preventDefault();
          }
        }
      } else {
        removeTokenAndRedirect($localStorage, $http, $window, urls, true, "?error=Please login first.");
      }
    });

    //navigate to home page if state is not found.
    if (!$state.current.name) {
      $state.go('home');
    }
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
}]).factory('stateCityService',['$http', 'urls', function ($http, urls) {
  return $http.get(urls.BASE + '/us_states_cities.json');
}]).filter('myStrictFilter', function($filter){
  return function(input, predicate){
    return $filter('filter')(input, predicate, true);
  }
}).filter('unique', function() {
  return function (arr, field) {
    var o = {}, i, l = !!arr ? arr.length : 0, r = [];
    for(i=0; i<l;i+=1) {
      o[arr[i][field]] = arr[i];
    }
    for(i in o) {
      r.push(o[i]);
    }
    return r;
  };
});

function removeTokenAndRedirect($localStorage, $http, $window, urls, redirectToLogin, message) {
  if (!message) {
    message = '';
  }

  delete $localStorage.currentUser;
  $http.defaults.headers.common.Authorization = '';
  if (redirectToLogin) {
    $window.location = urls.AUTH_URL + message;
  }
}