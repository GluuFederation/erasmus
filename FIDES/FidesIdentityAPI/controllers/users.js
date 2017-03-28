"use strict";
const scimConfig = {
  keyAlg: 'RS256',
  domain: 'https://gluu.local.org/',
  privateKey: 'scim-rp-sample.key',
  clientId: '@!8AEA.A0CF.482A.7912!0001!C0A0.3826!0008!6C3E.0965',
  keyId: '67a769f3-65f3-44ed-ad04-67aee51fbab0'
};
const scim = require('scim-node')(scimConfig);

const express = require('express');
const request = require('request-promise');
const jwt = require('jsonwebtoken');
const oxd = require('oxd-node');
const common = require('../helpers/common');
const httpStatus = require('http-status');
const Users = require('../helpers/users');
const Roles = require('../helpers/roles');
const Organizations = require('../helpers/organizations');
const Providers = require('../helpers/providers');
const Message = require('../helpers/message');

const router = express.Router();
/**
 * Validate user email before login.
 */
router.post('/validateEmail', (req, res, next) => {
  if (!req.body.email) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide email.'
    });
  }

  return Users.getUser(req.body)
    .then((user) => {
      if (!user.provider) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'There is no provider associated with the user.'
        });
      }

      let provider = user.provider;
      const loginUrl = JSON.parse(provider.redirectUris).find(x => x.indexOf('login.html') > 0);
      const registerUrl = JSON.parse(provider.redirectUris).find(x => x.indexOf('register.html') > 0);
      const authUrl = JSON.parse(provider.redirectUris).find(x => x.indexOf('auth.html') > 0);

      oxd.Request = {
        oxd_id: provider.oxdId,
        acr_values: ['basic'],
        port: process.env.OXD_PORT
      };

      oxd.get_authorization_url(oxd.Request, (response) => {
        response = JSON.parse(response);
        if (response.status != 'ok') {
          return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Failed to get authorization url'
          });
        }
        let url = response.data.authorization_url;
        const state = url.substring(url.indexOf('state=') + 6, url.indexOf('nonce=') - 1);
        url = url.replace(registerUrl, (req.body.isBadge ? authUrl : loginUrl));
        return res.status(httpStatus.OK).send({
          authEndpoint: url,
          email: req.body.email,
          redirectUri: loginUrl[0],
          state: state
        });
      });
    })
    .catch((err) => {
      if (err == false) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'User not found with this email.'
        });
      }
      return res.status(httpStatus.NOT_ACCEPTABLE).send({
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Authenticate user for login. (TODO: Compare entered email and OP login email)
 */
router.post('/login', (req, res, next) => {
  if (!req.body.email) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide email.'
    });
  }
  let user = null;
  return Users.getUser(req.body)
    .then((fUser) => {
      user = fUser;
      if (!user) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'User not found with this email.'
        });
      }

      if (!user.provider) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'There is no provider associated with the user.'
        });
      }
      const option = {
        method: 'GET',
        uri: user.provider.discoveryUrl + '/.well-known/openid-configuration',
        resolveWithFullResponse: true
      };
      return request(option);
    })
    .then((response) => {
      if (!response) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Metadata not found from Discovery URL. Please check Discovery URL.'
        });
      }

      let discoveryMetadata = {};
      try {
        discoveryMetadata = JSON.parse(response.body);
      } catch (exception) {
        console.log(exception.toString());
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Discovery URL is invalid. Please check and correct it.'
        });
      }

      let data = {};
      data.discoveryMetadata = discoveryMetadata;
      data.client_id = user.provider.clientId;
      data.client_secret = user.provider.clientSecret;
      data.redirect_uri = req.body.redirectUri;
      data.code = req.body.code;

      const clientInfo = {
        oxd_id: user.provider.oxdId,
        code: data.code,
        state: req.body.state
      };
      return getUserClaims(clientInfo);
    })
    .then((userInfo) => {
      if (!userInfo) {
        return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          message: common.message.INTERNAL_SERVER_ERROR
        });
      }

      if (!userInfo.data.claims.email[0] || userInfo.data.claims.email[0].toLowerCase() !== req.body.email.toLowerCase()) {
        return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          message: 'Email did not matched. Please check email address or logout from provider first and try again.'
        });
      }

      //Creating a JWT token for user session which is valid for 24 hrs.
      let token = jwt.sign(user, process.env.APP_SECRET, {
        expiresIn: process.env.JWT_EXPIRES_IN
      });

      return res.status(httpStatus.OK).send({
        user: user.safeModel(),
        role: user.role.name,
        token: token
      });
    })
    .catch((err) => {
      if (err == false) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'User not found'
        });
      }

      return res.status(httpStatus.NOT_ACCEPTABLE).send({
        err: err,
        message: 'Discovery URL is invalid. Please check and correct it.'
      });
    });
});

