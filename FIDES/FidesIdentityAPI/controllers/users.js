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
    jwt = require('jsonwebtoken'),
    Users = require('../helpers/users'),
    Roles = require('../helpers/roles'),
    Organizations = require('../helpers/organizations'),
    Providers = require('../helpers/providers');

/**
 * Authenticate user for login. (TODO: Authenticate using SCIM 2.0)
 */
router.post('/login', (req, res, next) => {

    if (!req.body.username) {
        return res.status(406).send({
            'message': 'Please provide username.'
        });
    }

    if (!req.body.password) {
        return res.status(406).send({
            'message': 'Please provide password.'
        });
    }

    Users.authenticateUser(req.body, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(406).send(info);
        }

        //Creating a JWT token for user session which is valid for 24 hrs.
        let token = jwt.sign(user, process.env.APP_SECRET, {
            expiresIn: process.env.JWT_EXPIRES_IN
        });

        return res.status(200).send({
            user: user.safeModel(),
            role: user.role.name,
            token
        });

    });
});

/**
 * Remove user. (TODO: update detail to SCIM)
 */
router.delete('/removeUser/:username', (req, res, next) => {
    if (!req.params.username) {
        return res.status(406).send({
            'message': 'Please provide username.'
        });
    }

    Users.removeUser(req.params.username, (err, user, info) => {
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
    if (!req.body.username) {
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
router.get('/isUserAlreadyExist', (req, res, next) => {
    if (!req.query.username && !req.query.email) {
        return res.status(406).send({
            'message': 'Please provide username or email.'
        });
    }

    Users.getUser(req.query, (err, user) => {
        if (err) {
            return next(err);
        }

        return res.status(200).send({isExists: !!user});
    });
});

/**
 * Register user, organization and provider. Adds user to gluu server using SCOM 2.0.
 */
router.post('/registerDetail', (req, res, next) => {
    // region User detail validation
    if (!req.body.personInfo) {
        return res.status(406).send({
            'message': 'Please provide person information.'
        });
    }
    if (!req.body.personInfo.username) {
        return res.status(406).send({
            'message': 'Please provide username.'
        });
    }
    if (!req.body.personInfo.email) {
        return res.status(406).send({
            'message': 'Please provide email.'
        });
    }
    if (!req.body.personInfo.password) {
        return res.status(406).send({
            'message': 'Please provide password.'
        });
    }
    if (!req.body.personInfo.firstName) {
        return res.status(406).send({
            'message': 'Please provide first name.'
        });
    }
    // endregion

    // region Organization detail validation
    if (!req.body.organizationInfo) {
        return res.status(406).send({
            'message': 'Please provide organization information.'
        });
    }
    if (!req.body.organizationInfo.organizationId && !req.body.organizationInfo.name) {
        return res.status(406).send({
            'message': 'Please provide organization name.'
        });
    }
    // endregion

    // region Provider detail validation
    if (!req.body.providerInfo) {
        return res.status(406).send({
            'message': 'Please provide provider information.'
        });
    }
    if (!req.body.providerInfo.name) {
        return res.status(406).send({
            'message': 'Please provide provider name.'
        });
    }
    if (!req.body.providerInfo.url) {
        return res.status(406).send({
            'message': 'Please provide provider URL.'
        });
    }
    if (!req.body.providerInfo.clientId) {
        return res.status(406).send({
            'message': 'Please provide client ID.'
        });
    }
    if (!req.body.providerInfo.clientSecret) {
        return res.status(406).send({
            'message': 'Please provide client secret.'
        });
    }
    // endregion

    Roles.getRoleByName('orgadmin', (err, role, info) => {
        if (err || info) {
            console.log(err || info);
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }

        // Add organization if user not selected existing one.
        req.body.organizationInfo.isApproved = false;
        if (req.body.organizationInfo.name) {
            Organizations.addOrganization(req.body.organizationInfo, (err, organization, info) => {
                if (err || info) {
                    console.log(err || info);
                    return res.status(500).send({
                        'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                    });
                }

                return AddUserAndProvider(req, res, role._id, organization._id, true);
            });
        } else if (req.body.organizationInfo.organizationId) {
            Organizations.getOrganizationById(req.body.organizationInfo.organizationId, (err, organization, info) => {
                if (err || info) {
                    console.log(err || info);
                    return res.status(500).send({
                        'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                    });
                }

                return AddUserAndProvider(req, res, role._id, organization._id, false);
            });
        }
    });

    function AddUserAndProvider(req, res, roleId, orgId, isNewOrg) {
        // Add user
        req.body.personInfo.roleId = roleId;
        req.body.personInfo.organizationId = orgId;
        req.body.personInfo.isActive = true;
        Users.createUser(req.body.personInfo, (err, user, info) => {
            if (err || info) {
                console.log(err || info);
                return deleteOrganization(orgId, isNewOrg, res);
            }

            // Add provider
            req.body.providerInfo.createdBy = user._id;
            req.body.providerInfo.organizationId = orgId;
            req.body.providerInfo.isApproved = false;
            Providers.addProvider(req.body.providerInfo, (err, provider, info) => {
                if (err || info) {
                    console.log(err || info);
                    return deleteUserAndOrg(user.username, orgId, isNewOrg, res);
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
                        'password': req.body.personInfo.password,
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
                    return updateScimId(data.id, provider._id, user.username, orgId, isNewOrg, res);
                }).catch(function (error) {
                    console.log(error);
                    return deleteProviderUserAndOrg(provider._id, user.username, orgId, isNewOrg, res);
                });
                // endregion
            });
        });
    }

    function deleteOrganization(orgId, isNewOrg, res) {
        if (isNewOrg === true) {
            Organizations.removeOrganization(orgId, (err, organization, info) => {
                if (err || info) {
                    console.log(err || info);
                }

                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            });
        }
    }

    function deleteUserAndOrg(username, orgId, isNewOrg, res) {
        Users.removeUser(username, (err, emptyUser, info) => {
            if (err || info) {
                console.log(err || info);
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            return deleteOrganization(orgId, isNewOrg, res);
        });
    }

    function deleteProviderUserAndOrg(providerId, username, orgId, isNewOrg, res) {
        Providers.removeProvider(providerId, (err, emptyProvider, info) => {
            if (err || info) {
                console.log(err || info);
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            return deleteUserAndOrg(username, orgId, isNewOrg, res);
        });
    }

    function updateScimId(scimId, providerId, username, orgId, isNewOrg, res) {
        Users.updateScimId(username.toLowerCase(), scimId, (err, user, info) => {
            if (err || info) {
                return deleteProviderUserAndOrg(providerId, username, orgId, isNewOrg, res);
            }

            return res.status(200).send(user);
        });
    }
});

module.exports = router;
