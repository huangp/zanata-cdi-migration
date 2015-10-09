package org.zanata.job;

import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.deltaspike.core.api.provider.BeanProvider;
import org.apache.deltaspike.scheduler.api.Scheduled;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zanata.bean.SessionStorageBean;
import org.zanata.servlet.ServletStuff;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Scheduled(cronExpression = "* * * * * ?", onStartup = true, startScopes = {})
public class CdiAwareQuartzJob implements org.quartz.Job {
    private static final Logger log =
            LoggerFactory.getLogger(CdiAwareQuartzJob.class);

    // scheduler by default starts request and session scope
//    @Inject
//    private SessionStorageBean bean;

    @Inject
    private ServletStuff servletStuff;

    @Override
    public void execute(JobExecutionContext context) throws
            JobExecutionException {
        Date fireTime = context.getFireTime();
        log.info("=== >> firing job at: {}", fireTime);
        HttpServletRequest request = servletStuff.getRequest();
        HttpSession session = servletStuff.getSession();
        HttpServletRequest injectedRequest = servletStuff.getInjectedRequest();
        HttpSession injectedSession = servletStuff.getInjectedSession();
        boolean equal = request == injectedRequest && session == injectedSession;
        log.info("equal: {}", equal);

//        log.info("request is null? {}", request.getContextPath());
//        log.info("session is null? {}", session.getId());
//        bean.put("haha", "hoho");
        SessionStorageBean bean = BeanProvider
                .getContextualReference(SessionStorageBean.class, true);
        log.info("session bean should be null: {}", bean);

        log.info("=== >> job {} finished",  context.getJobDetail());
    }
}
