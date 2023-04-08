package EmailVarification.Registration;


import EmailVarification.Entity.User;
import EmailVarification.Event.Listener.RegistrationCompleteListener;
import EmailVarification.Event.RegistrationCompleteEvent;
import EmailVarification.Registration.RegistrationTokenEntity.VerificationToken;
import EmailVarification.Registration.RegistrationTokenEntity.VerificationTokenRepository;
import EmailVarification.UserService.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.bind.annotation.*;
import java.io.UnsupportedEncodingException;


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/register")
public class RegistrationController {


    private final UserService userService;

    private  final VerificationTokenRepository tokenRepository;

    private final ApplicationEventPublisher publisher;

    private final RegistrationCompleteListener listener;

    private final HttpServletRequest servletRequest;



//    Registering A new user   ........................
    @PostMapping
    public String registerUser(@RequestBody  RegisterRequest registerRequest, final HttpServletRequest request ){

        User user = userService.registerUser(registerRequest);

//        publish a registration event
        publisher.publishEvent(new RegistrationCompleteEvent(user,applicationUrl(request)));
        return  "Please check your email for confirm registration";


    }

//  Verifying user by sent them a email
    @GetMapping("/verifyEmail")
    public String verifyEmail(@RequestParam("token") String token){
        String url=applicationUrl(servletRequest)+"/register/resend?token="+token;  // this is written for resend the verification LINK
        VerificationToken theToken = tokenRepository.findByToken(token);
        if (theToken.getUser().isEnabled()){
            return "This account has already been verified, please, login.";
        }
        String verificationResult = userService.validateToken(token);
        if(verificationResult.equalsIgnoreCase("valid")){
            return "Email verified successfull,now you can login ";
        }

        return "Invalid verification token,<a href=\""+url+"\"> Click to get a new verification Link</a>";

    }
    @GetMapping("/resend")
    public String resendVerificationToken( @RequestParam("token") String oldToken ,final  HttpServletRequest request) throws MessagingException, UnsupportedEncodingException {

        VerificationToken verificationToken=userService.generateNewToken(oldToken);
        User theUser =verificationToken.getUser();
        resendVerificationToken(theUser,applicationUrl(request),verificationToken);
        return "A new verification link has sent ";
    }

    private void resendVerificationToken(User theUser, String applicationUrl, VerificationToken verificationToken) throws MessagingException, UnsupportedEncodingException {

        String url=applicationUrl+"/register/verifyEmail?token="+verificationToken.getToken();
        listener.sendVerificationEmail(url);
        log.info("Click to verify your email:{} " ,url);
    }

    public String applicationUrl(HttpServletRequest request) {
        return  "http://"+request.getServerName() + ":"+request.getServerPort() + ":"+request.getContextPath();
    }

}
