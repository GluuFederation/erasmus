package org.xdi.oxd.badgemanager.ldap.service;

import org.apache.http.NameValuePair;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.xdi.oxd.badgemanager.global.Global;
import org.xdi.oxd.badgemanager.util.DisableSSLCertificateCheckUtil;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by lcom64 on 5/7/17.
 */
public class HttpService {
    static String Base_uri = Global.API_ENDPOINT;

    public static String callGet(String Method, ArrayList<NameValuePair> params, boolean isDisableCheck) {
        if (isDisableCheck) {
            try {
                DisableSSLCertificateCheckUtil.disableChecks();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + Global.Request_AccessToken);

        String uri = Base_uri + Method;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);
        for (NameValuePair p : params) {
            builder.queryParam(p.getName(), p.getValue());
        }
        HttpEntity<String> request = new HttpEntity<>(headers);

        HttpEntity<String> resp = restTemplate.exchange(builder.build().encode().toUri(), HttpMethod.GET, request, String.class);

        String result = resp.getBody();
        return result;
    }

    public static String callPost(String method, Object postData, boolean isDisableCheck) {
        if (isDisableCheck) {
            try {
                DisableSSLCertificateCheckUtil.disableChecks();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        }
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + Global.Request_AccessToken);

        String uri = Base_uri + method;
        HttpEntity<Object> request = new HttpEntity<>(postData, headers);
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

        HttpEntity<String> resp = restTemplate.exchange(uri, HttpMethod.POST, request, String.class);

        String result = resp.getBody();
        return result;

    }
}
