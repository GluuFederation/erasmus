package org.xdi.oxd.badgemanager.security;

import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.xdi.oxd.badgemanager.ldap.LDAPInitializer;
import org.xdi.oxd.badgemanager.ldap.commands.LdapCommands;
import org.xdi.oxd.badgemanager.ldap.models.Person;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class GluuUser extends User implements LDAPInitializer.ldapConnectionListner {

    private static final long serialVersionUID = 1L;
    private final String idToken;
    private final Map<String, List<String>> claims;
    private final LDAPInitializer ldapInitializer;
    private boolean isLdapConnected;
    private LdapCommands ldapCommands;
    Person person;

    public GluuUser(String idToken, Map<String, List<String>> claims,
                    Collection<? extends GrantedAuthority> authorities) {
        super(idToken, "", authorities);
        this.idToken = idToken;
        this.claims = claims;
        ldapCommands = new LdapCommands();
        this.ldapInitializer = new LDAPInitializer(this);

    }

    private void createPersonModel(LdapEntryManager ldapEntryManager) {
        person = new Person();

        if (claims != null) {

            if (claims.get("preferred_username") != null) {
                person.setPreferredUsername(claims.get("preferred_username").get(0));
            }
            if (claims.get("picture") != null) {
                person.setPicture(claims.get("picture").get(0));
            }
            if (claims.get("zoneinfo") != null) {
                person.setZoneinfo(claims.get("zoneinfo").get(0));
            }

            if (claims.get("birthdate") != null) {
                person.setBirthdate(claims.get("birthdate").get(0));
            }

            if (claims.get("gender") != null) {
                person.setGender(claims.get("gender").get(0));
            }

            if (claims.get("given_name") != null) {
                person.setGivenName(claims.get("given_name").get(0));
            }

            if (claims.get("nickname") != null) {
                person.setNickname(claims.get("nickname").get(0));
            }

            if (claims.get("email") != null) {
                person.setEmail(claims.get("email").get(0));
                person.setEmail_verified(claims.get("email").get(0));
            }

            if (claims.get("name") != null) {
                person.setName(claims.get("name").get(0));
            }

            if (claims.get("locale") != null) {
                person.setLocale(claims.get("locale").get(0));
            }

            if (claims.get("role") != null) {
                person.setRole(claims.get("role").get(0));
            }

            if (claims.get("sub") != null) {
                person.setSub(claims.get("sub").get(0));
            }
            if (isLdapConnected) {
                ldapCommands.createOrUpdatePerson(ldapEntryManager, person);
            }
        }
    }

    public String getIdToken() {
        return idToken;
    }

    public Map<String, List<String>> getClaims() {
        return claims;
    }


    @Override
    public void ldapConnected(boolean isConnected, LdapEntryManager ldapEntryManager) {
        if (isConnected) {
            isLdapConnected = true;
            if (ldapEntryManager != null)
                createPersonModel(ldapEntryManager);
        } else {
            isLdapConnected = false;
        }
    }
}
