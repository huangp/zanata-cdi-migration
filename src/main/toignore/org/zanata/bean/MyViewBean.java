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

import java.io.Serializable;

import javax.faces.bean.ViewScoped;
import javax.inject.Named;
import javax.validation.constraints.Min;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;

import org.apache.deltaspike.core.api.scope.ViewAccessScoped;
import org.zanata.security.annotations.CheckRole;
import org.zanata.security.annotations.CheckLoggedIn;

/**
 * @author Carlos Munoz <a href="mailto:camunoz@redhat.com">camunoz@redhat.com</a>
 */
//@ViewAccessScoped
@ViewScoped
@Named
public class MyViewBean implements Serializable {

    public void printName() {
        System.out.println("View bean: " + this.toString());
    }

    @Getter
    @Setter
    @Size(min = 5, max = 10)
    private String name;

    @Getter
    @Setter
    @Min(5)
    private int age;


    @CheckLoggedIn
    public String getMessage() {
        return "You are logged in";
    }

    @CheckRole("admin")
    public void doSomethingAuthorized() {
        // Should fail if the right role is not contained
    }

    @CheckRole("dodgyuser")
    public void doSomethingUnauthorized() {
        // Should fail if the right role is not contained
    }

    public String explicitlyNavigateToPage2() {
        return null;
    }
}
