/*
 * Copyright 2014, Red Hat, Inc. and individual contributors as indicated by the
 * @author tags. See the copyright.txt file in the distribution for a full
 * listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA, or see the FSF
 * site: http://www.fsf.org.
 */
package org.zanata.security;

import java.io.Serializable;

import javax.annotation.Nullable;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.inject.Inject;
import javax.inject.Named;

import org.picketlink.idm.model.Account;
import org.picketlink.idm.permission.spi.PermissionResolver;
import org.picketlink.internal.DefaultIdentity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.model.HAccount;
import org.zanata.security.annotations.Authenticated;
import org.zanata.security.authentication.ZanataUser;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 * @author Sean Flanigan <a href="mailto:sflaniga@redhat.com">sflaniga@redhat.com</a>
 */
// NB: Identity is @SessionScoped and @Named("identity)
// We need to override any method in PicketLink's Identity implementation
// which uses the account field, in case tempAccount has been set.
@Specializes
public class ZanataIdentity extends DefaultIdentity implements Serializable,
        Impersonator {
    private static final long serialVersionUID = 1L;

    private static final Logger log =
            LoggerFactory.getLogger(ZanataIdentity.class);
    private static final ThreadLocal<Account> tempAccount =
            new ThreadLocal<>();
    @Inject
    private transient PermissionResolver permissionResolver;

    @Produces
    @Dependent
    @Deprecated
    @Nullable
    HAccount authenticatedAccount() {
        return qualifiedAuthenticatedAccount();
    }

    @Produces
    @Dependent
    @Authenticated
    // original full name is: org.jboss.seam.security.management.authenticatedUser
    @Named("authenticatedUser")
    // WELD-000052 Cannot return null from a non-dependent producer method:
    @Nullable HAccount qualifiedAuthenticatedAccount() {
        HAccount authenticatedAccount;
        Account account = getAccount();
        if (account != null && account instanceof ZanataUser) {
            authenticatedAccount = ((ZanataUser) account).getAccount();
            log.debug("authenticated account: {}", authenticatedAccount);
        } else {
            log.error("account in identity is not an instance of ZanataUser: {}. Returning null", account);
            return null;
        }
        return authenticatedAccount;
    }

    // Replacement for Seam's RunAsOperation
    @Override
    public void runAs(HAccount user, Runnable runnable) {
        runAs(new ZanataUser(user), runnable);
    }

    // Replacement for Seam's RunAsOperation(systemOp=true)
    @Override
    public void runAsSystem(Runnable runnable) {
        runAs(ZanataUser.SYSTEM, runnable);
    }

    private void runAs(ZanataUser user, Runnable runnable) {
        tempAccount.set(user);
        try {
            runnable.run();
        } finally {
            tempAccount.remove();
        }
    }

    @Override
    public boolean isLoggedIn() {
        return getAccount() != null;
    }

    @Override
    public Account getAccount() {
        return tempAccount.get() != null ? tempAccount.get() :
                super.getAccount();
    }

    @Override
    public AuthenticationResult login() {
        if (tempAccount.get() != null) {
            throw new RuntimeException("login not permitted during runAs operation");
        }
        return super.login();
    }

    @Override
    public void logout() {
        if (tempAccount.get() != null) {
            throw new RuntimeException("logout not permitted during runAs operation");
        }
        super.logout();
    }

    public boolean hasPermission(Object resource, String operation) {
        if (getAccount() == ZanataUser.SYSTEM) {
            return true;
        }
        return isLoggedIn() && permissionResolver.resolvePermission(getAccount(), resource, operation);
    }

    public boolean hasPermission(Class<?> resourceClass, Serializable identifier, String operation) {
        if (getAccount() == ZanataUser.SYSTEM) {
            return true;
        }
        return isLoggedIn() && permissionResolver.resolvePermission(getAccount(), resourceClass, identifier, operation);
    }

    // hasRole is used by SecurityFunctions
    public boolean hasRole(String role) {
        if (getAccount() == ZanataUser.SYSTEM) {
            return true;
        }
        // TODO check the picketlink role
        throw new UnsupportedOperationException();
    }

}
