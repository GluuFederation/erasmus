"use strict";

const express = require('express'),
  router = express.Router(),
  request = require('request-promise'),
  simpleRequest = require('request'),
  httpStatus = require('http-status'),
  multer = require('multer'),
  fs = require('fs'),
  common = require('../helpers/common'),
  Organizations = require('../helpers/organizations');

const storage = multer.diskStorage({
  destination: function (req, file, callback) {
    callback(null, common.constant.trustMarkFilePath);
  },
  filename: function (req, file, callback) {
    callback(null, common.func.getFileName(req.body._id, file.originalname));
  }
});

const upload = multer({storage: storage}).any();
/**
 * Update organization
 */
router.put('/updateOrganization', upload, (req, res, next) => {
  if (!req.body._id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  req.body.trustMarkFile = (!!req.files[0]) ? req.files[0].filename : null;
  if (req.body.trustMarkFile && (req.body.trustMarkFile != req.body.oldtrustMarkFile)) {
    try {
      fs.unlinkSync(common.constant.trustMarkFilePath + req.body.oldtrustMarkFile);
    } catch (e) {
    }
  }

  Organizations.updateOrganization(req.body)
    .then((updatedOrganization) => {
      return res.status(httpStatus.OK).send(updatedOrganization);
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
  Organizations.getOrganizationById(req.body.organizationId)
    .then((response) => {
      // link organization with federation
      const options = {
        method: 'POST',
        uri: process.env.OTTO_BASE_URL + '/federations/' + req.body.federationId + '/organization/' + req.body.organizationId,
        headers: {
          'content-type': 'application/json'
        },
        json: true
      };
      return request(options);
    })
    .then((response) => Organizations.approveOrganization(req.body.organizationId, req.body.federationId))
    .then((organization) => res.status(httpStatus.OK).send(organization))
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

module.exports = router;
