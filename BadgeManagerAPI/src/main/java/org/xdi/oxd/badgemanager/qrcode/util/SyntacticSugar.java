package org.xdi.oxd.badgemanager.qrcode.util;

import org.apache.commons.lang3.StringUtils;

public class SyntacticSugar {

    public static void throwIllegalArgumentExceptionIfEmpty(String parameter, String parameterName){
        if(StringUtils.isEmpty(parameter)){
            throw new IllegalArgumentException("Parameter " + parameter + " cannot be empty");
        }
    }
}
