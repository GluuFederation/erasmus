"use strict";

const express = require('express'),
    router = express.Router(),
    request = require('request-promise'),
    jwt = require('jsonwebtoken'),
    common = require('../helpers/common'),
    httpStatus = require('http-status'),
    Providers = require('../helpers/providers');

/**
 * Add provider.
 */
router.post('/createProvider', (req, res, next) => {
    if (!req.body.name) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide name.'
        });
    }

    if (!req.body.discoveryUrl) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide discovery URL.'
        });
    }
    /*if (!req.body.clientId) {
     return res.status(406).send({
     'message': 'Please provide client id.'
     });
     }
     if (!req.body.clientSecret) {
     return res.status(406).send({
     'message': 'Please provide client secret.'
     });
     }*/
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
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide organization.'
        });
    }

    Providers.addProvider(req.body)
        .then((provider) => {
            return res.status(httpStatus.OK).send(provider);
        })
        .catch((err) => {
            if (err.code === 11000) {
                // already exist for discoveryUrl
                return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Provider ' + common.message.NOT_ACCEPTABLE_NAME});
            }

            return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                err: err,
                message: common.message.INTERNAL_SERVER_ERROR
            });
        });
});

/**
 * Update detail of provider.
 */
router.post('/updateProvider', (req, res, next) => {
    if (!req.body._id) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide id.'
        });
    }
    if (!req.body.name) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide name.'
        });
    }
    if (!req.body.discoveryUrl) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide discovery URL.'
        });
    }
    /*if (!req.body.clientId) {
     return res.status(406).send({
     'message': 'Please provide client id.'
     });
     }
     if (!req.body.clientSecret) {
     return res.status(406).send({
     'message': 'Please provide client secret.'
     });
     }*/
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
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide organization.'
        });
    }

    Providers.updateProvider(req.body)
        .then((provider) => {
            return res.status(httpStatus.OK).send(provider);
        })
        .catch((err) => {
            if (err.code === 11000) {
                // already exist for discoveryUrl
                return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Provider ' + common.message.NOT_ACCEPTABLE_NAME});
            }

            return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                err: err,
                message: common.message.INTERNAL_SERVER_ERROR
            });
        });
});

/**
 * Remove provider. Accepts providerId as parameter.
 */
router.delete('/removeProvider/:providerId', (req, res, next) => {
    if (!req.params.providerId) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide id.'
        });
    }

    Providers.removeProvider(req.params.providerId)
        .then((provider) => {
            return res.status(httpStatus.OK).send(provider);
        })
        .catch((err) => {
            return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                err: err,
                message: common.message.INTERNAL_SERVER_ERROR
            });
        });
});

/**
 *1 Approve provider. Accepts providerId as parameter.
 */
router.get('/approveProvider/:providerId', (req, res, next) => {
    if (!req.params.providerId) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Please provide id.'
        });
    }
    let provider = null;
    let dataJson = {};
    let ottoId = null;
    // Get provider details.
    Providers.getProviderById(req.params.providerId)
        .then((fProvider) => {
            provider = fProvider;
            if (provider.isApproved) {
                return res.status(httpStatus.NOT_ACCEPTABLE).send({
                    message: 'Provider is already approved.'
                });
            }

            if (provider.organization.isApproved !== true) {
                return res.status(httpStatus.NOT_ACCEPTABLE).send({
                    message: 'Please approve related organization first to proceed.'
                });
            }

            // Get provider configuration using discovery URL.
            const option = {
                method: 'GET',
                uri: provider.discoveryUrl,
                resolveWithFullResponse: true
            };

            return request(option);
        })
        .then((response) => {
            if (response) {
                try {
                    dataJson = JSON.parse(response.body);
                } catch (exception) {
                    console.log(exception.toString());
                    return res.status(httpStatus.NOT_ACCEPTABLE).send({
                        message: 'Discovery URL is invalid. Please check and correct it.'
                    });
                }
            }

            // Get keys
            let keypair = require('keypair');
            let pair = keypair();
            let signedUrl = jwt.sign(dataJson.jwks_uri, pair.private, {algorithm: 'RS256'});

            dataJson.signed_jwks_uri = signedUrl;
            dataJson.signing_key = pair.public;

            dataJson.jwks_uri = jwt.verify(dataJson.signed_jwks_uri, dataJson.signing_key);

            const option = {
                method: 'GET',
                uri: dataJson.jwks_uri,
                resolveWithFullResponse: true
            };

            return request(option);
        })
        .then((response) => {
            let keysJson = {};
            if (response) {
                try {
                    keysJson = JSON.parse(response.body);
                } catch (exception) {
                    console.log(exception.toString());
                    return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                        message: common.message.INTERNAL_SERVER_ERROR
                    });
                }
            }

            dataJson.id = dataJson.issuer;
            dataJson.name = provider.name;
            dataJson.client_id = provider.clientId;
            dataJson.client_secret = provider.clientSecret;
            dataJson.keys = keysJson.keys;

            const entityData = {
                id: dataJson.issuer,
                name: dataJson.name,
                metadata_statements: dataJson.metadata_statements || null,
                metadata_statement_uris: dataJson.metadata_statement_uris || null,
                signed_jwks_uri: dataJson.signed_jwks_uri,
                signing_keys: dataJson.signing_key,
                type: 'openid_provider'
                // organization
                // description
            };

            let options = {
                method: 'POST',
                uri: process.env.OTTO_BASE_URL + '/federation_entity',
                headers: {
                    'content-type': 'application/json'
                },
                body: entityData,
                json: true,
                resolveWithFullResponse: true
            };

            // Add provider (federation entity) to OTTO
            return request(options);
        })
        .then((response) => {
            if (!response) {
                return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                    message: common.message.INTERNAL_SERVER_ERROR
                });
            }

            if (!response.body['@id']) {
                return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                    message: common.message.INTERNAL_SERVER_ERROR
                });
            }

            let resValues = response.body['@id'].split('/');
            ottoId = resValues[resValues.length - 1];

            let options = {
                method: 'POST',
                url: process.env.OTTO_BASE_URL + '/organization/' + provider.organization.ottoId + '/federation_entity/' + ottoId,
                headers: {
                    'content-type': 'application/json'
                },
                json: true,
                resolveWithFullResponse: true
            };

            // Link provider (federation entity) and organization in OTTO
            return request(options);
        })
        .then((response) => {
            // Update local mongodb with provider's OTTO Id and approve flag.
            Providers.approveProvider(req.params.providerId, ottoId)
                .then((provider) => {
                    if (!provider) {
                        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Provider ' + common.message.NOT_ACCEPTABLE_ID});
                    }

                    return res.status(200).send(provider);
                })
                .catch((err) => {
                    return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                        err: err,
                        message: common.message.INTERNAL_SERVER_ERROR
                    });
                });
        })
        .catch((err) => {
            return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
                err: err,
                message: common.message.INTERNAL_SERVER_ERROR
            });
        });
});

module.exports = router;
