"use strict";

const express = require('express'),
  router = express.Router(),
  request = require('request-promise'),
  common = require('../helpers/common'),
  httpStatus = require('http-status'),
  BadgeCategories = require('../helpers/badgeCategories');

/**
 * Get all active badgeCategories
 */
router.get('/badgeCategory', (req, res, next) => {
  BadgeCategories.getAllBadgeCategories()
    .then((badgeCategories) => {
      if (!badgeCategories) {
        return res.status(httpStatus.OK).send({message: common.message.INTERNAL_SERVER_ERROR});
      }
      return res.status(httpStatus.OK).send(badgeCategories);
    })
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
      err: err,
      message: common.message.INTERNAL_SERVER_ERROR
    }));
});

/**
 * Create badgeCategory
 */
router.post('/badgeCategory', (req, res, next) => {
  return BadgeCategories.addBadgeCategory(req.body)
    .then((savedBadgeCategory) => res.status(httpStatus.OK).send(savedBadgeCategory))
    .catch((err) => {
      if (err.code === 11000) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'BadgeCategory ' + common.message.NOT_ACCEPTABLE_NAME});
      }
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Update badgeCategory
 */
router.put('/badgeCategory/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }

  return BadgeCategories.updateBadgeCategory(req.body, req.params.id)
    .then((badgeCategory) => res.status(httpStatus.OK).send(badgeCategory))
    .catch((err) => {
      if (err.code === 11000) {
        return res.status(httpStatus.NOT_ACCEPTABLE).send({message: 'BadgeCategory ' + common.message.NOT_ACCEPTABLE_NAME});
      }
      return res.status(httpStatus.INTERNAL_SERVER_ERROR).send({
        err: err,
        message: common.message.INTERNAL_SERVER_ERROR
      });
    });
});

/**
 * Remove badgeCategory
 */
router.delete('/badgeCategory/:id', (req, res, next) => {
  if (!req.params.id) {
    return res.status(httpStatus.NOT_ACCEPTABLE).send({
      message: common.message.PROVIDE_ID
    });
  }
  return BadgeCategories.removeBadgeCategory(req.params.id)
    .then((badgeCategory) => res.status(httpStatus.OK).send(badgeCategory))
    .catch((err) => res.status(httpStatus.INTERNAL_SERVER_ERROR).send({err: err, message: common.message.INTERNAL_SERVER_ERROR}));
});

module.exports = router;
