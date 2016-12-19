"use strict";

// load all the things we need
const express = require('express'),
    router = express.Router(),
    Organizations = require('../helpers/organizations');

// =============================================================================
// GET ALL ROLES ===============================================================
// =============================================================================
router.get('/getAllOrganizations', (req, res, next) => {
    Organizations.getAllOrganizations((err, organization, info) => {
        if (err) {
            console.log("err: " + err);
            return next(err);
        }
        if (!organization) {
            console.log("info: " + info);
            return res.status(200).send(JSON.stringify({data: [], message: info}));
        }
        console.log("organization: " + JSON.stringify(organization));
        return res.status(200).send(organization);
    });
});

module.exports = router;