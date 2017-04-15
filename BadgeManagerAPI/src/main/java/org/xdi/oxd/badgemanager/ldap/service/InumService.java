package org.xdi.oxd.badgemanager.ldap.service;

import org.xdi.util.INumGenerator;

/**
 * Created by Arvind Tomar on 10/8/16.
 */
public class InumService {
    public static String gluuDn = "@!4301.2A50.9A09.7688!";

    public static String personPrefix = gluuDn + "1000!";

    public static String organizationPrefix = gluuDn + "1001!";

    public static String badgePrefix = gluuDn + "1002!";

    public static String badgeRequestPrefix = gluuDn + "1002!";

    public static String badgeInstancePrefix = gluuDn + "1004!";

    public static String badgeIssuerPrefix = gluuDn + "1005!";

    public static String getInum(String prefix) {
        return prefix.concat(INumGenerator.generate(2));
    }

    public static String getInum(String prefix, int size) {
        return prefix.concat(INumGenerator.generate(size));
    }

}
