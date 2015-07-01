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
package org.zanata.bean;

import org.picketlink.idm.model.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.model.HAccount;
import org.zanata.security.ZanataIdentity;
import org.zanata.security.annotations.Authenticated;
import org.zanata.security.authentication.ZanataUser;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * To try Identity stuff out.
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Named
public class IdentityBean {
    private static final Logger log =
            LoggerFactory.getLogger(IdentityBean.class);
    @Inject
    private ZanataIdentity identity;

    @Inject @Authenticated
    private HAccount authenticated;

    public boolean isLoggedIn() {
        return identity.isLoggedIn();
    }

    public Account getAccount() {
        Account account = identity.getAccount();
        if (account instanceof ZanataUser) {
            log.info("logged in account {}:", ((ZanataUser) account).getAccount());
        }
        return account;
    }

    public String getAuthenticated() {
        log.info("authenticated: {}", authenticated);
        return authenticated == null ? "null" : authenticated.getUsername();
    }
}
