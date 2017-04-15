package org.xdi.oxd.badgemanager;

import org.gluu.site.ldap.persistence.LdapEntryManager;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.xdi.oxd.badgemanager.config.DefaultConfig;
import org.xdi.oxd.badgemanager.ldap.LDAPInitializer;
import org.xdi.oxd.badgemanager.ldap.commands.ServerCommands;
import org.xdi.oxd.badgemanager.storage.StorageProperties;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class OxdSpringApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(OxdSpringApplication.class);
	}

	public static void main(String[] args) {
		SpringApplication.run(OxdSpringApplication.class, args);
		LDAPInitializer ldapInitializer = new LDAPInitializer(new LDAPInitializer.ldapConnectionListner() {
			@Override
			public void ldapConnected(boolean isConnected, LdapEntryManager ldapEntryManager) {
				if (isConnected) {
					try {
						DefaultConfig.config_organization = ServerCommands.getRootOrgranizationInum(ldapEntryManager);
						if (DefaultConfig.config_organization == null || DefaultConfig.config_organization.equals("")) {
							System.out.println("server closed because we can't find organization dn configured properly in LDAP");
							SpringApplication.run(OxdSpringApplication.class, args).close();
						}
					} catch (Exception e) {
						System.out.println("server closed because we can't find organization dn configured properly in LDAP");
						e.printStackTrace();
					}
				}
			}
		});
	}
}
