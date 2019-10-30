package kim.jaehoon.hangman.service.user;

import kim.jaehoon.hangman.domain.payload.SignUp;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public interface UserService {

    Mono createUser(SignUp signUp);

    Mono login(String id, String password);

    Mono findById(String id);

}
