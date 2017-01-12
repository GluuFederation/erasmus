"use strict";

const express = require('express'),
    router = express.Router(),
    request = require('request'),
    Providers = require('../helpers/providers');

/**
 * Get all providers. Accepts userId as parameter if user is organization admin.
 */
router.get('/getAllProviders/:userId', (req, res, next) => {
    Providers.getAllProviders(req.params.userId, (err, provider, info) => {
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

/**
 * Add provider.
 */
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
    /*if (!req.body.redirectUri) {
        return res.status(406).send({
            'message': 'Please provide redirect uri.'
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
    if (!req.body.grantType) {
        return res.status(406).send({
            'message': 'Please provide grant type.'
        });
    }
    if (!req.body.code) {
        return res.status(406).send({
            'message': 'Please provide code.'
        });
    }*/
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

/**
 * Update detail of provider.
 */
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
    /*if (!req.body.redirectUri) {
        return res.status(406).send({
            'message': 'Please provide redirect uri.'
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

    if (!req.body.grantType) {
        return res.status(406).send({
            'message': 'Please provide grant type.'
        });
    }
    if (!req.body.code) {
        return res.status(406).send({
            'message': 'Please provide code.'
        });
    }*/
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

/**
 * Remove provider. Accepts providerId as parameter.
 */
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

/**
 * Approve provider. Accepts providerId as parameter.
 */
router.get('/approveProvider/:providerId', (req, res, next) => {
    if (!req.params.providerId) {
        return res.status(406).send({
            'message': 'Please provide id.'
        });
    }

    // Get provider details.
    Providers.getProviderById(req.params.providerId, (err, provider, info) => {
        if (err) {
            return res.status(500).send({
                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
            });
        }
        if (!provider) {
            return res.status(406).send(info);
        }

        if (provider.isApproved === true) {
            return res.status(406).send({
                'message': 'Provider is already approved.'
            });
        }

        if (provider.organization.isApproved !== true) {
            return res.status(406).send({
                'message': 'Please approve related organization first to proceed.'
            });
        }

        // Get provider configuration using discovery URL.
        request.get(provider.url + "/.well-known/openid-configuration", function (error, response, body) {
            let dataJson = {};
            if (response) {
                try {
                    dataJson = JSON.parse(body);
                } catch (exception){
                    console.log(exception.toString());
                }
            }

            dataJson.id = provider.url;
            dataJson.url = provider.url;
            dataJson.name = provider.name;
            dataJson.client_id = provider.clientId;
            dataJson.client_secret = provider.clientSecret;

            let options = {
                method: 'POST',
                url: process.env.OTTO_BASE_URL + "/federation_entity",
                headers: {
                    'content-type': 'application/json'
                },
                body: dataJson,
                json: true
            };

            // Add provider (federation entity) to OTTO
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

                let options = {
                    method: 'POST',
                    url: process.env.OTTO_BASE_URL + "/organization/" + provider.organization.ottoId + "/federation_entity/" + ottoId,
                    headers: {
                        'content-type': 'application/json'
                    },
                    json: true
                };

                // Link provider (federation entity) and organization in OTTO
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

                    // Update local mongodb with provider's OTTO Id and approve flag.
                    Providers.approveProvider(req.params.providerId, ottoId, (err, provider, info) => {
                        if (err) {
                            return res.status(500).send({
                                'message': 'The server encountered an internal error and was unable to complete your request. Please contact administrator.'
                            });
                        }
                        if (!provider) {
                            return res.status(406).send(info);
                        }

                        return res.status(200).send(provider);
                    });
                });
            });
        });
    });
});

module.exports = router;
