package org.xdi.oxd.badgemanager.util;

import java.util.Base64;

import java.io.UnsupportedEncodingException;

/**
 * Created by Arvind Tomar on 10/4/17.
 */
public class Utils {

    /**
     * Decodes Base 64 url.
     */
    public static String decodeBase64url(String strEncoded)
            throws UnsupportedEncodingException {
        if (strEncoded != null) {
            byte[] decodedBytes = Base64.getDecoder().decode(strEncoded);
            return new String(decodedBytes, "UTF-8");
        } else {
            return "";
        }
    }
}
