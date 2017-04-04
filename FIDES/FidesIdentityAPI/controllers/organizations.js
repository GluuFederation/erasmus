"use strict";

const express = require('express'),
  router = express.Router(),
  request = require('request-promise'),
  simpleRequest = require('request'),
  httpStatus = require('http-status'),
  multer = require('multer'),
  fs = require('fs'),
  _ = require('lodash'),
  common = require('../helpers/common'),
  Organizations = require('../helpers/organizations'),
  Federation = require('../helpers/federations');

const storage = multer.diskStorage({
  destination: function (req, file, callback) {
    callback(null, '.' + common.constant.TRUST_MARK_FILEPATH);
  },
  filename: function (req, file, callback) {
    callback(null, common.func.getFileName(file.originalname));
  }
});

const upload = multer({storage: storage}).any();

/**
 * get approved Badge By Organization
 */
router.get('/getBadgeByOrganization/:oid/:status', (req, res, next) => {
  Organizations.getBadgeByOrganization(req.params.oid)
    .then((org) => {
      if (req.params.status == 'approved') {
        return res.status(httpStatus.OK).send(org.approvedBadges);
      } else if (req.params.status == 'pending') {
        return res.status(httpStatus.OK).send(org.pendingBadges);
      } else {
        org.approvedBadges = _.forEach(org.approvedBadges, function (element) {
          if (element._doc)
            element._doc.isApproved = true;
        });
        org.pendingBadges = _.forEach(org.pendingBadges, function (element) {
          if (element._doc)
            element._doc.isApproved = false;
        });
        return res.status(httpStatus.OK).send(_.union(org.approvedBadges, org.pendingBadges));
      }
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * get approved Badge By Organization
 */
router.get('/getOrganizationById/:oid', (req, res, next) => {
  Organizations.getOrganizationById(req.params.oid)
    .then((org) => {
      return res.status(httpStatus.OK).send(org);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Update organization
 */
router.put('/updateOrganization', upload, (req, res, next) => {
  if (!req.body._id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  const filePath = common.constant.TRUST_MARK_FILEPATH;
  req.body.trustMarkFile = (!!req.files[0]) ? process.env.BASE_URL + filePath.substr(7, filePath.length) + '/' + req.files[0].filename : null;
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
  Organizations.getOrganizationById(req.body.organizationId)
    .then((response) => {
      // link organization with federation
      return Federation.addParticipant(req.body.federationId, req.body.organizationId);
    })
    .then((response) => Organizations.approveOrganization(req.body.organizationId, req.body.federationId))
    .then((organization) => res.status(httpStatus.OK).send(organization))
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

module.exports = router;
