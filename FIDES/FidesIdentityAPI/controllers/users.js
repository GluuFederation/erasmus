"use strict";

var scimConfig = {
    keyAlg: 'RS256',
    domain: 'https://gluu.local.org/',
    privateKey: 'scim-rp-sample.key',
    clientId: '@!FFDB.955F.C09D.E9FB!0001!2A77.8551!0008!C1BA.63D6',
    keyId: '63eed5a0-f1ea-42cb-bd21-5d904b0b894d'
};
var scim = require('scim-node')(scimConfig);

const express = require('express'),
    router = express.Router(),
    request = require('request'),
    jwt = require('jsonwebtoken'),
    Users = require('../helpers/users'),
    Roles = require('../helpers/roles'),
    Organizations = require('../helpers/organizations'),
    Providers = require('../helpers/providers');

/**
 * Validate user email before login.
 */
router.post('/validateEmail', (req, res, next) => {
    if (!req.body.email) {
        return res.status(406).send({
            'message': 'Please provide email.'
        });
    }

    Users.getUser(req.body, (err, user) => {
        if (err) {
            return next(err);
        }

        if (!user) {
            return res.status(406).send({
                'message': 'User not found with this email.'
            });
        }

        if (!user.provider) {
            return res.status(406).send({
                'message': 'There is no provider associated with the user.'
            });
        }

        let provider = user.provider;
        let state = generateString(16);
        let redirectUri = JSON.parse(provider.redirectUris)[1];
        let responseType = JSON.parse(provider.responseTypes)[0];
        let authEndpoint = provider.authorizationEndpoint.concat('?redirect_uri=').concat(redirectUri).concat('&client_id=').concat(provider.clientId).concat('&response_type=').concat(responseType).concat('&state=').concat(state).concat('&scope=').concat('openid email');
        return res.status(200).send({
            authEndpoint: authEndpoint,
            email: req.body.email,
            redirectUri: redirectUri,
            state: state
        });
    });
});

/**
 * Authenticate user for login. (TODO: Compare entered email and OP login email)
 */
router.post('/login', (req, res, next) => {
    if (!req.body.email) {
        return res.status(406).send({
            'message': 'Please provide email.'
        });
    }

    Users.getUser(req.body, (err, user) => {
        if (err) {
            return next(err);
        }

        if (!user) {
            return res.status(406).send({
                'message': 'User not found with this email.'
            });
        }

        if (!user.provider) {
            return res.status(406).send({
                'message': 'There is no provider associated with the user.'
            });
        }

        request.get(user.provider.discoveryUrl, function (error, response, body) {
            if (error) {
                console.log(error);
                return res.status(406).send({
                    'message': 'Unable to fetch metadata from Discovery URL. Please check Discovery URL.'
                });
            }

            if (!response) {
                return res.status(406).send({
                    'message': 'Metadata not found from Discovery URL. Please check Discovery URL.'
                });
            }

            let discoveryMetadata = {};
            try {
                discoveryMetadata = JSON.parse(body);
            } catch (exception) {
                console.log(exception.toString());
                return res.status(406).send({
                    'message': 'Discovery URL is invalid. Please check and correct it.'
                });
            }

            let data = {};
            data.discoveryMetadata = discoveryMetadata;
            data.client_id = user.provider.clientId;
            data.client_secret = user.provider.clientSecret;
            data.redirect_uri = req.body.redirectUri;
            data.code = req.body.code;
            getUserClaims(data, res, function (error, userInfo) {
                if (error) {
                    console.log(error);
                    return res.status(406).send({
                        'message': 'Unable to fetch metadata from Discovery URL. Please check Discovery URL.'
                    });
                }

                if (!userInfo) {
                    return res.status(500).send({
                        'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                    });
                }

                if (!userInfo.email || userInfo.email.toLowerCase() !== req.body.email.toLowerCase()) {
                    return res.status(500).send({
                        'message': 'Email did not matched. Please check email address or logout from provider first and try again.'
                    });
                }

                //Creating a JWT token for user session which is valid for 24 hrs.
                let token = jwt.sign(user, process.env.APP_SECRET, {
                    expiresIn: process.env.JWT_EXPIRES_IN
                });

                return res.status(200).send({
                    user: user.safeModel(),
                    role: user.role.name,
                    token: token
                });
            });
        });
    });
});

/**
 * Remove user. (TODO: update detail to SCIM)
 */
router.delete('/removeUser/:id', (req, res, next) => {
    if (!req.params.id) {
        return res.status(406).send({
            'message': 'Please provide username.'
        });
    }

    Users.removeUser(req.params.id, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(406).send(info);
        }

        return res.status(200).send(user);
    });
});

