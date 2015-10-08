package org.zanata.servlet;

import java.util.Set;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.deltaspike.core.api.common.DeltaSpike;
import org.apache.deltaspike.core.api.provider.BeanManagerProvider;
import org.apache.deltaspike.core.api.provider.BeanProvider;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Dependent
public class ServletStuff {
    @Inject
    @DeltaSpike
    private HttpServletRequest request;

    @Inject
    @DeltaSpike HttpSession session;


    public HttpSession getSession() {
        HttpSession session = BeanProvider
                .getContextualReference(HttpSession.class,
                        new AnnotationLiteral<DeltaSpike>() {
                        });
        return session;
    }

    public HttpServletRequest getRequest() {
        HttpServletRequest request = BeanProvider
                .getContextualReference(HttpServletRequest.class,
                        new AnnotationLiteral<DeltaSpike>() {
                        });
        return request;
    }

    public HttpSession getInjectedSession() {
        return session;
    }

    public HttpServletRequest getInjectedRequest() {
        return request;
    }
}
