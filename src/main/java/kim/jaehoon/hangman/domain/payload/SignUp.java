package kim.jaehoon.hangman.domain.payload;

import kim.jaehoon.hangman.domain.entity.User;
import kim.jaehoon.hangman.domain.entity.WinningRate;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.NotNull;

@Data
@Getter @Setter
public class SignUp {

    @NotNull
    private String id;

    @NotNull
    private String password;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(id, passwordEncoder.encode(password), 0, false,  new WinningRate(0, 0, (float) 0.0), null);
    }

}
