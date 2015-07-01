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
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Delegate;

import org.picketlink.Identity;
import org.picketlink.idm.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.model.HAccount;
import org.zanata.security.annotations.Authenticated;
import org.zanata.security.authentication.ZanataUser;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@SessionScoped
@Named("zanataIdentity")
public class ZanataIdentity implements Serializable {
    private static final Logger log =
            LoggerFactory.getLogger(ZanataIdentity.class);

    @Inject
    @Delegate
    private Identity identity;


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
        Account account = identity.getAccount();
        if (account != null && account instanceof ZanataUser) {
            authenticatedAccount = ((ZanataUser) account).getAccount();
            log.debug("authenticated account: {}", authenticatedAccount);
        } else {
            log.error("account in identity is not an instance of ZanataUser: {}. Returning null", account);
            return null;
        }
        return authenticatedAccount;
    }
}
