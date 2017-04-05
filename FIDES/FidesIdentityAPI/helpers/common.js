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
  OWNER_ORGANIZATION_ID: '58d8b139987e8419d0a3c1cc',
  TRUST_MARK_FILEPATH: '/public/images/trustmark',
  BADGE_IMAGE_PATH: '/public/images/badges',
  OTTO_BASE_URL: 'http://localhost:5053,',
  CONTEXT_SCHEMA_URL: 'https://raw.githubusercontent.com/KantaraInitiative/wg-otto/master/schema',
  ORGANIZATION_CONTEXT: '/otto/organization.jsonld',
  FEDERATION_CONTEXT: '/otto/federation.jsonld',
  PROVIDER_CONTEXT: '/openid/op.jsonld',
  OTTO_ORGANIZATION_URL: '/otto/organization',
  OTTO_FEDERATION_URL: '/otto/federations',
  OTTO_PROVIDER_URL: '/otto/federation_entity'
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

module.exports = {
  message,
  constant,
  smtpConfig,
  registrationEmailTemplate,
  func
};