"use strict";

const express = require('express'),
  router = express.Router(),
  request = require('request-promise'),
  common = require('../helpers/common'),
  httpStatus = require('http-status'),
  Organizations = require('../helpers/organizations');

/**
 * Update organization
 */
router.put('/updateOrganization', (req, res, next) => {
  if (!req.body._id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }

  Organizations.updateOrganization(req.body)
    .then((updatedFederation) => {
      return res.status(httpStatus.OK).send(updatedFederation);
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
 * Remove organization
 */
router.delete('/removeOrganization/:organizationId', (req, res, next) => {
  if (!req.params.organizationId) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  Organizations.removeOrganization(req.params.organizationId)
    .then((removedFederation) => res.status(httpStatus.OK).send(removedFederation))
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Approve organization
 */
router.post('/approveOrganization', (req, res, next) => {
  if (!req.body.organizationId) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  let organization = null;
  let organizationOttoId = null;
  Organizations.getOrganizationById(req.body.organizationId)
    .then((fOrganization) => {
      organization = fOrganization;
      if (!organization) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Federation ' + common.message.NOT_ACCEPTABLE_ID});
      }

      if (organization.isApproved === true) {
        return res.status(500).send({
          message: 'Organization ' + common.message.ALREADY_APPROVE
        });
      }

      let options = {
        method: 'POST',
        uri: process.env.OTTO_BASE_URL + '/organization',
        headers: {
          'content-type': 'application/json'
        },
        body: {name: organization.name},
        json: true,
        resolveWithFullResponse: true
      };

      return request(options);
    })
    .then((response) => {
      let resValues = response.body['@id'].split('/');
      organizationOttoId = resValues[resValues.length - 1];
      let federationOttoId = req.body.federationOttoId;

      // link organization with federation
      const options = {
        method: 'POST',
        uri: process.env.OTTO_BASE_URL + '/federations/' + federationOttoId + '/organization/' + organizationOttoId,
        headers: {
          'content-type': 'application/json'
        },
        json: true
      };
      return request(options);
    })
    .then((response) => {
      return Organizations.approveOrganization(req.body.organizationId, organizationOttoId, req.body.federationId)
        .then((organization) => res.status(httpStatus.OK).send(organization))
        .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
          err: err,
          message: common.message.INTERNAL_SERVER_ERROR
        }));
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

module.exports = router;
