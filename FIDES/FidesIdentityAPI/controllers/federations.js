"use strict";

const express = require('express'),
    router = express.Router(),
    request = require('request'),
    Federations = require('../helpers/federations');

/**
 * Get all active federations
 */
router.get('/getAllFederations', (req, res, next) => {
    Federations.getAllFederations((err, federation, info) => {
        if (err) {
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }
        if (!federation) {
            return res.status(200).send(info);
        }

        return res.status(200).send(federation);
    });
});

/**
 * Create federation
 */
router.post('/addFederation', (req, res, next) => {
    Federations.addFederation(req.body, (err, federation, info) => {
        if (err) {
            if(err.code === 11000) {
                return res.status(406).send({
                    'message': 'Federation with same name is already exists. Please try different name.'
                });
            } else{
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                })
            }
        }

        if (!federation) {
            return res.status(406).send(info);
        }

        return res.status(200).send(federation);
    });
});

/**
 * Update federation
 */
 router.put('/updateFederation', (req, res, next) => {
    if (!req.body._id) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    Federations.updateFederation(req.body, (err, federation, info) => {
        if (err) {
            if(err.code === 11000) {
                return res.status(406).send({
                    'message': 'Federation with same name is already exists. Please try different name.'
                });
            } else{
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                })
            }
        }

        if (!federation) {
            return res.status(406).send(info);
        }

        return res.status(200).send(federation);
    });
});

/**
 * Remove federation
 */
router.delete('/removeFederation/:federationId', (req, res, next) => {
    if (!req.params.federationId) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    Federations.removeFederation(req.params.federationId, (err, federation, info) => {
        if (err) {
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }
        if (!federation) {
            return res.status(406).send(info);
        }

        return res.status(200).send(federation);
    });
});

module.exports = router;
