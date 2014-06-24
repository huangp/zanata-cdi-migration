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
package org.zanata.test;

import org.apache.deltaspike.cdise.api.CdiContainer;
import org.apache.deltaspike.cdise.api.CdiContainerLoader;
import org.apache.deltaspike.core.api.projectstage.ProjectStage;
import org.apache.deltaspike.core.util.ProjectStageProducer;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.AnnotatedType;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.InjectionTarget;

/**
 * Base class for unit tests that need to use CDI beans. This class should serve
 * as a replacement for SeamAutowire tests
 * 
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class CdiTest {
    protected static CdiContainer cdiContainer;

    @Before
    public final void setupContainer() throws Exception {
        if (cdiContainer == null) {
            ProjectStageProducer.setProjectStage(ProjectStage.UnitTest);

            cdiContainer = CdiContainerLoader.getCdiContainer();
            cdiContainer.boot();
            cdiContainer.getContextControl().startContexts();
        }
        else {
            // clean the Instances by restarting the contexts
            cdiContainer.getContextControl().stopContexts();
            cdiContainer.getContextControl().startContexts();
        }
    }

    @After
    public final void resetContainerScopes() throws Exception {
        if (cdiContainer != null) {
            cdiContainer.getContextControl().stopContext(RequestScoped.class);
            cdiContainer.getContextControl().startContext(RequestScoped.class);
        }
    }

    @Before
    public final void injectBeans() throws Exception {
        setupContainer();
        cdiContainer.getContextControl().stopContext(RequestScoped.class);
        cdiContainer.getContextControl().startContext(RequestScoped.class);

        // perform injection into the very own test class
        BeanManager beanManager = cdiContainer.getBeanManager();

        CreationalContext creationalContext = beanManager.createCreationalContext(null);

        AnnotatedType annotatedType = beanManager.createAnnotatedType(this.getClass());
        InjectionTarget injectionTarget = beanManager.createInjectionTarget(annotatedType);
        injectionTarget.inject(this, creationalContext);
    }

    //@AfterSuite
    // TODO Need to figure out how to do AfterSuite in JUnit
    public synchronized void shutdownContainer() throws Exception {
        if (cdiContainer != null) {
            cdiContainer.shutdown();
            cdiContainer = null;
        }
    }
}
