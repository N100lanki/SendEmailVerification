package EmailVarification.Event.Listener;

import EmailVarification.Entity.User;
import EmailVarification.Event.RegistrationCompleteEvent;
import EmailVarification.UserService.UserService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.UUID;
@Slf4j
@Component
@RequiredArgsConstructor
public class RegistrationCompleteListener  implements  ApplicationListener<RegistrationCompleteEvent> {
    private  final UserService userService;
    private final JavaMailSender javaMailSender;
    private  User theUser;


    @Override
    public void onApplicationEvent(RegistrationCompleteEvent event) {
//        1.get newly register user

         theUser = event.getUser();
//        2.create a verification token  for the user

        String verificationToken= UUID.randomUUID().toString();
//        3.save the verification token
        userService.saveUserVerificationToken( theUser ,verificationToken);
//        4.build a verification URL to be sent to user

        String url=event.getApplicationurl()+"/register/verifyEmail?token="+verificationToken;
//        5.send the email
        try {
            sendVerificationEmail(url);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Click to verify your email:{} " ,url);

    }

    public void sendVerificationEmail(String url) throws MessagingException, UnsupportedEncodingException {
        String subject="Email Verification";
        String sender="You are Going to hacked..!";
        String mailContent = "<p> Hi, "+ theUser.getFirstname()+ ", </p>"+
                "<p>Thank you for registering with us,"+"" +
                "Please, follow the link below to complete your registration.</p>"+
                "<a href=\"" +url+ "\">Verify your email to activate your account</a>"+
                "<p> Thank you <br> Users Registration Portal Service";
        MimeMessage message = javaMailSender.createMimeMessage();
        var messageHelper = new MimeMessageHelper(message);
        messageHelper.setFrom("niranjan100lanki@gmail.com", sender);
        messageHelper.setTo(theUser.getEmail());
        messageHelper.setSubject(subject);
        messageHelper.setText(mailContent, true);
        javaMailSender.send(message);


    }
}
