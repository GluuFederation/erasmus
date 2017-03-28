const nodeMailer = require("nodemailer");

const setting = require("./common");
const transporter = nodeMailer.createTransport(setting.smtpConfig);

exports.sendEmail = function (mailOptions) {
    return transporter.sendMail(mailOptions, function (error, info) {
        if (error) {
            console.log(error);
            return false;
        } else {
            console.log(info);
            return true;
        }
    })
};