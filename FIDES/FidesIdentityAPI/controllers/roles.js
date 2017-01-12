"use strict";

const express = require('express'),
    router = express.Router(),
    Roles = require('../helpers/roles');

/**
 * Get all available roles.
 */
router.get('/getAllRoles', (req, res, next) => {
    Roles.getAllRoles((err, roles, info) => {
        if (err) {
            return next(err);
        }
        if (!roles) {
            return res.status(200).send(JSON.stringify({data: [], message: info}));
        }

        return res.status(200).send(roles);
    });
});

/**
 * Get role by name string.
 */
router.get('/getRoleByName/:name', (req, res, next) => {
    if (!req.params.name) {
        return res.name(406).send({
            'message': 'Please provide name.'
        });
    }

    Roles.getRoleByName(req.params.name, (err, role, info) => {
        if (err) {
            return next(err);
        }
        if (!role) {
            return res.status(200).send(JSON.stringify({data: [], message: info}));
        }

        return res.status(200).send(role);
    });
});

module.exports = router;