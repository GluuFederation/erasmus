package org.xdi.oxd.badgemanager.ldap.service;

import org.xdi.util.properties.FileConfiguration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Properties;

public class MergeService {

    private static String serverPort;

    public static void merge(Object obj, Object update) {
        if (!obj.getClass().isAssignableFrom(update.getClass())) {
            return;
        }

        Method[] methods = obj.getClass().getMethods();

        for (Method fromMethod : methods) {
            if (fromMethod.getDeclaringClass().equals(obj.getClass())
                    && fromMethod.getName().startsWith("get")) {

                String fromName = fromMethod.getName();
                String toName = fromName.replace("get", "set");

                try {
                    if (fromMethod.invoke(obj, (Object[]) null) == null) {
                        Method toMetod = obj.getClass().getMethod(toName, fromMethod.getReturnType());
                        Object value = fromMethod.invoke(update, (Object[]) null);
                        if (value != null) {
                            toMetod.invoke(obj, value);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void SetPitcureUrls(Object obj) {
        try {
            FileConfiguration configuration = new FileConfiguration("application.properties");

            Properties properties = (Properties) configuration.getProperties().clone();
            serverPort = properties.getProperty("server.port");

            String address = String.valueOf(NetworkInterface.getNetworkInterfaces().nextElement().getInterfaceAddresses().get(1).getAddress());
            address = "https:/" + address + ":" + serverPort;
            boolean setPitureExists = false;

            Method[] methods = obj.getClass().getMethods();

            for (Method method : obj.getClass().getMethods()) {

                if (method.getName().startsWith("setPicture")) {
                    setPitureExists = true;

                }
            }
            if (setPitureExists) {
                Method methodToset = null;
                Method methodToget = null;
                methodToset = obj.getClass().getMethod("setPicture", new Class[]{String.class});
                methodToget = obj.getClass().getMethod("getPicture");
                methodToset.invoke(obj, String.valueOf(address) + methodToget.invoke(obj));
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
