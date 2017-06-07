/*
 * oxAuth is available under the MIT License (2008). See http://opensource.org/licenses/MIT for full text.
 *
 * Copyright (c) 2014, Gluu
 */

package org.xdi.oxd.badgemanager.ldap.models.fido.u2f;

public class InvalidDeviceCounterException extends DeviceCompromisedException {

	private static final long serialVersionUID = -3393844723613998052L;

	public InvalidDeviceCounterException(DeviceRegistration registration) {
		super(registration, "The device's internal counter was was smaller than expected. It's possible that the device has been cloned!");
	}
}
