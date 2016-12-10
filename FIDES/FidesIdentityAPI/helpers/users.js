"use strict";

// load up the user model
const User = require('../models/user');

// =========================================================================
// Authenticates a user ====================================================
// =========================================================================
let authenticateUser = (email, password, done) => {
    if (email)
        email = email.toLowerCase();
    process.nextTick(() => {
        User.findOne({
            $or: [{'username': email}, {'email': email}]
        }, (err, user) => {
            if (err)
                return done(err);

            if (!user) {
                return done(null, false, {
                    'message': 'No user found.'
                });
            }
            if (!user.validPassword(password)) {
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
let createUser = (username, email, password, firstName, lastName, done) => {
    if (username)
        username = username.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': username}, {'email': email}]
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

                newUser.username = username;
                newUser.email = email;
                newUser.password = newUser.generateHash(password);
                newUser.firstName = firstName;
                newUser.lastName = lastName;

                newUser.save(err => {
                    if (err)
                        return done(err);

                    return done(null, newUser);
                });
            }
        });
    });
};

// =============================================================================
// Update user =================================================================
// =============================================================================
let updateUser = (username, email, firstName, lastName, done) => {
    if (username)
        username = username.toLowerCase();

    process.nextTick(() => {
        User.findOne({
            $or: [{'username': username}, {'email': email}]
        }, (err, user) => {
            if (err)
                return done(err);

            // check to see if there is already exists or not.
            if (!user) {
                return done(null, false, {
                    'message': 'User not found with that username/email.'
                });
            } else {
                user.firstName = firstName;
                user.lastName = lastName;

                user.save(err => {
                    if (err)
                        return done(err);

                    return done(null, user);
                });
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
        }).select('-_id -password')
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
    removeUser
};
