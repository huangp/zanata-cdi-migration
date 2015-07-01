package org.zanata.security.jaas;

import java.security.Principal;

import org.zanata.model.HAccount;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class AccountPrincipal implements Principal {
    private final HAccount account;

    public AccountPrincipal(HAccount account) {
        this.account = account;
    }

    @Override
    public String getName() {
        return account.getUsername();
    }

    public HAccount getAccount() {
        return account;
    }
}
