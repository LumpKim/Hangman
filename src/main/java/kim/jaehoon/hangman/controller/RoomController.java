package kim.jaehoon.hangman.controller;

import kim.jaehoon.hangman.service.room.RoomService;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1")
public class RoomController {

    @Autowired
    RoomService roomService;

    @GetMapping("/room/{id}")
    public Mono findById(@PathVariable ObjectId id) {
        return roomService.findById(id);
    }

    @GetMapping("/rooms")
    public Flux findAll() {
        return roomService.findAll();
    }

}
