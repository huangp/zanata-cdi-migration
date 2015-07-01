package org.zanata.security.jaas;

import java.security.Principal;

import org.zanata.model.HAccount;
import org.zanata.security.HCredentials;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class CredentialPrincipal implements Principal {
    private final HCredentials credentials;

    public CredentialPrincipal(HCredentials credentials) {
        this.credentials = credentials;
    }

    @Override
    public String getName() {
        return credentials.getAccount().getUsername();
    }

    public HAccount getAccount() {
        return credentials.getAccount();
    }
}
