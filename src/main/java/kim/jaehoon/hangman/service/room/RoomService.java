package kim.jaehoon.hangman.service.room;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public interface RoomService {

    Mono findById(ObjectId id);

    Flux findAll();

}