/**
 * Remove user. (TODO: update detail to SCIM)
 */
router.delete('/removeUser/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide username.'
    });
  }

  Users.removeUser(req.params.id)
    .then((user) => {
      if (!user) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'User ' + common.message.NOT_FOUND});
      }

      return res.status(httpStatus.OK).send(user);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Add user. (TODO: Add detail to SCIM)
 */
router.post('/signup', (req, res, next) => {
  if (!req.body.username) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide username.'
    });
  }

  if (!req.body.email) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide email.'
    });
  }

  if (!req.body.password) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide password.'
    });
  }

  if (!req.body.roleId) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide at least one role.'
    });
  }

  Users.createUser(req.body)
    .then((user) => {
      if (!user) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'User ' + common.message.NOT_FOUND});
      }

      return res.status(httpStatus.OK).send(user.safeModel());
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Update user password. (TODO: update password in SCIM)
 */
router.post('/updatePassword', (req, res, next) => {
  if (!req.body.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide username.'
    });
  }

  if (!req.body.currentPassword) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide current password.'
    });
  }

  if (!req.body.newPassword) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide new password.'
    });
  }

  Users.updatePassword(req.body)
    .then((user) => {
      if (!user) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'User ' + common.message.NOT_FOUND});
      }

      return res.status(httpStatus.OK).send(user.safeModel());
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Update user detail. (TODO: update detail to SCIM)
 */
router.post('/updateUser', (req, res, next) => {
  if (!req.body.username) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide username.'
    });
  }

  if (!req.body.email) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide email.'
    });
  }

  if (!req.body.roleId) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide role.'
    });
  }

  Users.updateUser(req.body)
    .then((user) => {
      if (!user) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'User ' + common.message.NOT_FOUND});
      }
      return res.status(httpStatus.OK).send(user.safeModel());
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Get list of all the organizations.
 */
