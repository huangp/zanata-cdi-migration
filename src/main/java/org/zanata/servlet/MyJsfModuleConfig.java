package org.zanata.servlet;

import org.apache.deltaspike.jsf.api.config.JsfModuleConfig;
import org.apache.deltaspike.jsf.spi.scope.window.ClientWindowConfig;

import javax.enterprise.inject.Specializes;
import javax.enterprise.util.Nonbinding;

@Specializes
public class MyJsfModuleConfig extends JsfModuleConfig
{
    @Override
    public ClientWindowConfig.ClientWindowRenderMode getDefaultWindowMode()
    {
        // TODO use NONE for bots
        // TODO changing it to CLIENTWINDOW will throw:
        // org.jboss.weld.context.ContextNotActiveException: WELD-001303 No active contexts for scope type org.apache.deltaspike.core.api.scope.WindowScoped
        //      org.jboss.weld.manager.BeanManagerImpl.getContext(BeanManagerImpl.java:608)
        return ClientWindowConfig.ClientWindowRenderMode.LAZY;
    }
}
