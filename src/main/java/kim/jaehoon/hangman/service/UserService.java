package kim.jaehoon.hangman.service;

import kim.jaehoon.hangman.domain.SignUp;
import kim.jaehoon.hangman.domain.TokenResponse;
import kim.jaehoon.hangman.domain.User;
import kim.jaehoon.hangman.domain.UserRepository;
import kim.jaehoon.hangman.exception.InvalidUserCredentialException;
import kim.jaehoon.hangman.exception.UserAlreadyExistsException;
import kim.jaehoon.hangman.exception.UserNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private TokenService tokenService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public Mono createUser(SignUp signUp) {
        User user = signUp.toUser(passwordEncoder);
        return userRepository.findById(user.getId())
                .handle((dbUser, sink) -> {
                    sink.error(new UserAlreadyExistsException());
                }).switchIfEmpty(Mono.fromRunnable(() -> {
                    userRepository.save(user).subscribe();
                }));
    }

    public Mono login(String id, String password) {
        return userRepository.findById(id)
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.just(new TokenResponse(tokenService.createAccessToken(id)));
                    } else {
                        return Mono.error(new InvalidUserCredentialException());
                    }
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }

    public Mono findById(String id) {
        return userRepository.findById(id)
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }
}
