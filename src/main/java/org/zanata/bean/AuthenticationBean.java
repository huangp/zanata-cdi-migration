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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;

import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;
import org.zanata.model.security.authenticator.AuthenticatorSelector;

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
    private AuthenticatorSelector authenticatorSelector;
    
    @Inject
    private Identity identity;

    public void authenticateInternal() {
        authenticatorSelector.setCredentials(loginCredentials);
        AuthenticationResult result = identity.login();
        if( result.equals( AuthenticationResult.SUCCESS )) {
            // Put a faces message on screen
        }
        else {
            throw new RuntimeException("Failed authentication");
        }
    }

}
