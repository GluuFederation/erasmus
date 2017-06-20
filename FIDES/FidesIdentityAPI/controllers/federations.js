"use strict";

const express = require('express'),
  router = express.Router(),
  request = require('request-promise'),
  common = require('../helpers/common'),
  httpStatus = require('http-status'),
  Federations = require('../helpers/federations'),
  Participant = require('../helpers/participants');

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
  req.body.sponsor = common.constant.OWNER_PARTICIPANT_ID;

  return Federations.addFederation(req.body)
    .then((savedFederation) => res.status(httpStatus.OK).send(savedFederation))
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
 * Update federation
 */
router.put('/updateFederation', (req, res, next) => {
  if (!req.body._id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }

  return Federations.updateFederation(req.body)
    .then((federation) => res.status(httpStatus.OK).send(federation))
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
router.delete('/removeFederation/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  return Federations.removeFederation(req.params.id)
    .then((federation) => res.status(httpStatus.OK).send(federation))
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

module.exports = router;
