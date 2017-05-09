/*
 *  oxPush2 is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 *  Copyright (c) 2014, Gluu
 */

package net.gluu.erasmus.u2f.v2.device;

import net.gluu.erasmus.u2f.v2.exception.U2FException;
import net.gluu.erasmus.u2f.v2.model.AuthenticateRequest;
import net.gluu.erasmus.u2f.v2.model.AuthenticateResponse;
import net.gluu.erasmus.u2f.v2.model.EnrollmentRequest;
import net.gluu.erasmus.u2f.v2.model.EnrollmentResponse;

/**
 * Fido U2F key service to process enrollment/authentication request
 *
 * Created by Yuriy Movchan on 12/28/2015.
 */
public interface U2FKey {

    EnrollmentResponse register(EnrollmentRequest enrollmentRequest) throws U2FException;

    AuthenticateResponse authenticate(AuthenticateRequest authenticateRequest) throws U2FException;

}
