package EmailVarification.Registration.RegistrationTokenEntity;

import EmailVarification.Entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;


@Entity
@Setter
@Getter
@NoArgsConstructor
public class VerificationToken {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
private String token;
private Date expirationTime;

private  static final  int EXPIRATION_TIME= 1;
@OneToOne
@JoinColumn(name = "user_id")
private User user;



    public VerificationToken(String token, User user) {
        this.token = token;
        this.user = user;
        this.expirationTime=this.getTokenExpirationTime();
    }

    private Date getTokenExpirationTime() {

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE,EXPIRATION_TIME);
                return  new Date(calendar.getTime().getTime());
    }
}
