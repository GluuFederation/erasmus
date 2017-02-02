"use strict";

const express = require('express'),
    router = express.Router(),
    request = require('request-promise'),
    common = require('../helpers/common'),
    httpStatus = require('http-status'),
    Federations = require('../helpers/federations');

/**
 * Get all active federations
 */
router.get('/getAllFederations', (req, res, next) => {
    Federations.getAllFederations()
        .then((federation) => {
            if (!federation) {
                return res.status(httpStatus.OK).send({message: common.message.INTERNAL_SERVER_ERROR});
            }
            return res.status(httpStatus.OK).send(federation);
        })
        .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
            err: err,
            message: common.message.INTERNAL_SERVER_ERROR
        }));
});

/**
 * Create federation
 */
router.post('/addFederation', (req, res, next) => {
    let federation = null;
    Federations.addFederation(req.body)
        .then((savedFederation) => {
            const options = {
                method: 'POST',
                uri: process.env.OTTO_BASE_URL + '/federations',
                headers: {
                    'content-type': 'application/json'
                },
                body: {name: req.body.name},
                json: true,
                resolveWithFullResponse: true
            };
            federation = savedFederation;
            return request(options);
        })
        .then((response) => {
            let resValues = response.body['@id'].split('/');
            let ottoId = resValues[resValues.length - 1];
            federation.ottoId = ottoId;
            return federation.save()
                .then((savedFederation) => res.status(httpStatus.OK).send(federation))
                .catch(() => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({message: common.message.INTERNAL_SERVER_ERROR}));
        })
        .catch((err) => {
            if (err.code === 11000) {
                return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Federation ' + common.message.NOT_ACCEPTABLE_NAME});
            }

            return federation.remove()
                .then(() => {
                    return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                        err: err,
                        message: common.message.INTERNAL_SERVER_ERROR
                    });
                });
        });
});

/**
 * Update federation
 */
router.put('/updateFederation', (req, res, next) => {
    if (!req.body._id) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: common.message.PROVIDE_ID
        });
    }
    let federation = null;
    Federations.updateFederation(req.body)
        .then((updatedFederation) => {
            federation = updatedFederation;
            const options = {
                method: 'PUT',
                uri: process.env.OTTO_BASE_URL + '/federations/' + updatedFederation.ottoId,
                headers: {
                    'content-type': 'application/json'
                },
                body: {name: req.body.name},
                json: true,
                resolveWithFullResponse: true
            };
            request(options);
        })
        .then((response) => {
            return res.status(httpStatus.OK).send(federation);
        })
        .catch((err) => {
            if (err.code === 11000) {
                return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Federation ' + common.message.NOT_ACCEPTABLE_NAME});
            }
            return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                err: err,
                message: common.message.INTERNAL_SERVER_ERROR
            });
        });
});

/**
 * Remove federation
 */
router.delete('/removeFederation/:federationId', (req, res, next) => {
    if (!req.params.federationId) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: common.message.PROVIDE_ID
        });
    }
    let federation = null;
    Federations.removeFederation(req.params.federationId)
        .then((removedFederation) => {
            federation = removedFederation;
            if (!federation) {
                return res.status(httpStatus.NOT_ACCEPTABLE).send({ message : 'Federation ' + common.message.NOT_ACCEPTABLE_ID });
            }
            const options = {
                method: 'DELETE',
                uri: process.env.OTTO_BASE_URL + '/federations/' + federation.ottoId,
                headers: {
                    'content-type': 'application/json'
                },
                json: true,
                resolveWithFullResponse: true
            };
            request(options);
        })
        .then((response) => {
            return res.status(httpStatus.OK).send(federation);
        })
        .catch((err) => {
            return federation.save()
                .then(() => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({message: common.message.INTERNAL_SERVER_ERROR}));
        });
});

module.exports = router;
