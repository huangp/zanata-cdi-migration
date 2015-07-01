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

import com.google.common.base.Optional;
import com.google.common.util.concurrent.ListenableFuture;
import org.zanata.util.BeanHolder;
import org.zanata.util.ServiceLocator;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

/**
 * TODO Constructor injection did not seem to work on interceptors
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@Async
@Interceptor
public class AsyncMethodInterceptor {

    static final ThreadLocal<Boolean> interceptorRan =
            new ThreadLocal<Boolean>();
    @Inject
    private AsynchronousTaskManager taskManager;

    @Inject
    private AsyncTaskHandleManager taskHandleManager;

//    AsyncMethodInterceptor() {}

//    @Inject
//    AsyncMethodInterceptor(AsynchronousTaskManager taskManager, AsyncTaskHandleManager taskHandleManager) {
//        this.taskManager = taskManager;
//        this.taskHandleManager = taskHandleManager;
//    }

    @AroundInvoke
    public Object callAsync(final InvocationContext ctx) throws Exception {

        if (interceptorRan.get() == null) {
            // If there is a Task handle parameter (only the first one will be
            // registered)
            final Optional<AsyncTaskHandle> handle =
                    Optional.fromNullable(findHandleIfPresent(ctx
                            .getParameters()));

            AsyncTask asyncTask = new AsyncTask() {
                @Override
                public Object call() throws Exception {
                    interceptorRan.set(true);
                    try {
                        if( handle.isPresent() ) {
                            handle.get().startTiming();
                        }
                        Class<?> beanClass = ctx.getMethod().getDeclaringClass();

                        // TODO [CDI] handle qualifiers?
                        try (BeanHolder<?> bean = ServiceLocator.instance()
                                .getDependent(beanClass)) {
                            return ctx.getMethod().invoke(bean.get(),
                                    ctx.getParameters());
                        }
                    }
                    finally {
                        interceptorRan.remove();
                        if (handle.isPresent()) {
                            handle.get().finishTiming();
                            taskHandleManager.taskFinished(handle.get());
                        }
                    }
                }
            };

            ListenableFuture<Object> futureResult =
                    taskManager.startTask(asyncTask);
            if( handle.isPresent() ) {
                handle.get().setFutureResult(futureResult);
            }
            return futureResult;
            // Async methods should return ListenableFuture
        } else {
            return ctx.proceed();
        }
    }

    private AsyncTaskHandle findHandleIfPresent(Object[] params) {
        for (Object param : params) {
            if (param instanceof AsyncTaskHandle) {
                return (AsyncTaskHandle) param;
            }
        }
        return null;
    }
}
