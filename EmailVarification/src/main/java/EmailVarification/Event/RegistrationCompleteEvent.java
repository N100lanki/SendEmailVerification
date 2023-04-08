package EmailVarification.Event;

import EmailVarification.Entity.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Setter
@Getter
public class RegistrationCompleteEvent extends ApplicationEvent {

    private User user;
    private  String applicationurl;

    public RegistrationCompleteEvent( User user,String applicationurl) {
        super(user);
        this.user = user;
        this.applicationurl = applicationurl;

    }
}
