"use strict";

const express = require('express'),
  router = express.Router(),
  request = require('request-promise'),
  httpStatus = require('http-status'),
  multer = require('multer'),
  fs = require('fs'),
  common = require('../helpers/common'),
  Badges = require('../helpers/badges');

const storage = multer.diskStorage({
  destination: function (req, file, callback) {
    callback(null, '.' + common.constant.BADGE_IMAGE_PATH);
  },
  filename: function (req, file, callback) {
    callback(null, common.func.getFileName(file.originalname));
  }
});

const upload = multer({storage: storage}).any();
/**
 * Get all active badges
 */
router.get('/badges', (req, res, next) => {
  Badges.getAllBadges()
    .then((badges) => {
      if (!badges) {
        return res.status(httpStatus.OK).send({message: common.message.INTERNAL_SERVER_ERROR});
      }
      return res.status(httpStatus.OK).send(badges);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Get badge by id
 */
router.get('/templateBadge/:id', upload, (req, res, next) => {
  Badges.getBadgeById(req.params.id)
    .then((badges) => {
      if (!badges) {
        return res.status(httpStatus.OK).send({message: 'Badge ' + common.message.NOT_FOUND});
      }
      return res.status(httpStatus.OK).send(badges);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: 'Badge ' + common.message.NOT_FOUND
    }));
});

/**
 * get approved Badge By issuer
 */
router.post('/templateBadge/issuer', (req, res, next) => {
  if (!req.body.issuer) {
    return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({message: 'Issuer ' + common.message.NOT_FOUND});
  }
  if (!req.body.type) {
    return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({message: 'Type ' + common.message.NOT_FOUND});
  }

  Badges.getBadgeByIssuer(req.body.issuer)
    .then((org) => {

      if (!org) {
        return res.status(httpStatus.OK).send([]);
      }

      let lstbadge = [];
      org.approvedBadges.forEach(function (item) {
        const cat = {
          id: process.env.BASE_URL + '/templateBadge/' + item._id,
          name: item.name,
          description: item.description,
          image: item.image,
          narrative: item.narrative,
          type: item.type,
          issuer: {
            id: process.env.BASE_URL + '/participant/' + org._id,
            name: org.name,
            type: org.type,
            url: req.body.issuer,
            verification: {
              allowedOrigins: req.body.issuer,
              type: 'hosted'
            }
          }
        };
        if (req.body.type == item.category.name || req.body.type == 'all') {
          lstbadge.push(cat);
        }
      });
      return res.status(httpStatus.OK).send(lstbadge);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Create badges
 */
router.post('/badges', upload, (req, res, next) => {
  const filePath = common.constant.BADGE_IMAGE_PATH;
  req.body.image = (!!req.files[0]) ? process.env.BASE_URL + filePath.substr(7, filePath.length) + '/' + req.files[0].filename : null;

  return Badges.addBadge(req.body)
    .then((savedBadge) => res.status(httpStatus.OK).send(savedBadge))
    .catch((err) => {
      if (err.code === 11000) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Badge ' + common.message.NOT_ACCEPTABLE_NAME});
      }
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Update badges
 */
router.put('/badges/:id', upload, (req, res, next) => {
  const filePath = common.constant.BADGE_IMAGE_PATH;
  req.body.image = (!!req.files[0]) ? process.env.BASE_URL + filePath.substr(7, filePath.length) + '/' + req.files[0].filename : null;
  if (req.body.image && (req.body.image != req.body.oldImage)) {
    try {
      fs.unlinkSync(common.constant.BADGE_IMAGE_PATH + req.body.oldImage);
    } catch (e) {
    }
  }

  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }

  return Badges.updateBadge(req.body, req.params.id)
    .then((badges) => res.status(httpStatus.OK).send(badges))
    .catch((err) => {
      if (err.code === 11000) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'Badge ' + common.message.NOT_ACCEPTABLE_NAME});
      }
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Remove badges
 */
router.delete('/badges/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  return Badges.removeBadge(req.params.id)
    .then((badges) => {
      if (!!badges.image) {
        try {
          let badgeImage = badges.image;
          badgeImage = badgeImage.substr(badgeImage.lastIndexOf('/') + 1, badgeImage.length);
          fs.unlinkSync('.' + common.constant.BADGE_IMAGE_PATH + '/' + badgeImage);
        } catch (e) {
        }
      }
      return res.status(httpStatus.OK).send(badges)
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Request for badges by participant admin
 */
router.post('/badgeRequest', (req, res, next) => {
  if (!req.body.oid) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  if (!req.body.bids || req.body.bids.length <= 0) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please select at least one badge'
    });
  }

  return Badges.badgeRequest(req.body.oid, req.body.bids)
    .then((participant) => {
      return res.status(httpStatus.OK).send(participant);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Approve badges by federation-fides admin
 */
router.post('/badgeApprove', (req, res, next) => {
  if (!req.body.oid) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }

  if (!req.body.bids || req.body.bids.length <= 0) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: 'Please select at least one badge'
    });
  }

  return Badges.badgeApprove(req.body.oid, req.body.bids)
    .then((participant) => {
      return res.status(httpStatus.OK).send(participant);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

module.exports = router;
