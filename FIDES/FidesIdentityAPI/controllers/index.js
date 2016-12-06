"use strict";

const express = require('express'),
    router = express.Router();

// normal routes ===============================================================
router.get('/', (req, res) => res.status(200).send({
    "message": "Please login first"
}));
router.use('/', require('./users'));
router.use('/', require('./providers'));


module.exports = router;
