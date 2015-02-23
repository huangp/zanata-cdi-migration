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
package org.zanata.security.authentication;

import java.io.IOException;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.picketlink.annotations.PicketLink;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.credential.DefaultLoginCredentials;
import org.picketlink.idm.credential.Credentials;
import org.picketlink.idm.model.basic.User;
import org.zanata.security.credentials.OpenIdCredentials;

import static org.picketlink.idm.credential.Credentials.Status.IN_PROGRESS;

/**
 * Initiates an authentication request in accordance with the type of
 * credentials being authenticated.
 * 
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@PicketLink
@RequestScoped
public class JAASAuthenticator extends BaseAuthenticator {

    @Inject
    private AuthenticatorSelector authenticatorSelector;

    @Override
    public void authenticate() {
        try {
            Subject subject = new Subject();
            LoginContext delegate =
                    new LoginContext(getLoginModuleName(), subject,
                            new CallbackHandler() {
                                @Override
                                public void handle(Callback[] callbacks)
                                        throws IOException,
                                        UnsupportedCallbackException {
                                    System.out
                                            .println(
                                                    "======Lets see what this does");
                                }
                            });
            delegate.login();
            setStatus(AuthenticationStatus.SUCCESS);
            setAccount(new User("undefined"));
        } catch (LoginException e) {
            setStatus(AuthenticationStatus.FAILURE);
        }
    }

    private String getLoginModuleName() {
        if (authenticatorSelector.getCredentials() instanceof DefaultLoginCredentials) {
            return "zanata.cdi.internal";
        }
        else if(authenticatorSelector.getCredentials() instanceof OpenIdCredentials) {
            return "zanata.cdi.openid";
        }
        return null;
    }

    private User getAuthenticatedUser() {
        if (authenticatorSelector.getCredentials() instanceof DefaultLoginCredentials) {
            return new User(
                    ((DefaultLoginCredentials) authenticatorSelector
                            .getCredentials()).getUserId());
        }
        else if(authenticatorSelector.getCredentials() instanceof OpenIdCredentials) {
            return new User(
                    ((OpenIdCredentials) authenticatorSelector.getCredentials())
                            .getOpenId());
        }
        return null;
    }
}
