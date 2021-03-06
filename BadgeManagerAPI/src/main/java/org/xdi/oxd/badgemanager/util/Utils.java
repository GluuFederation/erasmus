package org.xdi.oxd.badgemanager.util;

import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;

import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Created by Arvind Tomar on 10/4/17.
 */
@Component
public class Utils {

    /**
     * Decodes Base 64 url.
     */
    public String decodeBase64url(String strEncoded)
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
    public String generateRandomGUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    /**
     * Generates random alpha numeric String.
     */
    public String generateRandomKey(int count) {
        String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int) (Math.random() * ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }

    /**
     * Retrieve base url.
     */
    public String getBaseURL(HttpServletRequest request) {
        String url;
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            url = request.getScheme() + "://" + request.getServerName()+request.getContextPath();
        } else {
            url = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+request.getContextPath();
        }
        return url;
    }

    /**
     * Retrieve static resource path.
     */
    public String getStaticResourcePath(ServletContext context) {
        return context.getRealPath("/WEB-INF/classes/static/images");
    }

    /**
     * Retrieve static resource path.
     */
    public String getStaticResourceLogoPath(ServletContext context) {
        return context.getRealPath("/WEB-INF/classes/static/logo");
    }
}
