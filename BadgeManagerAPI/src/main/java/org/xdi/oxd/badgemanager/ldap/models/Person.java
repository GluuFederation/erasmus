package org.xdi.oxd.badgemanager.ldap.models;

import org.gluu.site.ldap.persistence.annotation.LdapAttribute;
import org.gluu.site.ldap.persistence.annotation.LdapEntry;
import org.gluu.site.ldap.persistence.annotation.LdapObjectClass;

/**
 * Created by Arvind Tomar on 3/10/16.
 * Updated by Arvind Tomar on 7/10/16.
 */
@LdapEntry
@LdapObjectClass(values = {"top", "gluuPerson"})
public class Person extends SimpleUser {

    @LdapAttribute(name = "zoneinfo")
    private String zoneinfo;

    @LdapAttribute(name = "inum")
    private String sub;

    @LdapAttribute(name = "phoneNumberVerified")
    private String phone_number;

    @LdapAttribute(name = "nickname")
    private String nickname;

    @LdapAttribute(name = "website")
    private String website;

    @LdapAttribute(name = "middleName")
    private String middle_name;

    @LdapAttribute(name = "emailVerified")
    private String email_verified;

    @LdapAttribute(name = "locale")
    private String locale;

    @LdapAttribute(name = "phoneNumberVerified")
    private String phone_number_verified;

    @LdapAttribute(name = "preferredUsername")
    private String preferredUsername;

    @LdapAttribute(name = "givenName")
    private String givenName;

    @LdapAttribute(name = "picture")
    private String picture;

    @LdapAttribute(name = "updatedAt")
    private String updated_at;

    @LdapAttribute(name = "inum")
    private String inum;

    @LdapAttribute(name = "mail")
    private String email;

    @LdapAttribute(name = "address")
    private String address;

    @LdapAttribute(name = "iname")
    private String name;

    @LdapAttribute(name = "birthdate")
    private String birthdate;

    @LdapAttribute(name = "gender")
    private String gender;

    @LdapAttribute(name = "phoneMobileNumber")
    private String phone_mobile_number;

    @LdapAttribute(name = "role")
    private String role;

    @LdapAttribute(name = "profile")
    private String profile;

    @LdapAttribute(name = "displayName")
    private String displayName;

    @LdapAttribute(name = "gluuStatus")
    private boolean active;

    public Person() {
    }


    public String getZoneinfo() {
        return zoneinfo;

    }

    public void setZoneinfo(String zoneinfo) {
        this.zoneinfo = zoneinfo;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }


    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }


    public String getMiddle_name() {
        return middle_name;
    }

    public void setMiddle_name(String middle_name) {
        this.middle_name = middle_name;
    }

    public String getEmail_verified() {
        return email_verified;
    }

    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getPhone_number_verified() {
        return phone_number_verified;
    }

    public void setPhone_number_verified(String phone_number_verified) {
        this.phone_number_verified = phone_number_verified;
    }

    public String getPreferredUsername() {
        return preferredUsername;
    }

    public void setPreferredUsername(String preferredUsername) {
        this.preferredUsername = preferredUsername;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getInum() {
        return inum;
    }

    public void setInum(String inum) {
        this.inum = inum;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthdate) {
        this.birthdate = birthdate;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhone_mobile_number() {
        return phone_mobile_number;
    }

    public void setPhone_mobile_number(String phone_mobile_number) {
        this.phone_mobile_number = phone_mobile_number;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }


    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}