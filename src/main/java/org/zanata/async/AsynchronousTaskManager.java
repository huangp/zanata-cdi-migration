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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import javax.annotation.Nonnull;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.SessionScoped;

import lombok.extern.slf4j.Slf4j;

import org.apache.deltaspike.cdise.api.ContextControl;
import org.apache.deltaspike.core.api.provider.BeanProvider;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@ApplicationScoped
@Slf4j
public class AsynchronousTaskManager {

    private ExecutorService scheduler;

    @PostConstruct
    public void init() {
        scheduler = Executors.newFixedThreadPool(3);
    }

    @PreDestroy
    public void cleanup() {
        scheduler.shutdown();
    }

    public <V> ListenableFuture<V> startTask(final @Nonnull AsyncTask<Future<V>> task) {

        final AsyncTaskResult<V> taskFuture = new AsyncTaskResult<V>();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                ContextControl ctxCtrl = null;
                try {
                    ctxCtrl = BeanProvider
                            .getContextualReference(ContextControl.class);
                    ctxCtrl.startContexts();
                    // TODO Security context. Make sure the new thread has the same
                    // security identity as the one that created it.

                    Future<V> returnValue = task.call();
                    taskFuture.set(returnValue.get());
                }
                catch(Throwable t) {
                    taskFuture.setException(t);
                    // TODO log unimportant exceptions as WARN
                    // but make sure this doesn't lead to unlogged 500 errors
                    log.error(
                            "Exception when executing an asynchronous task.", t);
                }
                finally {
                    // stop the started contexts to ensure that all scoped
                    // beans get cleaned up.
                    if(ctxCtrl != null) {
                        ctxCtrl.stopContexts();
                    }
                }
            }
        };
        scheduler.execute(runnable);
        return taskFuture;
    }

}
