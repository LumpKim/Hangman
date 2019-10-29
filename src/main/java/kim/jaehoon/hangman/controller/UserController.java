package kim.jaehoon.hangman.controller;

import kim.jaehoon.hangman.domain.Login;
import kim.jaehoon.hangman.domain.SignUp;
import kim.jaehoon.hangman.domain.TokenResponse;
import kim.jaehoon.hangman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono signUp(@RequestBody SignUp signUp) {
        return userService.createUser(signUp);
    }

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono login(@RequestBody @NotNull Login login) {
        return userService.login(login.getEmail(), login.getPassword());
    }
}
