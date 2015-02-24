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

import lombok.extern.slf4j.Slf4j;
import org.jboss.security.SecurityContextAssociation;
import org.picketlink.Identity;
import org.picketlink.authentication.BaseAuthenticator;
import org.picketlink.idm.model.basic.User;

import javax.faces.context.FacesContext;
import javax.inject.Inject;

import java.lang.reflect.Field;

import static org.picketlink.authentication.Authenticator.AuthenticationStatus.FAILURE;
import static org.picketlink.authentication.Authenticator.AuthenticationStatus.SUCCESS;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Slf4j
public class KerberosAuthenticator extends BaseAuthenticator {

    private static final String SUBJECT = "subject";
    private static final String PRINCIPAL = "principal";

    @Inject
    private Identity identity;

    @Override
    public void authenticate() {
        if (identity.isLoggedIn()) {
            // TODO Do we still need this?
            /*if (Events.exists()) {
                Events.instance().raiseEvent(Identity.EVENT_ALREADY_LOGGED_IN);
            }*/
            return;
        }

        // String username =
        // FacesContext.getCurrentInstance().getExternalContext().getRemoteUser();
        // Workaround for SECURITY-719, remove once it's fixed
        String username =
                FacesContext.getCurrentInstance().getExternalContext()
                        .getUserPrincipal().getName();
        // Remove the domain name, if there is one
        if (username.indexOf('@') > 0) {
            username = username.substring(0, username.indexOf('@'));
        }
        log.debug("remote username: {}", username);

        try {
            Field field = Identity.class.getDeclaredField(PRINCIPAL);
            field.setAccessible(true);
            field.set(identity, SecurityContextAssociation.getPrincipal());

            field = Identity.class.getDeclaredField(SUBJECT);
            field.setAccessible(true);
            field.set(identity, SecurityContextAssociation.getSubject());

            setStatus(SUCCESS);
            setAccount(new User(username));
        }
        catch (NoSuchFieldException e) {
            setStatus(FAILURE);
        }
        catch (IllegalAccessException e) {
            setStatus(FAILURE);
        }
    }
}
