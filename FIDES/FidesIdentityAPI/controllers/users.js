"use strict";

// load all the things we need
const express = require('express'),
    router = express.Router(),
    jwt = require('jsonwebtoken'),
    Users = require('../helpers/users');

// =============================================================================
// AUTHENTICATE (FIRST LOGIN) ==================================================
// =============================================================================
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

        delete user._doc.password;
        return res.status(200).send({
            user,
            role: user.role.name,
            token
        });

    });
});

// =============================================================================
// Remove User =================================================================
// =============================================================================
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

// =============================================================================
// SIGNUP (USER REGISTRATION) ==================================================
// =============================================================================
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

        delete user._doc.password;
        return res.status(200).send(user);
    });
});

// =============================================================================
// UPDATE USER PASSWORD ========================================================
// =============================================================================
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

        //delete user._doc.password;
        return res.status(200).send(user.safeModel());
    });
});

// =============================================================================
// UPDATE USER DATA ============================================================
// =============================================================================
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

        delete user._doc.password;
        return res.status(200).send(user);
    });
});

// =============================================================================
// GET ALL USERS ===============================================================
// =============================================================================
router.get('/getAllUsers', (req, res, next) => {
    Users.getAllUsers((err, user, info) => {
        if (err) {
            return next(err);
        }
        if (info) {
            return res.status(200).send(JSON.stringify({data: [], info}));
        } else {
            return res.status(200).send(user);
        }
    });
});

// =============================================================================
// GET USER DATA ===============================================================
// =============================================================================
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

        delete user._doc.password;
        return res.status(200).send(user);
    });
});

// =============================================================================
// Check whether user exists or not ============================================
// =============================================================================
router.get('/isUserAlreadyExist', (req, res, next) => {
    if (!req.query.username && !req.query.email) {
        return res.status(406).send({
            'message': 'Please provide username or email.'
        });
    }

    Users.getUser(req.query, (err, user, info) => {
        if (err) {
            return next(err);
        }

        return res.status(200).send({isExists: !!user});
    });
});

// =============================================================================
// Register user, organization and provider ====================================
// =============================================================================
router.post('/registerDetail', (req, res, next) => {
    // User Detail
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

    // Organization Detail
    if (!req.body.organizationInfo) {
        return res.status(406).send({
            'message': 'Please provide organization information.'
        });
    }
    if (!req.body.organizationInfo.organizationId && !req.body.organizationInfo.organizationName) {
        return res.status(406).send({
            'message': 'Please provide organization name.'
        });
    }

    //Provider Detail
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
    if (!req.body.providerInfo.discoveryUrl) {
        return res.status(406).send({
            'message': 'Please provide provider discovery URL.'
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

    return res.status(200).send(req.body);

    Users.createUser(req.body, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(406).send(info);
        }

        delete user._doc.password;
        return res.status(200).send(user);
    });
});

module.exports = router;