router.get('/getAllOrganizations', (req, res, next) => {
  Users.getAllOrganizations()
    .then((organizations) => {
      if (!organizations) {
        return res.status(httpStatus.OK).send({message: 'Organization ' + common.message.NOT_FOUND});
      }
      return res.status(httpStatus.OK).send(organizations);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Get list of all the users.
 */
router.get('/getAllUsers', (req, res, next) => {
  Users.getAllUsers()
    .then((user) => {
      if (!user) {
        return res.status(httpStatus.OK).send({message: 'Users ' + common.message.NOT_FOUND});
      }
      return res.status(httpStatus.OK).send(user);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Get all providers. Accepts userId as parameter if user is organization admin.
 */
router.get('/getAllProviders/:userId', (req, res, next) => {
  Users.getAllProviders(req.params.userId)
    .then((providers) => {
      if (!providers) {
        return res.status(httpStatus.OK).send({message: 'Users ' + common.message.NOT_FOUND});
      }
      return res.status(httpStatus.OK).send(providers);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Get user detail.
 */
router.post('/getUser', (req, res, next) => {
  if (!req.body.username && !req.body.email && !req.body.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide username or email or id.'
    });
  }

  Users.getUser(req.body)
    .then((user) => {
      if (!user) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Users ' + common.message.NOT_FOUND});
      }

      return res.status(httpStatus.OK).send(user.safeModel());
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Check if username is already exists or not.
 */
router.get('/isUserAlreadyExist/:email', (req, res, next) => {
  if (!req.params.email) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide valid email.'
    });
  }

  Users.getUser({email: req.params.email})
    .then((user) => {
      return res.status(httpStatus.OK).send({isExists: !!user});
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Validate registration detail and create dynamic client on OP.
 */
router.post('/validateRegistrationDetail', (req, res, next) => {
  if (!req.body) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide details.'
    });
  }

  let providerInfo = req.body;
  if (!providerInfo.email) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide valid email.'
    });
  }
  if (!providerInfo.organizationName) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide valid organization name.'
    });
  }
  if (!providerInfo.discoveryUrl) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide valid discovery URL.'
    });
  }
  if (!providerInfo.redirectUrls) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide valid redirect URLs.'
    });
  }
  let discoveryJson = {};
  let clientJson = {};
  let oxdId = '';
  let oxdRequest = {};
  let registreUrl = '';
  return Organizations.getOrganizationByName(providerInfo.organizationName)
    .then((organization) => {
      let isExists = !!organization;
      if (isExists) {
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Organization is already exists.'
        }));
      }

      return Users.getUser({email: providerInfo.email});
    })
    .then((user) => {
      let isExists = !!user;
      if (isExists) {
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Email is already exists.'
        }));
      }

      return Providers.getProviderByUrl(providerInfo.discoveryUrl);
    })
    .then((provider) => {
      let isExists = !!provider;
      /*if (isExists) {
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Provider is already exists.'
        }));
      }*/
      const option = {
        method: 'GET',
        uri: providerInfo.discoveryUrl + '/.well-known/openid-configuration',
        resolveWithFullResponse: true
      };

      return request(option);
    })
    .then((response) => {
      if (!response) {
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Metadata not found from Discovery URL. Please check Discovery URL.'
        }));
      }

      try {
        discoveryJson = JSON.parse(response.body);
      } catch (exception) {
        console.log(exception.toString());
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Discovery URL is invalid. Please check and correct it.'
        }));
      }
      const client = {
        redirect_uris: providerInfo.redirectUrls,
        application_type: 'native',
        client_name: providerInfo.organizationName,
        token_endpoint_auth_method: 'client_secret_basic',
        scopes: discoveryJson.scopes_supported
      };

      const options = {
        method: 'POST',
        uri: discoveryJson.registration_endpoint,
        body: JSON.stringify(client),
        resolveWithFullResponse: true
      };

      return request(options);
    })
    .then((response) => {
      if (!response) {
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Unable to register client. Please try after some time.'
        }));
      }

      try {
        clientJson = JSON.parse(response.body);
      } catch (exception) {
        console.log(exception.toString());
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Unable to register client due to invalid response. Please try after some time.'
        }));
      }

      if (clientJson.error) {
        return Promise.reject(res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          message: clientJson.error_description
        }));
      }

      registreUrl = clientJson.redirect_uris.filter(function (value) {
        return value.indexOf('register.html') > 0
      });

      // register site in oxd
      oxdRequest = {
        authorization_redirect_uri: registreUrl[0],
        op_host: providerInfo.discoveryUrl,
        redirect_uris: clientJson.redirect_uris,
        scopes: clientJson.scopes,
        response_types: clientJson.response_types,
        client_id: clientJson.client_id,
        client_secret: clientJson.client_secret,
        client_token_endpoint_auth_method: clientJson.client_token_endpoint_auth_method,
        client_registration_client_uri: clientJson.registration_client_uri,
        access_token: clientJson.registration_access_token,
        port: process.env.OXD_PORT
      };
      oxd.Request = oxdRequest;
      return new Promise((resolve, reject) => {
        oxd.register_site(oxd.Request, (response) => {
          response = JSON.parse(response);
          if (response.status == 'ok')
            return resolve(response);

          return resolve(null);
        });
      });
    })
    .then((response) => {
      if (!response) {
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Failed to create oxd client'
        }));
      }
      oxdId = response.data.oxd_id;
      oxd.Request = {
        oxd_id: oxdId,
        acr_values: ['basic'],
        port: process.env.OXD_PORT
      };

      oxd.get_authorization_url(oxd.Request, (response) => {
        response = JSON.parse(response);
        if (response.status != 'ok') {
          return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Failed to get authorization url'
          });
        }
        const url = response.data.authorization_url;
        const state = url.substring(url.indexOf('state=') + 6, url.indexOf('nonce=') - 1);
        return res.status(httpStatus.OK).send({
          authEndpoint: url,
          state: state,
          token: oxdRequest.access_token,
          client_id: oxdRequest.client_id,
          oxd_id: oxdId,
        });
      });
    })
    .catch((err) => {
      if (!err.statusCode) {
        return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          err: err,
          message: common.message.INTERNAL_SERVER_ERROR
        });
      }

      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Register user, organization and provider. Adds user to gluu server using SCOM 2.0.
 */
router.post('/registerDetail', (req, res, next) => {
  // region Provider detail validation
  let providerInfo = req.body.providerInfo;
  if (!providerInfo) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide provider information.'
    });
  }
  if (!providerInfo.email) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide email.'
    });
  }
  if (!providerInfo.organizationName) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide organization name.'
    });
  }
  if (!providerInfo.discoveryUrl) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide provider name.'
    });
  }
// endregion

// region User detail validation
  let clientInfo = req.body.clientInfo;
  if (!clientInfo) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Client information not found. Please try after some time.'
    });
  }
  if (!clientInfo.code) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'User login code is not valid. Please try after some time.'
    });
  }
  if (!clientInfo.client_id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Client ID is not valid. Please try after some time.'
    });
  }
  if (!clientInfo.token) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Client token not found. Please try after some time.'
    });
  }
  if (!clientInfo.oxd_id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide oxd Id.'
    });
  }
