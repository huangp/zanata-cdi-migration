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
package org.zanata.async;

import org.apache.deltaspike.core.api.provider.BeanProvider;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Async
@Interceptor
public class AsyncMethodInterceptor {

    @Inject
    private AsynchronousTaskManager executor;

    static final ThreadLocal<Boolean> interceptorRan = new ThreadLocal<Boolean>();

    @AroundInvoke
    public Object callAsync(final InvocationContext ctx) throws Exception {

        if (interceptorRan.get() == null) {
            AsyncTask asyncTask = new AsyncTask() {
                @Override
                public Object call() throws Exception {
                    interceptorRan.set(true);
                    try {
                        Object target =
                                BeanProvider.getContextualReference(ctx.getMethod().getDeclaringClass(), false);
                        return ctx.getMethod().invoke(target, ctx.getParameters());
                    }
                    finally {
                        interceptorRan.remove();
                    }
                }
            };

            return executor.startTask(asyncTask);
            // Async methods should return ListenableFuture
        } else {
            return ctx.proceed();
        }
    }
}
