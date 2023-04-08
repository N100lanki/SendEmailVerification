package EmailVarification.UserService;

import EmailVarification.Entity.User;
import EmailVarification.Registration.RegisterRequest;
import EmailVarification.Registration.RegistrationTokenEntity.VerificationToken;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    List<User> getUser();
    User registerUser( RegisterRequest request);
    Optional<User> findBYEmail(String email);

    void saveUserVerificationToken(User theUser, String verificationToken);

    String validateToken(String theToken);

    VerificationToken generateNewToken(String oldToken);

}
