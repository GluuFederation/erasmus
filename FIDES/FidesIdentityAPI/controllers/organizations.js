"use strict";

const express = require('express'),
    router = express.Router(),
    request = require('request'),
    Organizations = require('../helpers/organizations');

/**
 * Get all active organizations
 */
router.get('/getAllOrganizations', (req, res, next) => {
    Organizations.getAllOrganizations((err, organization, info) => {
        if (err) {
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }
        if (!organization) {
            return res.status(200).send(info);
        }

        return res.status(200).send(organization);
    });
});

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
            } else{
                return res.status(500).send({
                    'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                })
            }
        }
        if (!organization) {
            return res.status(406).send(info);
        }

        return res.status(200).send(organization);
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

        return res.status(200).send(organization);
    });
});

/**
 * Approve organization
 */
router.get('/approveOrganization/:organizationId', (req, res, next) => {
    if (!req.params.organizationId) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    Organizations.getOrganizationById(req.params.organizationId, (err, organization, info) => {
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

        var options = {
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
            let ottoId = resValues[resValues.length - 1];

            Organizations.approveOrganization(req.params.organizationId, ottoId, (err, organization, info) => {
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

module.exports = router;
