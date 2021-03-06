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
package org.zanata.test.bean;

import java.util.concurrent.Future;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.deltaspike.cdise.weld.WeldContextControl;
import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.jglue.cdiunit.InRequestScope;
import org.jglue.cdiunit.deltaspike.SupportDeltaspikeCore;
import org.jglue.cdiunit.internal.servlet.MockHttpServletRequestImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.zanata.async.AsyncMethodInterceptor;
import org.zanata.async.AsyncTaskHandle;
import org.zanata.async.AsyncTaskHandleManager;
import org.zanata.bean.AsyncMethodsBean;
import org.zanata.bean.SessionStorageBean;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({AsyncMethodInterceptor.class, WeldContextControl.class})
@SupportDeltaspikeCore
//@Ignore("cdi-unit race condition in ContextController")
public class AsyncMethodsBeanTest {

    @Inject
    AsyncMethodsBean asyncBean;

    @Inject
    SessionStorageBean storageBean;

    @Inject
    AsyncTaskHandleManager handleManager;

    @Produces
    public HttpServletRequest getRequest() {
        return new MockHttpServletRequestImpl();
    }

    @Test
    public void testAsync() throws Exception {
        Future<String> future = asyncBean.longWindedString("Carlos");
        assert !future.isDone();
        assert future.get().length() > 0;
    }

    @Ignore("sessions/requests are ThreadLocal")
    @Test
    @InRequestScope // Need to provide a request implementation (see above)
//    @InSessionScope
    public void testSessionIsShared() throws Exception {
        String str = "Stored String";
        storageBean.put("VALUE", str);
        assert storageBean.get("VALUE").equals(str);
    }

    @Test
    public void testHandleManagerRegistration() throws Exception {
        AsyncTaskHandle handle = handleManager.newTaskHandle();
        Future<String> result = asyncBean.getLongWindedString("Carlos", handle);
        // task shouldn't be done here
        assert !result.isDone();
        assert !handle.isDone();
        assert handleManager.getHandleById( handle.getId() ).equals( handle );
        // wait for task to finish
        assert result.get() != null;
        // task should be done here
        assert result.isDone();
        assert handle.isDone();
        assert handleManager.getHandleById( handle.getId() ).equals( handle );
    }
}
