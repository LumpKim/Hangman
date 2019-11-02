package kim.jaehoon.hangman.service.user;

import kim.jaehoon.hangman.domain.entity.User;
import kim.jaehoon.hangman.domain.payload.SignUp;
import kim.jaehoon.hangman.domain.payload.TokenResponse;
import kim.jaehoon.hangman.domain.repository.UserRepository;
import kim.jaehoon.hangman.exception.InvalidUserCredentialException;
import kim.jaehoon.hangman.exception.UserAlreadyExistsException;
import kim.jaehoon.hangman.exception.UserNotFoundException;
import kim.jaehoon.hangman.service.token.TokenServiceImpl;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class UserServiceImpl implements UserService {

    private TokenServiceImpl tokenService;
    private UserRepository userRepository;
    private PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();

    public UserServiceImpl(UserRepository userRepository, TokenServiceImpl tokenService) {
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

    @Override
    public Flux findAll() {
        return userRepository.findTop5ByOrderByLevelDesc();
    }
}