/**
 * Add user. (TODO: Add detail to SCIM)
 */
router.post('/signup', (req, res, next) => {
    if (!req.body.username) {
        return res.status(406).send({
            'message': 'Please provide username.'
        });
    }

    if (!req.body.email) {
        return res.status(406).send({
            'message': 'Please provide email.'
        });
    }

    if (!req.body.password) {
        return res.status(406).send({
            'message': 'Please provide password.'
        });
    }

    if (!req.body.roleId) {
        return res.status(406).send({
            'message': 'Please provide at least one role.'
        });
    }

    Users.createUser(req.body, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(406).send(info);
        }

        return res.status(200).send(user.safeModel());
    });
});

/**
 * Update user password. (TODO: update password in SCIM)
 */
router.post('/updatePassword', (req, res, next) => {
    if (!req.body.id) {
        return res.status(406).send({
            'message': 'Please provide username.'
        });
    }

    if (!req.body.currentPassword) {
        return res.status(406).send({
            'message': 'Please provide current password.'
        });
    }

    if (!req.body.newPassword) {
        return res.status(406).send({
            'message': 'Please provide new password.'
        });
    }

    Users.updatePassword(req.body, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(406).send(info);
        }

        return res.status(200).send(user.safeModel());
    });
});

/**
 * Update user detail. (TODO: update detail to SCIM)
 */
router.post('/updateUser', (req, res, next) => {
    if (!req.body.username) {
        return res.status(406).send({
            'message': 'Please provide username.'
        });
    }

    if (!req.body.email) {
        return res.status(406).send({
            'message': 'Please provide email.'
        });
    }

    if (!req.body.roleId) {
        return res.status(406).send({
            'message': 'Please provide role.'
        });
    }

    Users.updateUser(req.body, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(406).send(info);
        }

        return res.status(200).send(user.safeModel());
    });
});

/**
 * Get list of all the organizations.
 */
router.get('/getAllOrganizations', (req, res, next) => {
    Users.getAllOrganizations((err, organizations, info) => {
        if (err) {
            return next(err);
        }
        if (info) {
            return res.status(200).send(info);
        }

        return res.status(200).send(organizations);
    });
});

/**
 * Get list of all the users.
 */
router.get('/getAllUsers', (req, res, next) => {
    Users.getAllUsers((err, user, info) => {
        if (err) {
            return next(err);
        }
        if (info) {
            return res.status(200).send(info);
        }

        return res.status(200).send(user);
    });
});

/**
 * Get all providers. Accepts userId as parameter if user is organization admin.
 */
router.get('/getAllProviders/:userId', (req, res, next) => {
    Users.getAllProviders(req.params.userId, (err, providers, info) => {
        if (err) {
            return next(err);
        }
        if (info) {
            return res.status(200).send(info);
        }

        return res.status(200).send(providers);
    });
});

/**
 * Get user detail.
 */
router.post('/getUser', (req, res, next) => {
    if (!req.body.username && !req.body.email && !req.body.id) {
        return res.status(406).send({
            'message': 'Please provide username or email or id.'
        });
    }

    Users.getUser(req.body, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(406).send(info);
        }

        return res.status(200).send(user.safeModel());
    });
});

/**
 * Check if username is already exists or not.
 */
router.get('/isUserAlreadyExist/:email', (req, res, next) => {
    if (!req.params.email) {
        return res.status(406).send({
            'message': 'Please provide valid email.'
        });
    }

    Users.getUser(req.params.email, (err, user) => {
        if (err) {
            return next(err);
        }

        return res.status(200).send({isExists: !!user});
    });
});

/**
 * Validate registration detail and create dynamic client on OP.
 */
