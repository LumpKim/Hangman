package kim.jaehoon.hangman.domain.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Login {

    private String id;
    private String password;

}
