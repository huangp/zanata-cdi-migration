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

import java.util.Date;

import lombok.Getter;
import lombok.Setter;
import org.apache.deltaspike.jpa.api.transaction.Transactional;
import org.picketlink.idm.IdentityManager;
import org.picketlink.idm.credential.Password;
import org.picketlink.idm.model.basic.User;
import org.zanata.model.HAccount;
import org.zanata.model.HPerson;
import org.zanata.security.authentication.ZanataUser;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Named
@RequestScoped
public class UserRegistration {
    @Inject
    private IdentityManager identityManager;

    @Inject
    private EntityManager entityManager;

    @Getter @Setter
    private String loginName;
    @Getter @Setter
    private String firstName;
    @Getter @Setter
    private String lastName;
    @Getter @Setter
    private String password;

    @Transactional
    public String register() {
        Password password = new Password(this.password);

        HAccount account = new HAccount();
        HPerson person = new HPerson();
        person.setAccount(account);
        account.setPerson(person);
        account.setUsername(loginName);
        person.setEmail(loginName + "@example.com");
        person.setName(firstName + ' ' + lastName);
        Date creationDate = new Date();
        person.setCreationDate(creationDate);
        person.setLastChanged(creationDate);
        account.setCreationDate(creationDate);
        account.setLastChanged(creationDate);
        // TODO [CDI] password hash algorithm needs to match
        account.setPasswordHash(new String(password.getValue()));
        account.setEnabled(true);

        entityManager.persist(account);

        ZanataUser newUser = new ZanataUser(account);
        // TODO [CDI] use picketlink IDM model (If configure correctly below method should insert the record to IdentityStore). The default is FileIdentityStore.
//        this.identityManager.add(newUser);

//        this.identityManager.updateCredential(newUser, password);

        return "/security/signin.xhtml";
    }
}
