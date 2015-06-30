/*
 * Copyright 2010, Red Hat, Inc. and individual contributors as indicated by the
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
package org.zanata.util;


import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.seam.SeamAutowire;

import java.lang.annotation.Annotation;
import javax.inject.Named;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/**
 * Replacement class for our ServiceLocator. Tests that use
 * the {@link SeamAutowire} class will use this class instead of the real one to
 * request components.
 *
 * @author Carlos Munoz <a
 *         href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
public class AutowireLocator implements IServiceLocator {
    private static final Logger log =
            LoggerFactory.getLogger(AutowireLocator.class);

    public static IServiceLocator instance() {
        if (SeamAutowire.useRealServiceLocator) {
            return ServiceLocator.INSTANCE;
        } else {
            return new AutowireLocator();
        }

    }

    public <T> DependentBean<T> getDependent(String name, Class<T> clazz) {
        return new DependentBean<T>(getInstance(name, clazz));
    }

    public <T> DependentBean<T> getDependent(Class<T> clazz, Annotation... qualifiers) {
        return new DependentBean<T>(getInstance(clazz, qualifiers));
    }

    public <T> T getInstance(String name, Class<T> clazz) {
        return (T) SeamAutowire.instance().getComponent(name);
    }

    public <T> T getInstance(Class<T> clazz, Annotation... qualifiers) {
        if (qualifiers.length != 0) {
            log.warn(
                    "qualifier will be ignored in SeamAutowire test. Class:{}, Qualifiers:{}",
                    clazz,
                    Lists.newArrayList(qualifiers));
        }
        Named annotation = clazz.getAnnotation(Named.class);
        if (annotation != null) {
            String name = annotation.value();
            if (Strings.isNullOrEmpty(name)) {
                return getInstance(
                        StringUtils.uncapitalize(clazz.getSimpleName()), clazz);
            }
            return getInstance(name, clazz);
        }
        throw new UnsupportedOperationException("don't support look up by qualifiers only");
    }

    public <T> T getInstance(String name, Object scope, Class<T> clazz) {
        return getInstance(name, clazz);
    }

    @Override
    public EntityManager getEntityManager() {
        return getInstance("entityManager", EntityManager.class);
    }

    @Override
    public EntityManagerFactory getEntityManagerFactory() {
        return getInstance("entityManagerFactory", EntityManagerFactory.class);
    }

    @Override
    public <T> T getJndiComponent(String jndiName, Class<T> clazz)
            throws NamingException {
        return getInstance(jndiName, clazz);
    }

    //    public static Object getInstance(String name, ScopeType scope,
//            boolean create, boolean allowAutocreation) {
//        return SeamAutowire.instance().getComponent(name);
//    }
//
//    public static Object getInstance(Class<?> clazz, ScopeType scopeType) {
//        return SeamAutowire.instance().autowire(clazz);
//    }
//
//    private static Object getInstance(Class<?> clazz) {
//        return SeamAutowire.instance().autowire(clazz);
//    }
//
//    public static Object getInstance(String name, boolean create,
//            boolean allowAutoCreation, Object result, ScopeType scopeType) {
//        return SeamAutowire.instance().getComponent(name);
//    }

}
