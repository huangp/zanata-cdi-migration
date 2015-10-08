package org.zanata.email;

import java.io.UnsupportedEncodingException;
import java.net.ConnectException;

import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.common.base.Throwables;

/**
 * @author Patrick Huang
 *         <a href="mailto:pahuang@redhat.com">pahuang@redhat.com</a>
 */
@ApplicationScoped
@Named
public class EmailSender {
    @Resource(lookup = "jboss/mail/Default")
    private Session session;

    public void send() throws UnsupportedEncodingException {
        try {
            MimeMessage email = new MimeMessage(session);
            email.setFrom(new InternetAddress("from@example.com", "pahuang"));
            email.addRecipients(Message.RecipientType.TO, "pahuang@redhat.com");
            email.setSubject("test mail from cdi-security");
            Multipart mp = new MimeMultipart("alternative");

            MimeBodyPart textPart = new MimeBodyPart();
            String text = "hello";
            textPart.setText(text, "UTF-8");
            mp.addBodyPart(textPart);

            MimeBodyPart htmlPart = new MimeBodyPart();
            htmlPart.setContent("<h1>hello</h1>", "text/html; charset=UTF-8");
            mp.addBodyPart(htmlPart);

            email.setContent(mp);
            Transport.send(email);
        } catch (MessagingException e) {
            Throwable rootCause = Throwables.getRootCause(e);
            if (rootCause.getClass().equals(ConnectException.class) && rootCause.getMessage().equals("Connection refused")) {
                throw new RuntimeException("The system failed to connect to mail service. Please contact the administrator!", e);
            }
            throw new RuntimeException(e);
        }
    }
}