router.post('/validateRegistrationDetail', (req, res, next) => {
    if (!req.body) {
        return res.status(406).send({
            'message': 'Please provide details.'
        });
    }

    let providerInfo = req.body;
    if (!providerInfo.email) {
        return res.status(406).send({
            'message': 'Please provide valid email.'
        });
    }
    if (!providerInfo.organizationName) {
        return res.status(406).send({
            'message': 'Please provide valid organization name.'
        });
    }
    if (!providerInfo.discoveryUrl) {
        return res.status(406).send({
            'message': 'Please provide valid discovery URL.'
        });
    }
    if (!providerInfo.redirectUrls) {
        return res.status(406).send({
            'message': 'Please provide valid redirect URLs.'
        });
    }

    Organizations.getOrganizationByName(providerInfo.organizationName, (err, organization) => {
        if (err) {
            return next(err);
        }

        let isExists = !!organization;
        if (isExists) {
            return res.status(406).send({
                'message': 'Organization is already exists.'
            });
        }

        Users.getUser({email: providerInfo.email}, (err, user) => {
            if (err) {
                return next(err);
            }

            let isExists = !!user;
            if (isExists) {
                return res.status(406).send({
                    'message': 'Email is already exists.'
                });
            }

            Providers.getProviderByUrl(providerInfo.discoveryUrl, (err, provider) => {
                if (err) {
                    return next(err);
                }

                let isExists = !!provider;
                if (isExists) {
                    return res.status(406).send({
                        'message': 'Provider is already exists.'
                    });
                }

                request.get(providerInfo.discoveryUrl, function (error, response, body) {
                    if (error) {
                        console.log(error);
                        return res.status(406).send({
                            'message': 'Unable to fetch metadata from Discovery URL. Please check Discovery URL.'
                        });
                    }

                    let discoveryJson = {};
                    if (!response) {
                        return res.status(406).send({
                            'message': 'Metadata not found from Discovery URL. Please check Discovery URL.'
                        });
                    }

                    try {
                        discoveryJson = JSON.parse(body);
                    } catch (exception) {
                        console.log(exception.toString());
                        return res.status(406).send({
                            'message': 'Discovery URL is invalid. Please check and correct it.'
                        });
                    }

                    var client = {
                        redirect_uris: providerInfo.redirectUrls,
                        application_type: "native",
                        client_name: providerInfo.organizationName,
                        token_endpoint_auth_method: "client_secret_basic",
                        scopes: discoveryJson.scopes_supported
                    };

                    var options = {
                        method: 'POST',
                        url: discoveryJson.registration_endpoint,
                        body: JSON.stringify(client)
                    };

                    request(options, function (error, response, body) {
                        if (error || !response) {
                            console.log(error);
                            return res.status(406).send({
                                'message': 'Unable to register client. Please try after some time.'
                            });
                        }

                        var clientJson = {};
                        try {
                            clientJson = JSON.parse(body);
                        } catch (exception) {
                            console.log(exception.toString());
                            return res.status(406).send({
                                'message': 'Unable to register client due to invalid response. Please try after some time.'
                            });
                        }

                        if (clientJson.error) {
                            return res.status(500).send({
                                'message': clientJson.error_description
                            });
                        }

                        var state = generateString(16);
                        var authEndpoint = discoveryJson.authorization_endpoint.concat('?redirect_uri=').concat(clientJson.redirect_uris[0]).concat('&client_id=').concat(clientJson.client_id).concat('&response_type=').concat(clientJson.response_types[0]).concat('&state=').concat(state).concat('&scope=').concat('profile%20email%20openid');
                        return res.status(200).send({
                            authEndpoint: authEndpoint,
                            state: state,
                            client_id: clientJson.client_id,
                            token: clientJson.registration_access_token
                        });
                    });
                });
            });
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
        return res.status(406).send({
            'message': 'Please provide provider information.'
        });
    }
    if (!providerInfo.email) {
        return res.status(406).send({
            'message': 'Please provide email.'
        });
    }
    if (!providerInfo.organizationName) {
        return res.status(406).send({
            'message': 'Please provide organization name.'
        });
    }
    if (!providerInfo.discoveryUrl) {
        return res.status(406).send({
            'message': 'Please provide provider name.'
        });
    }
    // endregion

    // region User detail validation
    let clientInfo = req.body.clientInfo;
    if (!clientInfo) {
        return res.status(406).send({
            'message': 'Client information not found. Please try after some time.'
        });
    }
    if (!clientInfo.code) {
        return res.status(406).send({
            'message': 'User login code is not valid. Please try after some time.'
        });
    }
    if (!clientInfo.client_id) {
        return res.status(406).send({
            'message': 'Client ID is not valid. Please try after some time.'
        });
    }
    if (!clientInfo.token) {
        return res.status(406).send({
            'message': 'Client token not found. Please try after some time.'
        });
    }
    // endregion

    request.get(providerInfo.discoveryUrl, function (error, response, body) {
        if (error) {
            console.log(error);
            return res.status(406).send({
                'message': 'Unable to fetch metadata from Discovery URL. Please check Discovery URL.'
            });
        }

        let discoveryMetadata = {};
        if (!response) {
            return res.status(406).send({
                'message': 'Metadata not found from Discovery URL. Please check Discovery URL.'
            });
        }

        try {
            discoveryMetadata = JSON.parse(body);
        } catch (exception) {
            console.log(exception.toString());
            return res.status(406).send({
                'message': 'Discovery URL is invalid. Please check and correct it.'
            });
        }

        let clientRequestOptions = {
            method: 'GET',
            url: discoveryMetadata.registration_endpoint,
            qs: {client_id: clientInfo.client_id},
            headers: {authorization: 'Bearer ' + clientInfo.token}
        };

        request(clientRequestOptions, function (error, response, body) {
            if (error || !response) {
                console.log(error);
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            let clientMetadata = {};
            try {
                clientMetadata = JSON.parse(body);
            } catch (exception) {
                console.log(exception.toString());
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            if (clientMetadata.error) {
                return res.status(500).send({
                    'message': clientMetadata.error_description
                });
            }

            let data = {};
            data.discoveryMetadata = discoveryMetadata;
            data.client_id = clientMetadata.client_id;
            data.client_secret = clientMetadata.client_secret;
            data.redirect_uri = clientMetadata.redirect_uris[0];
            data.code = clientInfo.code;
            getUserClaims(data, res, function (error, userInfo) {
                if (error) {
                    console.log(error);
                    return res.status(406).send({
                        'message': 'Unable to fetch metadata from Discovery URL. Please check Discovery URL.'
                    });
                }

                if (!userInfo) {
                    return res.status(500).send({
                        'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                    });
                }

                Roles.getRoleByName('orgadmin', (err, role, info) => {
                    if (err || info) {
                        console.log(err || info);
                        return res.status(500).send({
                            'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                        });
                    }

                    // Add organization if user not selected existing one.
                    let organizationInfo = {};
                    organizationInfo.isApproved = false;
                    organizationInfo.name = providerInfo.organizationName;
                    Organizations.addOrganization(organizationInfo, (err, organization, info) => {
                        if (err || info) {
                            console.log(err || info);
                            return res.status(500).send({
                                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                            });
                        }

                        let data = {};
                        data.organizationId = organization._id;
                        data.personInfo = {};
                        data.personInfo.email = providerInfo.email;
                        data.personInfo.username = userInfo.username || providerInfo.email;
                        data.personInfo.firstName = userInfo.given_name;
                        data.personInfo.lastName = userInfo.family_name;
                        data.personInfo.roleId = role._id;
                        data.personInfo.isActive = true;
                        data.providerInfo = {};
                        data.providerInfo.name = providerInfo.organizationName;
                        data.providerInfo.discoveryUrl = providerInfo.discoveryUrl;
                        data.providerInfo.clientId = clientMetadata.client_id;
                        data.providerInfo.clientSecret = clientMetadata.client_secret;
                        data.providerInfo.authorizationEndpoint = discoveryMetadata.authorization_endpoint;
                        data.providerInfo.redirectUris = JSON.stringify(clientMetadata.redirect_uris);
                        data.providerInfo.responseTypes = JSON.stringify(clientMetadata.response_types);
                        data.providerInfo.isApproved = false;

                        return addProviderAndUser(data, res);
                    });
                });

                function addProviderAndUser(data, res) {
                    // Add provider
                    data.providerInfo.organizationId = data.organizationId;
                    Providers.addProvider(data.providerInfo, (err, provider, info) => {
                        if (err || info) {
                            console.log(err || info);
                            return deleteOrganization(data.organizationId, res);
                        }

                        // Add user
                        data.personInfo.providerId = provider._id;
                        data.personInfo.organizationId = data.organizationId;
                        Users.createUser(data.personInfo, (err, user, info) => {
                            if (err || info) {
                                console.log(err || info);
                                return deleteProviderAndOrg(provider._id, data.organizationId, res);
                            }

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
                                    }
                                    ],
                                    'entitlements': [{
                                        'value': 'Access to manage organization and provider added by user.'
                                    }
                                    ],
                                    'meta': {
                                        'created': user.createdOn,
                                        'lastModified': user.createdOn,
                                        'version': user.__v,
                                        'location': ''
                                    }
                                };

                            scim.addUser(userDetail).then(function (data) {
                                return updateScimId(data.id, user._id, provider._id, data.organizationId, res);
                            }).catch(function (error) {
                                console.log(error);
                                return deleteUserProviderAndOrg(user._id, provider._id, data.organizationId, res);
                            });
                            // endregion
                        });
                    });
                }

                function deleteOrganization(orgId, res) {
                    Organizations.removeOrganization(orgId, (err, organization, info) => {
                        if (err || info) {
                            console.log(err || info);
                        }

                        return res.status(500).send({
                            'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                        });
                    });
                }

                function deleteProviderAndOrg(providerId, orgId, res) {
                    Providers.removeProvider(providerId, (err, emptyProvider, info) => {
                        if (err || info) {
                            console.log(err || info);
                            return res.status(500).send({
                                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                            });
                        }

                        return deleteOrganization(orgId, res);
                    });
                }

                function deleteUserProviderAndOrg(userId, providerId, orgId, res) {
                    Users.removeUser(userId, (err, emptyUser, info) => {
                        if (err || info) {
                            console.log(err || info);
                            return res.status(500).send({
                                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                            });
                        }

                        return deleteProviderAndOrg(providerId, orgId, res);
                    });
                }

                function updateScimId(scimId, userId, providerId, orgId, res) {
                    Users.updateScimId(userId, scimId, (err, user, info) => {
                        if (err || info) {
                            return deleteUserProviderAndOrg(userId, providerId, orgId, res);
                        }

                        return res.status(200).send(user);
                    });
                }
            });
        });
    });
});

function getUserClaims(data, res, callback) {
    let authToken = new Buffer(data.client_id + ":" + data.client_secret).toString("base64");
    let tokenRequestOptions = {
        method: 'POST',
        url: data.discoveryMetadata.token_endpoint,
        headers: {
            'authorization': 'Basic ' + authToken,
            'content-type': 'application/x-www-form-urlencoded'
        },
        form: {
            grant_type: 'authorization_code',
            code: data.code,
            redirect_uri: data.redirect_uri
        }
    };

    request(tokenRequestOptions, function (error, response, body) {
        if (error || !response) {
            console.log(error);
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }

        let tokenMetadata = {};
        try {
            tokenMetadata = JSON.parse(body);
        } catch (exception) {
            console.log(exception.toString());
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }

        if (tokenMetadata.error) {
            return res.status(500).send({
                'message': tokenMetadata.error_description
            });
        }

        let userInfoOptions = {
            method: 'GET',
            url: data.discoveryMetadata.userinfo_endpoint,
            headers: {
                authorization: 'Bearer ' + tokenMetadata.access_token
            }
        };

        request(userInfoOptions, function (error, response, body) {
            if (error || !response) {
                console.log(error);
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            let userInfo = {};
            try {
                userInfo = JSON.parse(body);
            } catch (exception) {
                console.log(exception.toString());
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            if (userInfo.error) {
                return res.status(500).send({
                    'message': userInfo.error_description
                });
            }

            return callback(error, userInfo);
        });
    });
}

function generateString(length) {
    var chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
    var str = '';
    for (let i = 0; i < length; i++) {
        let rnum = Math.floor(Math.random() * chars.length);
        str += chars.substring(rnum, rnum + 1);
    }

    return str;
}

module.exports = router;
