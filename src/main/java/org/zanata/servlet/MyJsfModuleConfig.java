package org.zanata.servlet;

import org.apache.deltaspike.jsf.api.config.JsfModuleConfig;
import org.apache.deltaspike.jsf.spi.scope.window.ClientWindowConfig;

import javax.enterprise.inject.Specializes;

@Specializes
public class MyJsfModuleConfig extends JsfModuleConfig
{
    @Override
    public ClientWindowConfig.ClientWindowRenderMode getDefaultWindowMode()
    {
        // TODO use NONE for bots
        return ClientWindowConfig.ClientWindowRenderMode.CLIENTWINDOW;
    }
}
