/*
 * Copyright 2016 The AppAuth for Android Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.gluu.appauth;

import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static net.gluu.appauth.TestValues.TEST_APP_REDIRECT_URI;
import static net.gluu.appauth.TestValues.TEST_AUTH_CODE;
import static net.gluu.appauth.TestValues.TEST_CLIENT_ID;
import static net.gluu.appauth.TestValues.getTestServiceConfig;
import static net.gluu.appauth.TokenResponse.KEY_ACCESS_TOKEN;
import static net.gluu.appauth.TokenResponse.KEY_EXPIRES_AT;
import static net.gluu.appauth.TokenResponse.KEY_ID_TOKEN;
import static net.gluu.appauth.TokenResponse.KEY_REFRESH_TOKEN;
import static net.gluu.appauth.TokenResponse.KEY_SCOPE;
import static net.gluu.appauth.TokenResponse.KEY_TOKEN_TYPE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.junit.Assert.assertThat;

@RunWith(RobolectricTestRunner.class)
@Config(constants = BuildConfig.class, sdk=16)
public class TokenResponseTest {
    private static final String TEST_KEY_TOKEN_TYPE = "Bearer";
    private static final String TEST_KEY_ACCESS_TOKEN = "pAstudrU6axaw#Da355eseTu6ugufrev";
    private static final Long TEST_KEY_KEY_EXPIRES_AT = 1481304561609L;
    private static final String TEST_KEY_REFRESH_TOKEN = "T#xapeva#Rux3steh3fazuvak4seN#S?";
    private static final String TEST_KEY_ID_TOKEN = "5-=5eW5eGe3wE7A$WA+waph7S#FRedat";
    private static final String TEST_KEY_SCOPE_1 = "Scope01";
    private static final String TEST_KEY_SCOPE_2 = "Scope02";
    private static final String TEST_KEY_SCOPE_3 = "Scope03";
    private static final String TEST_KEY_SCOPES = TEST_KEY_SCOPE_1 + " " + TEST_KEY_SCOPE_2 + " " + TEST_KEY_SCOPE_3;

    private static String TEST_JSON_WITH_SCOPE = "{\n" +
                                                 "    \"" + KEY_ACCESS_TOKEN + "\": \"" + TEST_KEY_ACCESS_TOKEN + "\",\n" +
                                                 "    \"" + KEY_TOKEN_TYPE + "\": \"" + TEST_KEY_TOKEN_TYPE + "\",\n" +
                                                 "    \"" + KEY_REFRESH_TOKEN + "\": \"" + TEST_KEY_REFRESH_TOKEN + "\",\n" +
                                                 "    \"" + KEY_ID_TOKEN + "\": \"" + TEST_KEY_ID_TOKEN + "\",\n" +
                                                 "    \"" + KEY_EXPIRES_AT + "\": " + TEST_KEY_KEY_EXPIRES_AT + ",\n" +
                                                 "    \"" + KEY_SCOPE + "\": \"" + TEST_KEY_SCOPES + "\"\n" +
                                                 "}";

    private static String TEST_JSON_WITHOUT_SCOPE = "{\n" +
                                                    "    \"" + KEY_ACCESS_TOKEN + "\": \"" + TEST_KEY_ACCESS_TOKEN + "\",\n" +
                                                    "    \"" + KEY_TOKEN_TYPE + "\": \"" + TEST_KEY_TOKEN_TYPE + "\",\n" +
                                                    "    \"" + KEY_REFRESH_TOKEN + "\": \"" + TEST_KEY_REFRESH_TOKEN + "\",\n" +
                                                    "    \"" + KEY_ID_TOKEN + "\": \"" + TEST_KEY_ID_TOKEN + "\",\n" +
                                                    "    \"" + KEY_EXPIRES_AT + "\": " + TEST_KEY_KEY_EXPIRES_AT + ",\n" +
                                                    "    \"" + KEY_SCOPE + "\":\"\"\n" +
                                                    "}";
    private static String TEST_JSON_WITHOUT_SCOPE_FIELD = "{\n" +
                                                          "    \"" + KEY_ACCESS_TOKEN + "\": \"" + TEST_KEY_ACCESS_TOKEN + "\",\n" +
                                                          "    \"" + KEY_TOKEN_TYPE + "\": \"" + TEST_KEY_TOKEN_TYPE + "\",\n" +
                                                          "    \"" + KEY_REFRESH_TOKEN + "\": \"" + TEST_KEY_REFRESH_TOKEN + "\",\n" +
                                                          "    \"" + KEY_ID_TOKEN + "\": \"" + TEST_KEY_ID_TOKEN + "\",\n" +
                                                          "    \"" + KEY_EXPIRES_AT + "\": " + TEST_KEY_KEY_EXPIRES_AT + "\n" +
                                                          "}";

    private TokenResponse.Builder mMinimalBuilder;

    @Before
    public void setUp() {
        TokenRequest request = new TokenRequest.Builder(getTestServiceConfig(), TEST_CLIENT_ID)
                .setAuthorizationCode(TEST_AUTH_CODE)
                .setRedirectUri(TEST_APP_REDIRECT_URI)
                .build();
        mMinimalBuilder = new TokenResponse.Builder(request);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testBuilder_setAdditionalParams_withBuiltInParam() {
        mMinimalBuilder.setAdditionalParameters(
                Collections.singletonMap(TokenRequest.PARAM_SCOPE, "scope"));
    }

    @Test
    public void testBuilder_fromResponseJsonStringWithScope() throws JSONException{
        System.out.println(TEST_JSON_WITH_SCOPE);
        TokenResponse tokenResponse = mMinimalBuilder.fromResponseJsonString(TEST_JSON_WITH_SCOPE).build();

        assertNotNull(tokenResponse);

        assertEquals(TEST_KEY_ACCESS_TOKEN, tokenResponse.accessToken);
        assertEquals(TEST_KEY_TOKEN_TYPE, tokenResponse.tokenType);
        assertEquals(TEST_KEY_REFRESH_TOKEN, tokenResponse.refreshToken);
        assertEquals(TEST_KEY_ID_TOKEN, tokenResponse.idToken);
        assertEquals(TEST_KEY_KEY_EXPIRES_AT, tokenResponse.accessTokenExpirationTime);

        assertEquals(TEST_KEY_SCOPES, tokenResponse.scope);
        assertThat(tokenResponse.getScopeSet(), is(equalTo((Set) new HashSet<>(Arrays.asList(TEST_KEY_SCOPE_1, TEST_KEY_SCOPE_2, TEST_KEY_SCOPE_3)))));
    }

    @Test
    public void testBuilder_fromResponseJsonStringWithoutScope() throws JSONException{
        System.out.println(TEST_JSON_WITHOUT_SCOPE);
        TokenResponse tokenResponse = mMinimalBuilder.fromResponseJsonString(TEST_JSON_WITHOUT_SCOPE).build();

        assertNotNull(tokenResponse);

        assertEquals(TEST_KEY_ACCESS_TOKEN, tokenResponse.accessToken);
        assertEquals(TEST_KEY_TOKEN_TYPE, tokenResponse.tokenType);
        assertEquals(TEST_KEY_REFRESH_TOKEN, tokenResponse.refreshToken);
        assertEquals(TEST_KEY_ID_TOKEN, tokenResponse.idToken);
        assertEquals(TEST_KEY_KEY_EXPIRES_AT, tokenResponse.accessTokenExpirationTime);

        assertThat(tokenResponse.scope, isEmptyOrNullString());
        assertNull(tokenResponse.getScopeSet());
    }

    @Test
    public void testBuilder_fromResponseJsonStringWithoutScopeField() throws JSONException{
        System.out.println(TEST_JSON_WITHOUT_SCOPE_FIELD);
        TokenResponse tokenResponse = mMinimalBuilder.fromResponseJsonString(TEST_JSON_WITHOUT_SCOPE_FIELD).build();

        assertNotNull(tokenResponse);

        assertEquals(TEST_KEY_ACCESS_TOKEN, tokenResponse.accessToken);
        assertEquals(TEST_KEY_TOKEN_TYPE, tokenResponse.tokenType);
        assertEquals(TEST_KEY_REFRESH_TOKEN, tokenResponse.refreshToken);
        assertEquals(TEST_KEY_ID_TOKEN, tokenResponse.idToken);
        assertEquals(TEST_KEY_KEY_EXPIRES_AT, tokenResponse.accessTokenExpirationTime);

        assertThat(tokenResponse.scope, isEmptyOrNullString());
        assertNull(tokenResponse.getScopeSet());
    }
}
