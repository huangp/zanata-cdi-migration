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
package org.zanata.security.authentication;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.Authenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.credential.Credentials;
import org.zanata.security.credentials.OpenIdCredentials;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@RequestScoped
public class AuthenticatorSelector {

    @Getter @Setter
    private Credentials credentials;

    @Getter @Setter
    private AuthType authenticationType;

    @Inject
    private Instance<JAASAuthenticator> jaasAuthenticator;

    // TODO See if this is possible
    //private Instance<OpenIdAuthenticator> openIdAuthenticator;

    @Produces
    @PicketLink
    @RequestScoped
    public Authenticator getAuthenticator() {
        switch (authenticationType) {
            // Kerberos authentication happens with JAAS
            default:
                return jaasAuthenticator.get();
        }
    }
}