// endregion
  let discoveryMetadata = {};
  let clientMetadata = {};
  let userInfo = null;
  let role = null;
  const option = {
    method: 'GET',
    uri: providerInfo.discoveryUrl + '/.well-known/openid-configuration',
    resolveWithFullResponse: true
  };
  return request.get(option)
    .then((response) => {
      if (!response) {
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Metadata not found from Discovery URL. Please check Discovery URL.'
        }));
      }

      try {
        discoveryMetadata = JSON.parse(response.body);
      } catch (exception) {
        console.log(exception.toString());
        return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Discovery URL is invalid. Please check and correct it.'
        }));
      }

      let clientRequestOptions = {
        method: 'GET',
        uri: discoveryMetadata.registration_endpoint,
        qs: {client_id: clientInfo.client_id},
        headers: {authorization: 'Bearer ' + clientInfo.token},
        resolveWithFullResponse: true
      };

      return request(clientRequestOptions);
    })
    .then((response) => {
      if (!response) {
        return Promise.reject(res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          message: common.message.INTERNAL_SERVER_ERROR
        }));
      }

      try {
        clientMetadata = JSON.parse(response.body);
      } catch (exception) {
        console.log(exception.toString());
        return Promise.reject(res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          message: common.message.INTERNAL_SERVER_ERROR
        }));
      }

      if (clientMetadata.error) {
        return Promise.reject(res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          message: clientMetadata.error_description
        }));
      }

      let data = {};
      data.discoveryMetadata = discoveryMetadata;
      data.client_id = clientMetadata.client_id;
      data.client_secret = clientMetadata.client_secret;
      data.redirect_uri = clientMetadata.redirect_uris[0];
      data.code = clientInfo.code;

      // Get access token for fetch user information
      oxd.Request = {
        oxd_id: clientInfo.oxd_id,
        code: clientInfo.code,
        state: clientInfo.state,
        port: process.env.OXD_PORT
      };

      return getUserClaims(clientInfo);
    })
    .then((claimsUserInfo) => {
      if (!claimsUserInfo || !claimsUserInfo.data.claims.email) {
        return Promise.reject(res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          message: common.message.INTERNAL_SERVER_ERROR
        }));
      }
      userInfo = claimsUserInfo.data.claims;
      return Roles.getRoleByName('orgadmin');
    })
    .then((frole) => {
      role = frole;
      // Add organization if user not selected existing one.
      let organizationInfo = {
        isApproved: false,
        name: providerInfo.organizationName,
        phoneNo: String(providerInfo.phoneNo),
        address: providerInfo.address,
        state: providerInfo.state,
        city: providerInfo.city,
        type: providerInfo.type,
        zipcode: providerInfo.zipcode,
        description: providerInfo.description
      };
      return Organizations.addOrganization(organizationInfo);
    })
    .then((organization) => {
      let data = {};
      data.organizationId = organization._id;
      data.personInfo = {};
      data.personInfo.email = providerInfo.email;
      data.personInfo.username = userInfo.email[0];
      data.personInfo.firstName = userInfo.given_name[0];
      data.personInfo.lastName = userInfo.family_name[0];
      data.personInfo.roleId = role._id;
      data.personInfo.isActive = true;
      data.providerInfo = {};
      data.providerInfo.name = providerInfo.organizationName;
      data.providerInfo.discoveryUrl = providerInfo.discoveryUrl;
      data.providerInfo.clientId = clientMetadata.client_id;
      data.providerInfo.clientSecret = clientMetadata.client_secret;
      data.providerInfo.oxdId = clientInfo.oxd_id;
      data.providerInfo.authorizationEndpoint = discoveryMetadata.authorization_endpoint;
      data.providerInfo.redirectUris = JSON.stringify(clientMetadata.redirect_uris);
      data.providerInfo.responseTypes = JSON.stringify(clientMetadata.response_types);
      data.providerInfo.isApproved = false;

      return addProviderAndUser(data, res);
    })
    .catch((err) => {
      if (!err.statusCode) {
        return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          err: err,
          message: common.message.INTERNAL_SERVER_ERROR
        });
      }
    });
});

/**
 * Take private key and data and return signed data
 * @param {string} privateKey - privateKey for encrypt data
 * @param {string} data - any string data
 * @return {string} signed data
 */
