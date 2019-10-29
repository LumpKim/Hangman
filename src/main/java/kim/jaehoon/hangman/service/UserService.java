package kim.jaehoon.hangman.service;

import kim.jaehoon.hangman.domain.SignUp;
import kim.jaehoon.hangman.domain.TokenResponse;
import kim.jaehoon.hangman.domain.User;
import kim.jaehoon.hangman.domain.UserRepository;
import kim.jaehoon.hangman.exception.InvalidUserCredentialException;
import kim.jaehoon.hangman.exception.UserAlreadyExistsException;
import kim.jaehoon.hangman.exception.UserNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserService {

    private static final Logger logger = LogManager.getLogger(UserService.class);

    private TokenService tokenService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserService(UserRepository userRepository, TokenService tokenService) {
        this.tokenService = tokenService;
        this.userRepository = userRepository;
    }

    public Mono createUser(SignUp signUp) {
        User user = signUp.toUser(passwordEncoder);
        return userRepository.findByEmail(user.getEmail())
                .handle((dbUser, sink) -> {
                    sink.error(new UserAlreadyExistsException());
                }).switchIfEmpty(Mono.fromRunnable(() -> {
                    userRepository.save(user).subscribe();
                }));
    }

    public Mono login(String email, String password) {
        return userRepository.findByEmail(email)
                .flatMap(user -> {
                    if (passwordEncoder.matches(password, user.getPassword())) {
                        return Mono.just(new TokenResponse(tokenService.createAccessToken(email), tokenService.createRefreshToken(email)));
                    } else {
                        return Mono.error(new InvalidUserCredentialException());
                    }
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException()));
    }
}
