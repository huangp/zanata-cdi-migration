package org.zanata.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.model.HAccount;
import org.zanata.model.HPerson;
import org.zanata.security.annotations.Authenticated;

import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.Serializable;

/**
 * @author Patrick Huang <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@SessionScoped
public class AuthenticatedPersonSessionScopedHolder implements Serializable {
    private static final Logger log = LoggerFactory.getLogger(AuthenticatedPersonSessionScopedHolder.class);

    @Produces
    @Named("personInSession") @Authenticated
    HPerson getPerson() {
        log.info(">>>>>>>>> session scoped person producer");
        HPerson hPerson = new HPerson();
        hPerson.setName("sessionScoped");
        return hPerson;
    }
}
