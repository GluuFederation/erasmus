"use strict";

// load all the things we need
const express = require('express'),
    router = express.Router(),
    jwt = require('jsonwebtoken'),
    Providers = require('../helpers/providers');


// =============================================================================
// REGISTRATION (OPENID CONNECT PROVIDER REGISTRATION) =========================
// =============================================================================
router.post('/providerRegistration', (req, res, next) => {

    if (!req.body.name)
        return res.status(409).send({
            'message': 'Please provide name.'
        });

    if (!req.body.opUrls)
        return res.status(409).send({
            'message': 'Please provide opUrls.'
        });

    if (!req.body.keys)
        return res.status(409).send({
            'message': 'Please provide keys.'
        });

    if (!req.body.trustMarks)
        return res.status(409).send({
            'message': 'Please provide trustMarks.'
        });

    Providers.registerProvider(req.body,(err, provider, info) => {
        if (err) {
            return next(err);
        }
        if (!provider) {
            return res.status(401).send(info);
        }

        return res.status(200).send(provider);
    });
});


// =============================================================================
// GET ALL PROVIDERS ===========================================================
// =============================================================================
router.get('/getAllProviders', (req, res, next) => {

    Providers.getAllProviders((err, provider, info) => {
        if (err) {
            console.log("err: " + err);
            return next(err);
        }
        if (!provider) {
            console.log("info: " + info);
            return res.status(401).send(info);
        }
        console.log("provider: " + JSON.stringify(provider));
        return res.status(200).send(provider);
    });
});

module.exports = router;