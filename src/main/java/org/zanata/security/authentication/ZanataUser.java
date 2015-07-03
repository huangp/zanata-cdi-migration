package org.zanata.security.authentication;

import org.picketlink.idm.model.Account;
import org.picketlink.idm.model.annotation.IdentityStereotype;
import org.picketlink.idm.model.basic.User;
import org.zanata.model.HAccount;
import org.zanata.model.HPerson;

import com.google.common.base.MoreObjects;

import static org.picketlink.idm.model.annotation.IdentityStereotype.Stereotype.USER;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@IdentityStereotype(USER)
public class ZanataUser extends User {

    /**
     * Special system user for which all permission checks succeed.
     */
    public static final ZanataUser SYSTEM = new ZanataUser() {
        @Override
        public String toString() {
            return "SYSTEM";
        }
    };

    private HAccount accountEntity;

    public ZanataUser() {
        // picketlink needs this
    }

    public ZanataUser(HAccount accountEntity) {
        this.accountEntity = accountEntity;
        super.setLoginName(accountEntity.getUsername());
        super.setEmail(getPerson().getEmail());
        super.setEnabled(accountEntity.isEnabled());
        super.setCreatedDate(accountEntity.getCreationDate());
        super.setFirstName(getPerson().getName());
    }

    public HAccount getAccount() {
        return accountEntity;
    }

    public HPerson getPerson() {
        return accountEntity.getPerson();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("accountEntity", accountEntity)
                .toString();
    }
}
