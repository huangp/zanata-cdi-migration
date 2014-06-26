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

import com.google.common.util.concurrent.ListenableFuture;
import org.zanata.async.Async;
import org.zanata.async.AsyncTaskHandle;
import org.zanata.async.AsyncTaskResult;

import javax.inject.Inject;
import java.util.concurrent.Future;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class AsyncMethodsBean {

    private SessionStorageBean sessionStorageBean;

    @Inject
    public AsyncMethodsBean(SessionStorageBean sessionStorageBean) {
        this.sessionStorageBean = sessionStorageBean;
    }

    @Async
    public ListenableFuture<String> longWindedString(String name) {
        for(int i =0; i<10; i++) {
            try {
                Thread.sleep(1000);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Sleeping... zzz");
        }
        return new AsyncTaskResult<String>("Hello " + name + "! I slept for 10 seconds");
    }

    @Async
    public ListenableFuture<String> getSessionStoredValue() {
        return new AsyncTaskResult<String>((String)sessionStorageBean.get("VALUE"));
    }

    @Async
    public ListenableFuture<String> getLongWindedString(String name, AsyncTaskHandle handle) {
        return this.longWindedString(name);
    }

}
