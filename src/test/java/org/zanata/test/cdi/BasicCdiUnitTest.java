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
package org.zanata.test.cdi;

import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.zanata.test.bean.CoffeeBean;
import org.zanata.test.bean.TreeBean;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
@RunWith(CdiRunner.class)
public class BasicCdiUnitTest {

    @Produces
    @Mock
    private CoffeeBean coffeeBean;

    @Inject
    private TreeBean treeBean;

    @Test
    public void testMockedBean() throws Exception {
        Mockito.when(coffeeBean.getName()).thenReturn("Custom name");
        assert treeBean.getCoffeeBean().getName().equals("Custom name");
    }
}
