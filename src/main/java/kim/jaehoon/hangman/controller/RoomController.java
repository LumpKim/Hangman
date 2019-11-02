package kim.jaehoon.hangman.controller;

import kim.jaehoon.hangman.domain.entity.User;
import kim.jaehoon.hangman.domain.payload.Answer;
import kim.jaehoon.hangman.domain.payload.CreateRoomInfo;
import kim.jaehoon.hangman.service.room.RoomService;
import kim.jaehoon.hangman.service.token.TokenService;
import kim.jaehoon.hangman.service.user.UserService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/v1")
public class RoomController {

    @Autowired
    TokenService tokenService;

    @Autowired
    RoomService roomService;

    @Autowired
    UserService userService;

    @GetMapping("/rooms")
    public Flux findAll() {
        return roomService.findAll();
    }

    @PostMapping("/room")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono createRoom(@NotNull @RequestHeader("Authorization") String auth, @RequestBody CreateRoomInfo roomInfo) {
        String adminId = tokenService.getIdentity(auth.replace("Bearer ", ""));
        return roomService.save(adminId, roomInfo.getName(), roomInfo.getMaxPlayer());
    }

    @GetMapping("/room/{roomId}")
    public Mono findById(@NotNull @PathVariable ObjectId roomId) {
        return roomService.findById(roomId);
    }

    @PutMapping("/room/{roomId}")
    public Flux<User> setAnswer(@NotNull @RequestHeader("Authorization") String auth, @PathVariable ObjectId roomId, @RequestBody Answer answer) {
        String adminId = tokenService.getIdentity(auth.replace("Bearer ", ""));
        return roomService.setAnswer(roomId, adminId, answer.getAnswer());
    }

    @PostMapping("/room/{roomId}/participant")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono setParticipant(@NotNull @RequestHeader("Authorization") String auth, @PathVariable ObjectId roomId) {
        String userId = tokenService.getIdentity(auth.replace("Bearer ", ""));
        return roomService.setParticipant(roomId, userId);
    }

    @PutMapping("/room/{roomId}/participant")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono setReady(@RequestHeader("Authorization") String auth, @PathVariable ObjectId roomId) {
        String userId = tokenService.getIdentity(auth.replace("Bearer ", ""));
        return roomService.setReady(roomId, userId);
    }

    @DeleteMapping("/room/{roomId}/participant")
    @ResponseStatus(HttpStatus.OK)
    public Mono removeParticipant(@NotNull @RequestHeader("Authorization") String auth, @PathVariable ObjectId roomId) {
        String userId = tokenService.getIdentity(auth.replace("Bearer ", ""));
        return roomService.removeParticipant(roomId, userId);
    }

    @PatchMapping("/room/{roomId}/answer")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public Mono submitAnswer(@RequestHeader("Authorization") String auth, @PathVariable ObjectId roomId, @RequestBody Answer answer) {
        String userId = tokenService.getIdentity(auth.replace("Bearer ", ""));
        return roomService.submitAnswer(roomId, userId, answer.getAnswer());
    }
}
