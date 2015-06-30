/*
 * Copyright 2015, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.security.jaas;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.security.auth.login.LoginException;

import org.jboss.security.SimplePrincipal;
import org.picketlink.credential.DefaultLoginCredentials;
import org.zanata.model.HAccount;
import sun.security.acl.PrincipalImpl;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class InternalLoginModule extends ZanataLoginModule {

    @Inject
    private DefaultLoginCredentials credentials;

    @Inject
    private EntityManager entityManager;

    @Override
    public boolean login() throws LoginException {
        // TODO Use picketlink's IDM features. For now, just a simple authentication will do
        HAccount account =
                (HAccount) entityManager
                        .createQuery(
                                "from HAccount a where a.username = :username")
                        .setParameter("username", credentials.getUserId())
                        .getSingleResult();

        // TODO Actually check a password
        if(account != null) {
            subject.getPrincipals().add(new SimplePrincipal(account.getUsername()));
            return true;
        }
        else {
            throw new LoginException();
        }
    }

    @Override
    public boolean commit() throws LoginException {
        return true;
    }

    @Override
    public boolean abort() throws LoginException {
        return true;
    }

    @Override
    public boolean logout() throws LoginException {
        return true;
    }
}
