package EmailVarification.SecurityConfigration;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class RegistrationSecurity {

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return  http.cors().and().csrf().disable()
                .authorizeRequests()
                .requestMatchers("/register/**")
                .permitAll()
                .and()
                .authorizeRequests()
                .requestMatchers("/users/**").
                hasAnyAuthority("USER","ADMIN")
                .and()
                .formLogin()
                .and().     build();
    }

}
