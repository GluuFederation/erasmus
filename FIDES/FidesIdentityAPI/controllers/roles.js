"use strict";

// load all the things we need
const express = require('express'),
    router = express.Router(),
    Roles = require('../helpers/roles');

// =============================================================================
// GET ALL ROLES ===============================================================
// =============================================================================
router.get('/getAllRoles', (req, res, next) => {
    Roles.getAllRoles((err, role, info) => {
        if (err) {
            console.log("err: " + err);
            return next(err);
        }
        if (!role) {
            console.log("info: " + info);
            return res.status(200).send(JSON.stringify({data: [], message: info}));
        }
        console.log("role: " + JSON.stringify(role));
        return res.status(200).send(role);
    });
});

module.exports = router;