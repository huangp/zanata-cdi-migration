package org.zanata.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.model.HPerson;
import org.zanata.security.annotations.Authenticated;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@RequestScoped
public class AuthenticatedPersonRequestScopedHolder {
    private static final Logger log = LoggerFactory.getLogger(AuthenticatedPersonRequestScopedHolder.class);
    @Produces
    @Named("personInRequest") @Authenticated
    HPerson getPerson() {
        log.info("??????????/ reqeust scoped person producer");
        HPerson hPerson = new HPerson();
        hPerson.setName("request");
        return hPerson;
    }
}
