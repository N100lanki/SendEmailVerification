package EmailVarification.UserService;

import EmailVarification.Entity.User;
import EmailVarification.Exception.UserAlreadyExistException;
import EmailVarification.Registration.RegisterRequest;
import EmailVarification.Registration.RegistrationTokenEntity.VerificationToken;
import EmailVarification.Registration.RegistrationTokenEntity.VerificationTokenRepository;
import EmailVarification.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService  implements  IUserService{

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private  final VerificationTokenRepository TokenRepository;

    @Override
    public List<User> getUser() {
        return userRepository.findAll();
    }

    @Override
    public User registerUser(RegisterRequest request) {
        Optional<User> user =this.findBYEmail(request.email());
        if (user.isPresent()){

            throw  new UserAlreadyExistException("User Already Registered"+request.email());
        }
        var newuser= new  User();
        newuser.setFirstname(request.firstname());
        newuser.setLastname(request.lastname());
        newuser.setEmail(request.email());
        newuser.setEmail(request.email());
        newuser.setRole(request.role());
        newuser.setPassword(passwordEncoder.encode(request.password()));

        return userRepository.save(newuser);
    }

    @Override
    public Optional<User> findBYEmail(String email) {
        return Optional.empty();
    }

    @Override
    public void saveUserVerificationToken(User theUser, String token) {

        var verificationToken = new VerificationToken( token,theUser);
        TokenRepository.save(verificationToken);

    }

    @Override
    public String validateToken(String theToken) {
        VerificationToken token = TokenRepository.findByToken(theToken);
        if (token==null){
            return  "Invalid Token";

        }
        User user = token.getUser();
        Calendar calendar=Calendar.getInstance();
        if ((token.getExpirationTime().getTime()-calendar.getTime().getTime())<=0){

            TokenRepository.delete(token);
            return "Token expired";
        }
        user.setEnabled(true);
        userRepository.save(user);

        return "valid";
    }

    @Override
    public VerificationToken generateNewToken(String oldToken) {
         VerificationToken verificationToken=TokenRepository.findByToken(oldToken);
        var verificationTokenTime=new VerificationToken();
         verificationToken.setToken(UUID.randomUUID().toString());
         verificationToken.setExpirationTime(verificationTokenTime.getExpirationTime());
         return TokenRepository.save(verificationToken);

    }


}
