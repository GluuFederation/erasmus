"use strict";

// load all the things we need
const express = require('express'),
    router = express.Router(),
    Providers = require('../helpers/providers');

// =============================================================================
// GET ALL PROVIDERS ===========================================================
// =============================================================================
router.get('/getAllProviders/:organizationId', (req, res, next) => {
    Providers.getAllProviders(req.params.organizationId, (err, provider, info) => {
        if (err) {
            console.log("err: " + err);
            return next(err);
        }
        if (!provider) {
            console.log("info: " + info);
            return res.status(406).send(info);
        }
        console.log("provider: " + JSON.stringify(provider));
        return res.status(200).send(provider);
    });
});

// =============================================================================
// REGISTRATION (OPENID CONNECT PROVIDER REGISTRATION) =========================
// =============================================================================
router.post('/createProvider', (req, res, next) => {
    if (!req.body.name) {
        return res.status(406).send({
            'message': 'Please provide name.'
        });
    }
    if (!req.body.url) {
        return res.status(406).send({
            'message': 'Please provide url.'
        });
    }
    if (!req.body.clientId) {
        return res.status(406).send({
            'message': 'Please provide client id.'
        });
    }
    if (!req.body.clientSecret) {
        return res.status(406).send({
            'message': 'Please provide client secret.'
        });
    }
    if (!req.body.responseType) {
        return res.status(406).send({
            'message': 'Please provide response type.'
        });
    }
    if (!req.body.state) {
        return res.status(406).send({
            'message': 'Please provide state.'
        });
    }
    if (!req.body.redirectUri) {
        return res.status(406).send({
            'message': 'Please provide redirect uri.'
        });
    }
    if (!req.body.grantType) {
        return res.status(406).send({
            'message': 'Please provide grant type.'
        });
    }
    if (!req.body.code) {
        return res.status(406).send({
            'message': 'Please provide code.'
        });
    }
    if (!req.body.organizationId) {
        return res.status(406).send({
            'message': 'Please provide organization.'
        });
    }

    Providers.addProvider(req.body, (err, provider, info) => {
        if (err) {
            return next(err);
        }
        if (!provider) {
            return res.status(406).send(info);
        }

        return res.status(200).send(provider);
    });
});

// =============================================================================
// Update Provider =============================================================
// =============================================================================
router.post('/updateProvider', (req, res, next) => {
    if (!req.body._id) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }
    if (!req.body.name) {
        return res.status(406).send({
            'message': 'Please provide name.'
        });
    }
    if (!req.body.url) {
        return res.status(406).send({
            'message': 'Please provide url.'
        });
    }
    if (!req.body.clientId) {
        return res.status(406).send({
            'message': 'Please provide client id.'
        });
    }
    if (!req.body.clientSecret) {
        return res.status(406).send({
            'message': 'Please provide client secret.'
        });
    }
    if (!req.body.responseType) {
        return res.status(406).send({
            'message': 'Please provide response type.'
        });
    }
    if (!req.body.state) {
        return res.status(406).send({
            'message': 'Please provide state.'
        });
    }
    if (!req.body.redirectUri) {
        return res.status(406).send({
            'message': 'Please provide redirect uri.'
        });
    }
    if (!req.body.grantType) {
        return res.status(406).send({
            'message': 'Please provide grant type.'
        });
    }
    if (!req.body.code) {
        return res.status(406).send({
            'message': 'Please provide code.'
        });
    }
    if (!req.body.organizationId) {
        return res.status(406).send({
            'message': 'Please provide organization.'
        });
    }

    Providers.updateProvider(req.body, (err, provider, info) => {
        if (err) {
            return next(err);
        }
        if (!provider) {
            return res.status(406).send(info);
        }

        //delete provider._doc.password;
        return res.status(200).send(provider);
    });
});

// =============================================================================
// Remove Provider =============================================================
// =============================================================================
router.delete('/removeProvider/:providerId', (req, res, next) => {
    if (!req.params.providerId) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    Providers.removeProvider(req.params.providerId, (err, provider, info) => {
        if (err) {
            return next(err);
        }
        if (!provider) {
            return res.status(406).send(info);
        }

        return res.status(200).send(provider);
    });
});

module.exports = router;