router.post('/encrypt', (req, res, next) => {
  try {
    if (!req.body.data) {
      return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Please enter data'});
    }
    if (!req.body.privateKey) {
      return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Please enter private key'});
    }
    const key = require('keypair')().private;
    let signedData = jwt.sign(req.body.data, req.body.privateKey, {algorithm: 'RS256'});
    return res.status(httpStatus.OK).send(signedData);
  } catch (err) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Not converted', err: err.message});
  }
});

function addProviderAndUser(data, res) {
  // Add provider
  data.providerInfo.organizationId = data.organizationId;
  return Providers.addProvider(data.providerInfo)
    .then((provider) => {
      // Add user
      data.personInfo.providerId = provider._id;
      data.personInfo.organizationId = data.organizationId;
      return Users.createUser(data.personInfo)
        .then((user) => {
          // region Adding user to SCIM.
          let userDetail =
            {
              'externalId': user._id,
              'userName': user.username,
              'name': {
                'givenName': user.firstName,
                'familyName': user.lastName
              },
              'displayName': user.firstName.concat(' ' + user.lastName).trim(),
              'emails': [{
                'value': user.email.toLowerCase(),
                'type': 'work',
                'primary': 'true'
              }
              ],
              'userType': 'OrgAdmin',
              'title': 'Organization Admin',
              'active': 'true',
              //'password': data.personInfo.password,
              'roles': [{
                'value': 'orgadmin'
              }],
              'role': 'orgadmin',
              'entitlements': [{
                'value': 'Access to manage organization and provider added by user.'
              }],
              'meta': {
                'created': user.createdOn,
                'lastModified': user.createdOn,
                'version': user.__v,
                'location': ''
              }
            };

          // send email
          const mailOption = {
            from: common.smtpConfig.auth.user, // sender address
            to: user.email.toLowerCase(), // list of receivers
            subject: 'Registration : Gluu Federation', // Subject line
            html: common.registrationEmailTemplate.format(user.name, data.providerInfo.clientId) // html body
          };

          //Message.sendEmail(mailOption);

          return res.status(httpStatus.OK).send(user);
          // scim.addUser(userDetail).then(function (data) {
          //     return updateScimId(data.id, user._id, provider._id, data.organizationId, res);
          // }).catch(function (error) {
          //     console.log(error);
          //     return deleteUserProviderAndOrg(user._id, provider._id, data.organizationId, res);
          // });
          // endregion
        })
        .catch((err) => {
          return deleteProviderAndOrg(provider._id, data.organizationId, res);
        });
    })
    .catch((err) => {
      return deleteOrganization(data.organizationId, res);
    });
}

function deleteOrganization(orgId, res) {
  Organizations.removeOrganization(orgId)
    .then((organization) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        message: common.message.INTERNAL_SERVER_ERROR
      });
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
}

function deleteProviderAndOrg(providerId, orgId, res) {
  Providers.removeProvider(providerId)
    .then((emptyProvider) => {
      return deleteOrganization(orgId, res);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
}

function deleteUserProviderAndOrg(userId, providerId, orgId, res) {
  Users.removeUser(userId)
    .then((emptyUser) => {
      return deleteProviderAndOrg(providerId, orgId, res);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
}

function updateScimId(scimId, userId, providerId, orgId, res) {
  Users.updateScimId(userId, scimId)
    .then((user) => {
      return res.status(httpStatus.OK).send(user);
    })
    .catch((err) => {
      return deleteUserProviderAndOrg(userId, providerId, orgId, res);

      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
}

function getUserClaims(clientInfo) {
  oxd.Request = {
    oxd_id: clientInfo.oxd_id,
    code: clientInfo.code,
    state: clientInfo.state,
    port: process.env.OXD_PORT
  };

  const tokenPromise = new Promise((resolve, reject) => {
    oxd.get_tokens_by_code(oxd.Request, (response) => {
      response = JSON.parse(response);
      if (response.status != 'ok') {
        resolve(null);
      }
      resolve(response);
    });
  });

  return tokenPromise.then((response) => {
    if (!response) {
      return Promise.resolve(null);
    }

    oxd.Request = {
      oxd_id: clientInfo.oxd_id,
      access_token: response.data.access_token,
      port: process.env.OXD_PORT
    };

    return new Promise((resolve, reject) => {
      oxd.get_user_info(oxd.Request, (response) => {
        response = JSON.parse(response);
        if (response.status != 'ok') {
          return resolve(null);
        }
        return resolve(response);
      });
    });
  });
}

//string format prototype
String.prototype.format = function () {
  var formatted = this;
  for (var i = 0; i < arguments.length; i++) {
    var regexp = new RegExp('\\{{' + i + '\\}}', 'gi');
    formatted = formatted.replace(regexp, arguments[i]);
  }
  return formatted;
};

module.exports = router;