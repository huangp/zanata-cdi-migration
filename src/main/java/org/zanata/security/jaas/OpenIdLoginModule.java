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
import javax.security.auth.login.LoginException;

import org.openid4java.consumer.ConsumerManager;
import org.zanata.security.openid.OpenIdAuthenticationManager;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class OpenIdLoginModule extends ZanataLoginModule {
    private ConsumerManager manager;

    @Inject
    private OpenIdAuthenticationManager openIdAuthenticationManager;

    @Override
    public boolean login() throws LoginException {
        try {
            // This verifies the response and does the login
            openIdAuthenticationManager.verifyResponse();
        }
        catch (Exception e) {
            throw new LoginException(e.getMessage());
        }
        return true;
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
