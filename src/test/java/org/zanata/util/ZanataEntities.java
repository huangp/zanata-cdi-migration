package org.zanata.util;

import org.zanata.model.HAccount;
import org.zanata.model.HAccountRole;
import org.zanata.model.HPerson;
import com.google.common.collect.Lists;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
public class ZanataEntities {
    public static Iterable<Class> entitiesForRemoval() {
        return Lists.<Class> newArrayList(HAccountRole.class, HPerson.class, HAccount.class);
    }
}
