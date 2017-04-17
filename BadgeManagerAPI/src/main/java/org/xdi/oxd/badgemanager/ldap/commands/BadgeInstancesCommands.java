package org.xdi.oxd.badgemanager.ldap.commands;

import com.unboundid.ldap.sdk.Filter;
import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.jboss.resteasy.spi.NotFoundException;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.models.BadgeInstances;
import org.xdi.oxd.badgemanager.ldap.service.InumService;
import org.xdi.oxd.badgemanager.ldap.service.MergeService;
import org.xdi.util.INumGenerator;

import java.util.List;

/**
 * Created by Arvind Tomar on 10/10/16.
 */
public class BadgeInstancesCommands {

    /**
     * Assign a badge to a user which has been requested by him or her
     *
     * @param ldapEntryManager ldapEntryManager instance
     * @param badges           badge class object
     */
    public static boolean createNewBadgeInstances(LdapEntryManager ldapEntryManager, BadgeInstances badges) throws Exception {

        try {
            String inum = InumService.getInum(InumService.badgeInstancePrefix);
            badges.setDn("inum=" + inum + ",ou=badgeInstances,ou=badges,o=" + DefaultConfig.config_organization + ",o=gluu");
            badges.setInum(inum);

            if (!(ldapEntryManager.contains(badges.getDn(), BadgeInstances.class, Filter.create("(inum=" + badges.getInum() + ")")))) {
                ldapEntryManager.persist(badges);
                System.out.println("New badge instance entry");
                return true;
            } else {
                createNewBadgeInstances(ldapEntryManager, badges);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Exception in badge instance entry: " + e.getMessage());
            throw new Exception("There was error creating badge instance");
        }
        throw new Exception("There was error issuing a badge");
    }

    /**
     * Update a badge instance
     *
     * @param ldapEntryManager
     * @param badges
     * @return
    public static boolean updateBadge(LdapEntryManager ldapEntryManager, BadgeInstances badges) throws Exception {
        try {
            String inum = DefaultConfig.config_organization + INumGenerator.generate(2);
            badges.setDn("inum=" + inum + ",ou=badges,ou=people,o=" + DefaultConfig.config_organization + ",o=gluu");
            badges.setInum(inum);
            if (ldapEntryManager.contains(badges.getDn(), BadgeInstances.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                MergeService.merge(badges, ldapEntryManager.findEntries(badges.getDn(), BadgeInstances.class, Filter.create("(inum=" + badges.getInum() + ")")).get(0));
                ldapEntryManager.merge(badges);
                System.out.println("updated entry ");
                return true;
            } else {
                throw new NotFoundException("No badge instance found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No badge instance found");
        }
    }

    /**
     * Delete a badge instance that was assigned to a user
     *
     * @param ldapEntryManager
     * @param badges           object of the badgeInstance that is to be deleted
     * @return
     */
    public static boolean deleteBadge(LdapEntryManager ldapEntryManager, BadgeInstances badges) {
        try {
            String inum = DefaultConfig.config_organization + INumGenerator.generate(2);
            badges.setDn("inum=" + inum + ",ou=badges,ou=people,o=" + DefaultConfig.config_organization + ",o=gluu");
            badges.setInum(inum);
            if (ldapEntryManager.contains(badges.getDn(), BadgeInstances.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                ldapEntryManager.remove(badges);
                System.out.println("Deleted entry ");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Delete a badge instance by Inum of the badge instance.
     *
     * @param ldapEntryManager ldapEntryManager.
     * @param inum             Inum of the badge instance that is to be deleted.
     * @return
     */
    public static boolean deleteBadgeInstanceByInum(LdapEntryManager ldapEntryManager, String inum) {
        try {
            BadgeInstances badges = new BadgeInstances();
            badges.setDn("inum=" + inum + ",ou=badges,ou=people,o=" + DefaultConfig.config_organization + ",o=gluu");
            badges.setInum(inum);
            if (ldapEntryManager.contains(badges.getDn(), BadgeInstances.class, Filter.create("(inum=" + badges.getInum() + ")"))) {
                ldapEntryManager.remove(badges);
                System.out.println("Deleted entry ");
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Reterived a badge instance by Inum of the badge instance.
     *
     * @param ldapEntryManager ldapEntryManager.
     * @param Inum             Inum of the badge instance that is to be retrieved.
     * @return
     */
    public static BadgeInstances getBadgeInstanceByInum(LdapEntryManager ldapEntryManager, String Inum) throws Exception {
        try {

            BadgeInstances badge = new BadgeInstances();
            badge.setInum(Inum);
            badge.setDn("inum=" + Inum + ",ou=badges,ou=people,o=" + DefaultConfig.config_organization + ",o=gluu");

            if (ldapEntryManager.contains(badge.getDn(), BadgeInstances.class, Filter.create("(inum=" + badge.getInum() + ")"))) {

                List<BadgeInstances> badges = ldapEntryManager.findEntries(badge.getDn(), BadgeInstances.class, Filter.create("(inum=" + badge.getInum() + ")"));

                if (badges.size() > 0)
                    return badges.get(0);
                else
                    throw new NotFoundException("No such badge instance found");
            } else {
                throw new NotFoundException("No such badge instance found");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new NotFoundException("No such badge instance found");
        }
    }

    /**
     * Get all the badge instance of the system
     *
     * @param ldapEntryManager ldapEntryManager
     * @return
     */
    public static List<BadgeInstances> getAllBadgesInstance(LdapEntryManager ldapEntryManager) {

        BadgeInstances badges = new BadgeInstances();
        badges.setDn("ou=badges,ou=people,o=" + DefaultConfig.config_organization + ",o=gluu");

        return (ldapEntryManager.findEntries(badges.getDn(), BadgeInstances.class, null));
    }
}