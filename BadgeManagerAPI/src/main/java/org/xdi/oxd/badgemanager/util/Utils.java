package org.xdi.oxd.badgemanager.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

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

    /**
     * Generates random GUID.
     */
    public static String generateRandomGUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Generates random alpha numeric String.
     */
    public static String generateRandomKey(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    /**
     * Generates base url.
     */
    public static String getBaseURL(HttpServletRequest request) {
        String url;
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            url = request.getScheme() + "://" + request.getServerName()+request.getContextPath();
        } else {
            url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+request.getContextPath();
        }
        return url;
    }
}
