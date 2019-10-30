package kim.jaehoon.hangman.controller;

import kim.jaehoon.hangman.domain.payload.Login;
import kim.jaehoon.hangman.domain.payload.SignUp;
import kim.jaehoon.hangman.service.user.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    @Autowired
    UserServiceImpl userService;

    @PostMapping("/signup")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono signUp(@RequestBody @NotNull SignUp signUp) {
        return userService.createUser(signUp);
    }

    @PostMapping("/auth")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono login(@RequestBody @NotNull Login login) {
        return userService.login(login.getId(), login.getPassword());
    }

    @GetMapping("/{id}")
    public Mono findUserInfo(@PathVariable String id) {
        return userService.findById(id);
    }
}
