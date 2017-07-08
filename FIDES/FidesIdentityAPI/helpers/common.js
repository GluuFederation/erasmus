const uuid = require('node-uuid');

const message = {
  INTERNAL_SERVER_ERROR: 'The server encountered an internal error and was unable to complete your request. Please contact administrator.',
  NOT_FOUND: 'not found',
  NOT_ACCEPTABLE_NAME: 'is already exists. Please try different.',
  NOT_ACCEPTABLE_ID: 'id not found.',
  PROVIDE_ID: 'Please provide id',
  ALREADY_APPROVE: 'is already approved.'
};

const constant = {
  OWNER_PARTICIPANT_ID: '595f8d4eba8a3155b4f916b7',
  TRUST_MARK_FILEPATH: '/public/images/trustmark',
  BADGE_IMAGE_PATH: '/public/images/badges',
  OTTO_BASE_URL: 'http://localhost:5053',
  CONTEXT_SCHEMA_URL: 'https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html',
  PARTICIPANT_CONTEXT: '#participant',
  FEDERATION_CONTEXT: '#federation',
  ENTITY_CONTEXT: '#entity',
  RA_CONTEXT: '#registration-authority',
  METADATA_CONTEXT: '#metadata',
  OTTO_PARTICIPANT_URL: '/otto/participant',
  OTTO_FEDERATION_URL: '/otto/federations',
  OTTO_ENTITY_URL: '/otto/entity',
  OTTO_RA_URL: '/otto/registration_authority',
  OTTO_METADATA_URL: '/otto/metadata',
  RA_ID: '58f5da4957d53d2ffbbb31df',
  METADATA_ID: '58f5da4957d53d2ffbbb31e0'
};

const func = {
  getFileName: getFileName,
  generateString: generateString
};

//COMMON METHODS
function getExtension(filename) {
  const phrase = filename;
  const i = phrase.lastIndexOf('.');
  return (i < 0) ? '' : phrase.substr(i);
}

function getFileName(filename) {
  return uuid.v4() + '' + getExtension(filename);
}

function generateString(length) {
  const chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXTZabcdefghiklmnopqrstuvwxyz";
  let str = '';
  for (let i = 0; i < length; i++) {
    let rnum = Math.floor(Math.random() * chars.length);
    str += chars.substring(rnum, rnum + 1);
  }

  return str;
}

const smtpConfig = {
  host: 'webmail.gluu.org',
  port: 587,
  secure: false,
  auth:{
    user:'support@gluu.org',
    pass:'nLtcTnsJbS'
  },
  requireTLS: true
};

const registrationEmailTemplate = `<html><head><title>Registration</title></head><p>Thank you <b>{{0}}</b>, for registration with us with client id <b>{{1}}</b>. <br/> Please maintain your client and extend your client expire date for connect with us.</p><p> Regards, <br/> Gluu Team.</p></html>`;

var ottoConfig = {
  dbConfig: "mongodb://localhost:27017/otto-fides",
  port: "5053",
  RA_NAME: "otto-test",
  baseURL: "http://localhost:5053",
  contextParticipant: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#participant",
  contextFederation: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#federation",
  contextEntity: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#entity",
  contextRegistrationAuthority: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#registration-authority",
  contextMetadata: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#metadata",
  contextRequirement: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#requirement",
  contextACR: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#acr",
  contextSchemaClass: "https://rawgit.com/KantaraInitiative/wg-otto/master/html/otto-vocab-1.0.html#schema",
  contextBadge: "https://w3id.org/openbadges/v2",
  RA_ID: "58f5da4957d53d2ffbbb31df",
  isServerStart: true
};

module.exports = {
  message,
  constant,
  smtpConfig,
  registrationEmailTemplate,
  func,
  ottoConfig
};