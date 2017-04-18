"use strict";

const express = require('express'),
  router = express.Router(),
  request = require('request-promise'),
  jwt = require('jsonwebtoken'),
  common = require('../helpers/common'),
  httpStatus = require('http-status'),
  Entity = require('../helpers/entities'),
  Participant = require('../helpers/participants');

/**
 * Add entity.
 */
router.post('/createEntity', (req, res, next) => {
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
  if (!req.body.participant) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide participant.'
    });
  }

  Entity.addEntity(req.body)
    .then((entity) => {
      return res.status(httpStatus.OK).send(entity);
    })
    .catch((err) => {
      if (err.code === 11000) {
        // already exist for discoveryUrl
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Entity ' + common.message.NOT_ACCEPTABLE_NAME});
      }

      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Update detail of entity.
 */
router.post('/updateEntity', (req, res, next) => {
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
  if (!req.body.participant) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide participant.'
    });
  }

  Entity.updateEntity(req.body)
    .then((entity) => {
      return res.status(httpStatus.OK).send(entity);
    })
    .catch((err) => {
      if (err.code === 11000) {
        // already exist for discoveryUrl
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Entity ' + common.message.NOT_ACCEPTABLE_NAME});
      }

      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Remove entity. Accepts entityId as parameter.
 */
router.delete('/removeEntity/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide id.'
    });
  }

  Entity.removeEntity(req.params.id)
    .then((entity) => {
      return res.status(httpStatus.OK).send(entity);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 *1 Approve entity. Accepts entityId as parameter.
 */
router.get('/approveEntity/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please provide id.'
    });
  }
  let entity = null;
  let dataJson = {};
  // Get entity details.
  return Entity.getEntityById(req.params.id)
    .then((fEntity) => {
      entity = fEntity;
      if (entity.isApproved) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Entity is already approved.'
        });
      }

      if (entity.participant.isApproved !== true) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({
          message: 'Please approve related participant first to proceed.'
        });
      }

      // Get entity configuration using discovery URL.
      const option = {
        method: 'GET',
        uri: entity.discoveryUrl + '/.well-known/openid-configuration',
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
          return Promise.reject(res.status(httpStatus.NOT_ACCEPTABLE).send({
            message: 'Discovery URL is invalid. Please check and correct it.'
          }));
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
          return Promise.reject(res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
            message: common.message.INTERNAL_SERVER_ERROR
          }));
        }
      }

      dataJson.id = dataJson.issuer;
      dataJson.name = entity.name;
      dataJson.client_id = entity.clientId;
      dataJson.client_secret = entity.clientSecret;
      dataJson.keys = keysJson.keys;

      entity.id = dataJson.issuer;
      entity.name = dataJson.name;
      entity.signedJwksUri = dataJson.signed_jwks_uri;
      entity.signingKeys = dataJson.signing_key;
      entity.type = 'openid_entity';

      return entity.save();
    })
    .then((updatedEntity) => Participant.addEntityInPartcipant(updatedEntity.participant._id, updatedEntity._id))
    .then((response) => Entity.approveEntity(req.params.id))
    .then((entity) => res.status(200).send(entity))
    .catch((err) => {
      if (!err.statusCode) {
        return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          err: err,
          message: common.message.INTERNAL_SERVER_ERROR
        });
      }

      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

module.exports = router;
