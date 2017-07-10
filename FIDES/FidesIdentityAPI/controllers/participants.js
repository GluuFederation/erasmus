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
  Participant = require('../helpers/participants'),
  Users = require('../helpers/users'),
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
 * get approved Badge By Participant
 */
router.get('/participant/:pid/badge/status/:status', (req, res, next) => {
  Participant.getBadgeByParticipant(req.params.pid)
    .then((org) => {
      if (!org) {
        return res.status(httpStatus.OK).send([]);
      }

      if (req.params.status == 'approved') {
        return res.status(httpStatus.OK).send(org.approvedBadges);
      } else if (req.params.status == 'pending') {
        return res.status(httpStatus.OK).send(org.pendingBadges);
      } else {
        org.approvedBadges = _.forEach(org.approvedBadges, function (element) {
          if (!!element)
            element.isApproved = true;
        });
        org.pendingBadges = _.forEach(org.pendingBadges, function (element) {
          if (!!element)
            element.isApproved = false;
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
 * get approved Badge By Participant
 */
router.get('/participant/:oid', (req, res, next) => {
  Participant.getParticipantById(req.params.oid)
    .then((org) => {
      return res.status(httpStatus.OK).send(org);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Get list of all the participants.
 */
router.get('/participant', (req, res, next) => {
  Users.getAllParticipants(req.query)
    .then((participants) => {
      if (!participants) {
        return res.status(httpStatus.OK).send({message: 'Participant ' + common.message.NOT_FOUND});
      }
      return res.status(httpStatus.OK).send(participants);
    })
    .catch((err) => {
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Update participant
 */
router.put('/participant', upload, (req, res, next) => {
  if (!req.body._id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  const filePath = common.constant.TRUST_MARK_FILEPATH;
  req.body.trustMarkFile = (!!req.files && !!req.files[0]) ? process.env.BASE_URL + filePath.substr(7, filePath.length) + '/' + req.files[0].filename : null;
  if (req.body.trustMarkFile && (req.body.trustMarkFile != req.body.oldtrustMarkFile)) {
    try {
      fs.unlinkSync(common.constant.TRUST_MARK_FILEPATH + req.body.oldtrustMarkFile);
    } catch (e) {
    }
  }

  Participant.updateParticipant(req.body)
    .then((updatedParticipant) => {
      return res.status(httpStatus.OK).send(updatedParticipant);
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
 * Remove participant
 */
router.delete('/participant/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  Participant.removeParticipant(req.params.id)
    .then((removedFederation) => res.status(httpStatus.OK).send(removedFederation))
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Approve participant
 */
router.post('/participant/:pid/federation/:fid', (req, res, next) => {
  if (!req.params.pid) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  Participant.getParticipantById(req.params.pid)
    .then((response) => {
      // link participant with federation
      return Federation.addParticipant(req.params.fid, req.params.pid);
    })
    .then((response) => Participant.approveParticipant(req.params.pid, req.params.fid))
    .then((participant) => res.status(httpStatus.OK).send(participant))
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

module.exports = router;
