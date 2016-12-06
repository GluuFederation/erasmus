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

    if (!req.body.username)
        return res.status(409).send({
            'message': 'Please provide username.'
        });

    if (!req.body.password)
        return res.status(409).send({
            'message': 'Please provide password.'
        });

    Users.authenticateUser(req.body.username, req.body.password, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(401).send(info);
        }

        //Creating a JWT token for user session which is valid for 24 hrs.
        let token = jwt.sign(user, process.env.APP_SECRET, {
            expiresIn: process.env.JWT_EXPIRES_IN
        });
        return res.status(200).send({
            user,
            token
        });

    });
});

// =============================================================================
// Remove User =================================================================
// =============================================================================

router.delete('/removeUser/:username', (req, res, next) => {
    if (!req.params.username) {
        return res.status(409).send({
            'message': 'Please provide username.'
        });
    }

    Users.removeUser(req.params.username, (err, user, info) => {
        if (err) {
            return next(err);
        }
        if (!user) {
            return res.status(401).send(info);
        }

        return res.status(200).send(user);
    });
});

// =============================================================================
// SIGNUP (USER REGISTRATION) ==================================================
// =============================================================================
router.post('/signup', (req, res, next) => {
    if (!req.body.username)
        return res.status(409).send({
            'message': 'Please provide username.'
        });

    if (!req.body.email)
        return res.status(409).send({
            'message': 'Please provide email.'
        });

    if (!req.body.password)
        return res.status(409).send({
            'message': 'Please provide password.'
        });

    Users.createUser(req.body.username, req.body.email, req.body.password, req.body.firstName, req.body.lastName, (err, user, info) => {
        if (err) {
            return res.status(500).send({
                'message': err.message
            });
        }
        if (!user) {
            return res.status(401).send(info);
        }

        return res.status(200).send(user);
    });
});

// =============================================================================
// UPDATE USER DATA ============================================================
// =============================================================================
router.post('/updateUser', (req, res, next) => {
    console.log(req.body);
    if (!req.body.username)
        return res.status(409).send({
            'message': 'Please provide username.'
        });

    if (!req.body.email)
        return res.status(409).send({
            'message': 'Please provide email.'
        });

    Users.updateUser(req.body.username, req.body.email, req.body.firstName, req.body.lastName, (err, user, info) => {
        if (err) {
            return res.status(500).send({
                'message': err.message
            });
        }
        if (!user) {
            return res.status(401).send(info);
        }

        return res.status(200).send(user);
    });
});

// =============================================================================
// GET ALL USERS ===========================================================
// =============================================================================
router.get('/getAllUsers', (req, res, next) => {

    Users.getAllUsers((err, user, info) => {
        if (err) {
            return next(err);
        }
        if (info) {
            return res.status(200).send(JSON.stringify({data: [], message: info}));
        } else {
            return res.status(200).send(user);
        }
    });
});

module.exports = router;
