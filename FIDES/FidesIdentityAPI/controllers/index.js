"use strict";

const express = require('express'),
  router = express.Router();

/**
 * Default route.
 */
router.get('/loggedIn', (req, res) => res.status(200).send({
  message: 'User logged in.'
}));

router.use('/', require('./users'));
router.use('/', require('./entities'));
router.use('/', require('./roles'));
router.use('/', require('./participants'));
router.use('/', require('./federations'));
router.use('/', require('./badgeCategory'));
router.use('/', require('./badge'));

module.exports = router;
