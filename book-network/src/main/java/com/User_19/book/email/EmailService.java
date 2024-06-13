package com.User_19.book.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Async
    public void sendEmail(
            String to,
            String username,
            EmailTemplate emailTemplate,
            String confirmationUrl,
            String activationCode,
            String subject
    ) throws MessagingException {
        String templateName;
        if(emailTemplate==null){
            templateName="confirm-email";

        }else{
            templateName=emailTemplate.name();
        }
        MimeMessage mimeMessage=javaMailSender.createMimeMessage();
        MimeMessageHelper helper= new MimeMessageHelper(
                mimeMessage,
                MimeMessageHelper.MULTIPART_MODE_MIXED,
                StandardCharsets.UTF_8.name()

        );
        Map<String,Object> prop= new HashMap<>();
        prop.put("username",username);
        prop.put("confirmationUrl",confirmationUrl);
        prop.put("activation_code",activationCode);
        Context context= new Context();
        context.setVariables(prop);
        helper.setFrom("contact@user19.com");
        helper.setTo(to);
        helper.setSubject(subject);
        String template = springTemplateEngine.process(templateName,context);
        helper.setText(template,true);
        javaMailSender.send(mimeMessage);

    }
}
