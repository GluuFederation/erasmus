"use strict";

const express = require('express'),
    router = express.Router(),
    request = require('request'),
    Organizations = require('../helpers/organizations');

/**
 * Update organization
 */
 router.post('/updateOrganization', (req, res, next) => {
    if (!req.body._id) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    Organizations.updateOrganization(req.body, (err, organization, info) => {
        if (err) {
            if(err.code === 11000) {
                return res.status(406).send({
                    'message': 'Organization with same name is already exists. Please try different name.'
                });
            } else {
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                })
            }
        }

        if (!organization) {
            return res.status(406).send(info);
        }

        const options = {
            method: 'PUT',
            url: process.env.OTTO_BASE_URL + '/organization/' + organization.ottoId,
            headers: {
                'content-type': 'application/json'
            },
            body: {name: req.body.name},
            json: true
        };

        request(options, (error, response, body) => {
            if (error) {
                return res.status(500).send({
                    'message': 'The otto server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            if (!response) {
                return res.status(500).send({
                    'message': 'The otto server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            return res.status(200).send(organization);
        });
    });
});

/**
 * Remove organization
 */
router.delete('/removeOrganization/:organizationId', (req, res, next) => {
    if (!req.params.organizationId) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    Organizations.removeOrganization(req.params.organizationId, (err, organization, info) => {
        if (err) {
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }
        if (!organization) {
            return res.status(406).send(info);
        }

        const options = {
            method: 'DELETE',
            url: process.env.OTTO_BASE_URL + '/organization/' + organization.ottoId,
            headers: {
                'content-type': 'application/json'
            },
            json: true
        };

        request(options, (error, response, body) => {
            if (error) {
                return res.status(500).send({
                    'message': 'The otto server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            if (!response) {
                return res.status(500).send({
                    'message': 'The otto server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            return res.status(200).send(organization);
        });
    });
});

/**
 * Approve organization
 */
router.post('/approveOrganization', (req, res, next) => {
    if (!req.body.organizationId) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    Organizations.getOrganizationById(req.body.organizationId, (err, organization, info) => {
        if (err) {
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }
        if (!organization) {
            return res.status(406).send(info);
        }

        if(organization.isApproved === true) {
            return res.status(500).send({
                'message': 'Organization is already approved.'
            });
        }

        let dataJson = {
            "name": organization.name
        };

        let options = {
            method: 'POST',
            url: process.env.OTTO_BASE_URL + "/organization",
            headers: {
                'content-type': 'application/json'
            },
            body: dataJson,
            json: true
        };

        request(options, function (error, response, body) {
            if (error) {
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            if (!response) {
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                });
            }

            let resValues = body['@id'].split('/');
            let organizationOttoId = resValues[resValues.length - 1];
            let federationOttoId = req.body.federationOttoId;

            // link organization with federation
            options = {
                method: 'POST',
                url: process.env.OTTO_BASE_URL + "/organization/" + organizationOttoId + "/federation/" + federationOttoId,
                headers: {
                    'content-type': 'application/json'
                },
                json: true
            };

            request(options, function (error, response, body) {
                if (error) {
                    return res.status(500).send({
                        'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                    });
                }

                if (!response) {
                    return res.status(500).send({
                        'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                    });
                }

                Organizations.approveOrganization(req.body.organizationId, organizationOttoId, req.body.federationId , (err, organization, info) => {
                    if (err) {
                        return res.status(500).send({
                            'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                        });
                    }

                    if (!organization) {
                        return res.status(406).send(info);
                    }

                    return res.status(200).send(organization);
                });
            });
        });
    });
});

module.exports = router;
