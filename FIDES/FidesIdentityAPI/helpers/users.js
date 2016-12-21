"use strict";

// load up the user model
const User = require('../models/user');

// =========================================================================
// Authenticates a user ====================================================
// =========================================================================
let authenticateUser = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();
    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.username}]
        }).populate('role organization').exec(function (err, user) {
            if (err)
                return done(err);

            if (!user) {
                return done(null, false, {
                    'message': 'No user found.'
                });
            }
            if (!user.validPassword(req.password)) {
                return done(null, false, {
                    'message': 'Oops! Wrong password.'
                });
            } else {
                return done(null, user);
            }
        });
    });
};

// =============================================================================
// Creates a new user ==========================================================
// =============================================================================
let createUser = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.email}]
        }, (err, user) => {
            if (err)
                return done(err);

            // check to see if there is already a user with that username/email
            if (user) {
                return done(null, false, {
                    'message': 'That username/email is already taken.'
                });
            } else {
                // create the user
                let newUser = new User();

                newUser.username = req.username;
                newUser.email = req.email;
                newUser.password = newUser.generateHash(req.password);
                newUser.firstName = req.firstName;
                newUser.lastName = req.lastName;
                newUser.role = req.roleId;
                newUser.organization = req.organizationId;

                newUser.save(err => {
                    if (err)
                        return done(err);

                    User.populate(newUser, 'role organization', function (err, newUser) {
                        if (err)
                            return done(err);

                        console.log(newUser);
                        return done(null, newUser);
                    });
                });
            }
        });
    });
};

// =============================================================================
// Update user =================================================================
// =============================================================================
let updateUser = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.email}]
        }).populate('role organization').exec(function (err, user) {
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

                user.save(err => {
                    if (err)
                        return done(err);

                    /*console.log(user);
                    return done(null, user);*/

                    User.populate(user, 'role organization', function (err, user) {
                        if (err)
                            return done(err);

                        console.log(user);
                        return done(null, user);
                    });
                });
            }
        });
    });
};

// =============================================================================
// Update password =============================================================
// =============================================================================
let updatePassword = (req, done) => {
    if (req.username)
        req.username = req.username.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': req.username}, {'email': req.username}]
        }).populate('role organization').exec(function (err, user) {
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

// =============================================================================
// Remove user =================================================================
// =============================================================================
let removeUser = (username, done) => {
    if (username)
        username = username.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            'username': username
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

// =============================================================================
// Retrieves all user ==========================================================
// =============================================================================
let getAllUsers = (done) => {
    process.nextTick(() => {
        User.find().sort({
            firstName: 1
        }).select('-password').populate('role organization')
            .exec(function (err, users) {
                if (err) {
                    done(err);
                } else if (users.length) {
                    done(null, users);
                } else {
                    done(null, null, 'No records found');
                }
            });
    });
};

module.exports = {
    createUser,
    authenticateUser,
    getAllUsers,
    updateUser,
    updatePassword,
    removeUser
};
