"use strict";

const User = require('../models/user'),
    Roles = require('../helpers/roles');

/**
 * Callback function for all the export functions.
 * @callback requestCallback
 * @param {Error} error - Error information from base function.
 * @param {Object} [data] - Data from base function.
 * @param {Object} [info] - Message from base function if object not found.
 */

/**
 * Authenticate user for login.
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let authenticateUser = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();
    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.username}]
        }).populate('role organization provider').exec(function (err, user) {
            if (err)
                return done(err);

            if (!user) {
                return done(null, false, {
                    'message': 'Incorrect username or password.'
                });
            }
            if (!user.validPassword(req.password)) {
                return done(null, false, {
                    'message': 'Incorrect username or password.'
                });
            } else {
                return done(null, user);
            }
        });
    });
};

/**
 * Add user. (TODO: Add detail to SCIM)
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let createUser = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();
    if (req.email)
        req.email = req.email.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.email}]
        }, (err, user) => {
            if (err)
                return done(err);

            // check to see if there is already a user with that username/email
            if (!!user) {
                return done(null, false, {
                    'message': 'That username/email is already taken.'
                });
            } else {
                // create the user
                let newUser = new User();

                newUser.username = req.username;
                newUser.email = req.email;
                //newUser.password = newUser.generateHash(req.password);
                newUser.firstName = req.firstName;
                newUser.lastName = req.lastName;
                newUser.role = req.roleId;
                newUser.organization = req.organizationId;
                newUser.provider = req.providerId;
                newUser.isActive = req.isActive || false;

                newUser.save(err => {
                    if (err)
                        return done(err);

                    User.populate(newUser, 'role organization', function (err, newUser) {
                        if (err)
                            return done(err);

                        return done(null, newUser);
                    });
                });
            }
        });
    });
};

/**
 * Update user detail. (TODO: update detail to SCIM)
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let updateUser = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();
    if (req.email)
        req.email = req.email.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.email}]
        }).populate('role organization provider').exec(function (err, user) {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!user) {
                return done(null, false, {
                    'message': 'User not found with that username/email.'
                });
            } else {
                // if(password){
                //     user.password = user.generateHash(req.password);
                // }

                user.firstName = req.firstName;
                user.lastName = req.lastName;
                user.role = req.roleId;
                user.organization = req.organizationId;
                newUser.provider = req.providerId;
                user.isActive = req.isActive;

                user.save(err => {
                    if (err)
                        return done(err);

                    User.populate(user, 'role organization provider', function (err, user) {
                        if (err)
                            return done(err);

                        return done(null, user);
                    });
                });
            }
        });
    });
};

/**
 * Update user password. (TODO: update password in SCIM)
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let updatePassword = (req, done) => {
    process.nextTick(() => {
        User.findOne({
            '_id': req.id
        }).populate('role organization provider').exec(function (err, user) {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!user) {
                return done(null, false, {
                    'message': 'User not found with that username/email.'
                });
            } else {
                if (!user.validPassword(req.currentPassword)) {
                    return done(null, false, {
                        'message': 'Oops! Wrong password.'
                    });
                } else {
                    user.password = user.generateHash(req.newPassword);
                    user.save(err => {
                        if (err)
                            return done(err);

                        return done(null, user);
                    });
                }
            }
        });
    });
};

/**
 * Update SCIM ID of user after adding user to SCIM.
 * @param {string} userId - Id of user
 * @param {String} scimId - SCIM ID of user, returned after adding user to server using SCIM 2.0.
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let updateScimId = (userId, scimId, done) => {
    process.nextTick(() => {
        User.findOne({
            '_id': userId
        }).populate('role organization provider').exec(function (err, user) {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!user) {
                return done(null, false, {
                    'message': 'User not found with that username/email.'
                });
            } else {
                user.scimId = scimId;
                user.save(err => {
                    if (err)
                        return done(err);

                    return done(null, user);
                });
            }
        });
    });
};

/**
 * Remove user. (TODO: update detail to SCIM)
 * @param {string} id - id of user
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let removeUser = (id, done) => {
    process.nextTick(() => {
        User.findOne({
            '_id': id
        }, (err, user) => {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!user) {
                return done(null, false, {
                    'message': 'User not found with that username.'
                });
            } else {
                user.remove(err => {
                    if (err)
                        return done(err);

                    return done(null, user);
                });
            }
        });
    });
};

/**
 * Get user detail.
 * @param {object} req - Request json object
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getUser = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();
    if (req.email)
        req.email = req.email.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.email}, {'_id': req.id}]
        }).populate('role organization provider').exec(function (err, user) {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!user) {
                return done(null, false, {
                    'message': 'User not found with that username/email.'
                });
            } else {
                return done(null, user);
            }
        });
    });
};

/**
 * Get list of all the users.
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getAllUsers = (done) => {
    process.nextTick(() => {
        Roles.getRoleByName('admin', (err, role, info) => {
            if (err) {
                return next(err);
            }
            if (!role) {
                return res.status(200).send(JSON.stringify({data: [], message: info}));
            }

            User.find({role: {$ne: role._id}})
                .populate('role organization provider')
                .exec(function (err, users) {
                    if (err) {
                        return done(err);
                    }
                    if (users.length) {
                        return done(null, users);
                    }
                    return done(null, null, 'No records found');
                });
        });
    });
};

/**
 * Get list of all the organizations.
 * @param {requestCallback} done - Callback function that returns error, object or info
 */
let getAllOrganizations = (done) => {
    process.nextTick(() => {
        Roles.getRoleByName('admin', (err, role, info) => {
            if (err) {
                return next(err);
            }
            if (!role) {
                return res.status(200).send(JSON.stringify({data: [], message: info}));
            }

            User.find({role: {$ne: role._id}}, 'organization')
                .populate('organization')
                .exec(function (err, users) {
                    if (err) {
                        return done(err);
                    }
                    if (users.length) {
                        let organizations = [];
                        users.forEach(function (user) {
                            organizations.push(user.organization);
                        });
                        return done(null, organizations);
                    }
                    return done(null, null, 'No records found');
                });
        });
    });
};

/**
 * Retrieves all providers.
 * @param {ObjectId} userId - Org admin user id.
 * @param {requestCallback} done - Callback function that returns error, object or info.
 */
let getAllProviders = (userId, done) => {
    Roles.getRoleByName('admin', (err, role, info) => {
        if (err) {
            return next(err);
        }
        if (!role) {
            return res.status(200).send(JSON.stringify({data: [], message: info}));
        }
        let queryCondition = {};

        queryCondition['role'] = {$ne: role._id};
        if (userId && userId !== 'undefined') {
            queryCondition['_id'] = userId;
        }

        User.find(queryCondition, 'provider organization')
            .populate('provider organization')
            .exec(function (err, users) {
                if (err) {
                    return done(err);
                }
                if (users.length) {
                    let providers = [];
                    users.forEach(function (user) {
                        let provider = user.provider;
                        provider.organization = user.organization;
                        providers.push(provider);
                    });
                    return done(null, providers);
                }
                return done(null, null, 'No records found');
            });
    });
};

module.exports = {
    createUser,
    authenticateUser,
    getUser,
    getAllUsers,
    getAllOrganizations,
    getAllProviders,
    updateUser,
    updatePassword,
    updateScimId,
    removeUser
};
