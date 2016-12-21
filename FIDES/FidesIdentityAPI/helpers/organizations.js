"use strict";

// load up the organization model
const Organization = require('../models/organization');

// =============================================================================
// Retrieves all provider ======================================================
// =============================================================================
let getAllOrganizations = (done) => {
    let query = Organization.find({
        isActive: true
    });
    query.sort({
        order: 'asc'
    });

    query.exec((err, organizations) => {
        if (err)
            done(err);
        else {
            if (organizations.length) {
                console.log(organizations);
                done(null, organizations);
            } else {
                done(null, null, {
                    message: 'No records found'
                });
            }
        }
    });
};

module.exports = {
    getAllOrganizations
};
