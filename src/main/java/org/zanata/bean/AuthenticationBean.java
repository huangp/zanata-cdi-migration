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

import static org.picketlink.Identity.AuthenticationResult;
import static org.picketlink.Identity.AuthenticationResult.FAILED;
import static org.picketlink.Identity.AuthenticationResult.SUCCESS;
import static org.picketlink.idm.credential.Credentials.Status.IN_PROGRESS;
import static org.zanata.security.authentication.AuthType.INTERNAL;
import static org.zanata.security.authentication.AuthType.JAAS;
import static org.zanata.security.authentication.AuthType.KERBEROS;
import static org.zanata.security.authentication.AuthType.OPENID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.credential.Credentials;
import org.zanata.security.authentication.AuthType;
import org.zanata.security.authentication.AuthenticatorSelector;
import org.zanata.security.credentials.OpenIdCredentials;
import org.zanata.security.openid.OpenIdAuthenticationManager;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@RequestScoped
@Named
public class AuthenticationBean {

    @Inject
    @Getter
    private DefaultLoginCredentials loginCredentials;

    @Inject
    @Getter
    private OpenIdCredentials openIdCredentials;

    @Inject
    private AuthenticatorSelector authenticatorSelector;

    @Inject
    private OpenIdAuthenticationManager openIdAuthenticationManager;
    
    @Inject
    private Identity identity;

    public void authenticateInternal() {
        authenticatorSelector.setCredentials(loginCredentials);
        authenticatorSelector.setAuthenticationType(INTERNAL);
        authenticate();
    }

    public void authenticateOpenId() {
        authenticatorSelector.setCredentials(openIdCredentials);
        authenticatorSelector.setAuthenticationType(OPENID);
        try {
            openIdAuthenticationManager.initiateAuthentication();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void authenticateKerberosWithForm() {
        authenticatorSelector.setCredentials(loginCredentials);
        authenticatorSelector.setAuthenticationType(KERBEROS);
        authenticate();
    }

    private void authenticate() {
        AuthenticationResult result = identity.login();
        // Open Id authentication won't finish here
        if (result.equals(FAILED)
                && authenticatorSelector.getAuthenticationType() == OPENID
                && authenticatorSelector.getCredentials().getStatus() == IN_PROGRESS) {
            // Just let it continue its course
        }
        else if( result.equals(SUCCESS) ) {
            // Put a faces message on screen
        }
        else {
            throw new RuntimeException("Failed authentication");
        }
    }

}
