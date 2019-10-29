package kim.jaehoon.hangman.domain;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.validation.constraints.Email;

@Data
@Getter @Setter
public class SignUp {

    @Email
    private String email;

    private String password;
    private String nickname;

    public User toUser(PasswordEncoder passwordEncoder) {
        return new User(new ObjectId(), email, passwordEncoder.encode(password), nickname);
    }

}
