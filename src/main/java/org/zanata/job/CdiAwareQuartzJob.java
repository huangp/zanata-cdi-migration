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
import org.zanata.model.HPerson;
import org.zanata.security.annotations.Authenticated;
import org.zanata.servlet.ServletStuff;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@Scheduled(cronExpression = "* * * * * ?", onStartup = false)
public class CdiAwareQuartzJob implements org.quartz.Job {
    private static final Logger log =
            LoggerFactory.getLogger(CdiAwareQuartzJob.class);

    // scheduler by default starts request and session scope
//    @Inject
//    private SessionStorageBean bean;

//    @Inject
//    private ServletStuff servletStuff;

//    @Inject @Authenticated
//    private HPerson person;

    @Override
    public void execute(JobExecutionContext context) throws
            JobExecutionException {
        Date fireTime = context.getFireTime();
        log.info("=== >> firing job at: {}", fireTime);

//        log.info("request is null? {}", request.getContextPath());
//        log.info("session is null? {}", session.getId());
//        bean.put("haha", "hoho");

//        log.info("person: {}", person);
        log.info("=== >> job {} finished",  context.getJobDetail());
    }
}
